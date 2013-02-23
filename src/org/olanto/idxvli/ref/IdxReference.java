/**
 * ********
 * Copyright © 2010-2012 Olanto Foundation Geneva
 *
 * This file is part of myCAT.
 *
 * myCAT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.idxvli.ref;

import java.io.*;
import java.util.*;
import static org.olanto.idxvli.ql.QueryOperator.*;

import org.olanto.idxvli.*;
import org.olanto.idxvli.ref.stat.ReferenceStatistic;
import org.olanto.idxvli.server.REFResultNice;
import org.olanto.idxvli.util.SetOfBits;
import org.olanto.util.Timer;
import static org.olanto.idxvli.IdxConstant.*;

/**
 * Une classe pour référencer un string (pour la version de référence).
 *
 *
 * to do: - l'utilisation de SparseBitSet[] peut etre remplacée par des
 * opérations sur les vecteurs de documents directement
 *
 * modif 2012.4.28 permettre de faire des références sur 3,4,5
 *
 * - modification JG: SetOfBits (copy/reference)
 *
 */
public class IdxReference {

    public static final int MaxIndexedW = 1000000;
    public static final int NotIndexed = -1;
    static final int MaxMark = 3;
    static final int NoMark = -1;
    private int currentMark = 0;
    private int seqmax = 6;   // borne du seq
    private int seqn = 6 - 1;   // borne pour les boucles
    public int[] idxW;     // les  mots indexés (numéros)
    public String[] word;  // les mots tokenisés
    public int[] idxpos;   //les positions dans le documents
    private SparseBitSet[] doc;
    public int lastscan;
    public int totwordspacesep;
    protected int lastcp;
    private IdxStructure glue;
    private int[] idxcp;   // index sur le numéro dans le vecteur compacté
    private int[] cpW;     // les  mots indexés (numéros) compacté
    public int[] idxorig;   //l'index dans le vecteur non compacté
    private int[] begM, endM, docM;
    public List<String> docMultiRef;
    public List<String> txtRef;
    public List<String> txtRefOrigin;
    public int nbref;
    private int minlength;
    private SetOfBits sota;  // and of source and target
    private boolean alignsota;
    public int[] posRef;
    private String textforhtml;
    private String[] selectedCollection;
    private String uploadFileName;
    private String collectList;

    public IdxReference(IdxStructure _glue, String s, int min, String source, String target, boolean alignsota, String[] _selectedCollection) {
        Timer timing = new Timer("--------------------------------Total reference, size: " + s.length());
        glue = _glue;
        selectedCollection = _selectedCollection;
        collectList = "";
        if (selectedCollection != null) {
            for (int i = 0; i < selectedCollection.length; i++) {
                collectList += " " + selectedCollection[i].replace("COLLECTION.", "");
            }
        }
        lastscan = 0;
        minlength = min;
        if (minlength < 6) { // alors on utilise le seq3
            seqmax = 3;   // borne du seq
            int seqn = 3 - 1;   // borne pour les boucles
        }
        idxW = new int[MaxIndexedW];
        idxpos = new int[MaxIndexedW];
        word = new String[MaxIndexedW];
        this.alignsota = alignsota;
        if (alignsota) {// filtered by source and target
            sota = new SetOfBits(glue.docstable.satisfyThisProperty("SOURCE." + source)); // une copie pour le premier operande
//           System.out.println("so:"+sota.countTrue());
            SetOfBits ta = glue.docstable.satisfyThisProperty("TARGET." + target);
//            System.out.println("ta:"+ta.countTrue());
            sota.and(ta, SetOfBits.ALL);
//            System.out.println("sota:"+sota.countTrue());
        }
        if (alignsota && selectedCollection != null) {// étend le filtre aux collections
            SetOfBits colfilter = new SetOfBits(glue.docstable.satisfyThisProperty(selectedCollection[0])); // une copie pour le premier operande
            for (int i = 1; i < selectedCollection.length; i++) {
                SetOfBits col = glue.docstable.satisfyThisProperty(selectedCollection[i]);
                colfilter.or(col, SetOfBits.ALL);
            }
            sota.and(colfilter, SetOfBits.ALL);
        }
        Timer t = new Timer("Parsing");
        //       textforhtml = s.replace("&", "&amp;").replace("<", "&lt;").replace("\n", "<br/>")+ " EndOfDocument ."; // supprime les return
        textforhtml = "BeginOfDocument <br/>" + s.replace("<", "&lt;").replace("&nbsp;", " ") + " EndOfDocument ."; // &amp sont déjà insérer par le GUI
        textforhtml = addSpace(textforhtml);
        {  // calculer la longueur en mot séparé par des blancs
            String[] words = textforhtml.split("[\\s]");
            totwordspacesep = words.length-3; // -3  pour ajuster avec les différentes marques ajoutées
        }
        DoParse a = new DoParse(new StringReader(textforhtml), glue.dontIndexThis);
        a.scanString(glue, this);
        t.stop();
        //t = new Timer("Compacting");
        compact();
        //t.stop();
        //t = new Timer("Compute Sequence");
        computeSeq3();
        //t.stop();
        //t = new Timer("Marking");
        markString();
        //t.stop();
        System.out.println(glue.indexread.getStatistic());
        timing.stop();
        //this.print();
    }

    public void postInit(String fileName) {
        uploadFileName = fileName;
    }

    public static String addSpace(String s) {  // traitement du chinois
        StringBuilder res = new StringBuilder("");
        for (int i = 0; i < s.length(); i++) {
            res.append(s.charAt(i));
            int v = s.charAt(i);
            if ((v >= 0x4e00 && v <= 0x9FFF) // unified ideographs
                    || (v >= 0x3400 && v <= 0x4DFF) // extension A  
                    || (v >= 0x3400 && v <= 0x4DFF) // extension A  
                    || (v >= 0x20000 && v <= 0x2a6df) // extension A  
                    || (v >= 0xf900 && v <= 0xfaff) // compatibility  
                    || (v >= 0x2f800 && v <= 0x2fa1f) // compatibility  
                    ) {
                res.append(" ");
            }
        }
        return res.toString();
    }

    private final void newMark() {
        currentMark++;
        if (currentMark == MaxMark) {
            currentMark = 0;
        }
    }

    private final void compact() {
        int l;
        lastcp = 0;
        idxcp = new int[lastscan];
        idxorig = new int[lastscan];
        cpW = new int[lastscan];
        for (int i = 0; i < lastscan; i++) {
            if (idxW[i] != NotIndexed) {
                idxcp[i] = lastcp;
                cpW[lastcp] = idxW[i];
                idxorig[lastcp] = i;
                lastcp++;
            } else {
                idxcp[i] = NoMark;
            }
        }
        System.out.println(lastscan + " compact " + lastcp);
    }

    private final void computeSeq3() {
        boolean verbose = false;
        int[] resD;
        int count = 0;
        Date start = new Date();
        doc = new SparseBitSet[lastscan];
        for (int i = 0; i < lastcp - seqn; i++) {
            count++;
            if (verbose && count % 100 == 0) {  // check time  ...
                Date end = new Date();
//                System.out.println(count + ":" + (end.getTime() - start.getTime()));
                start = new Date();
            }
            if (minlength < 6) { // <6 
                resD = getDocforWseqW3(glue, cpW[i], cpW[i + 1], cpW[i + 2]);
            } else { // >=6
                resD = getDocforWseqW6(glue, cpW[i], cpW[i + 1], cpW[i + 2], cpW[i + 3], cpW[i + 4], cpW[i + 5]);
            }
            //id.showVector(resD);
            if (alignsota) {  // need to be filtered by sota
                int l = resD.length;
                doc[i] = new SparseBitSet();
                for (int j = 0; j < l; j++) {
                    int currentDoc = resD[j];
                    if (sota.get(currentDoc)) {// check source and target
                        doc[i].insertbit(currentDoc);
                    }
                }
            } else { // no filter
                if (resD.length > 0) { // copy result
                    doc[i] = new SparseBitSet(resD);
                } else { // empty result
                    doc[i] = new SparseBitSet();
                }
            }
        }
    }

    private final void markString() {
        begM = new int[lastscan];
        for (int i = 0; i < lastscan; i++) {
            begM[i] = NoMark;
        }
        endM = new int[lastscan + 1]; // for the last !
        for (int i = 0; i < lastscan; i++) {
            endM[i] = NoMark;
        }
        docM = new int[lastscan];
        docMultiRef = new Vector<String>();
        txtRef = new Vector<String>();
        txtRefOrigin = new Vector<String>();
        int maxmarked = -1;
        int mark;
        int markdoc = 0;
        int[] multidoc = null;
        for (int i = 0; i < lastcp - seqn; i++) {
            SparseBitSet b = doc[i];
            if (b.notEmpty()) { // ok look for the next
                mark = i;
                for (int j = i; j < lastcp - seqn; j++) {
                    b = b.and(doc[j]);
                    if (b.notEmpty()) {
                        mark = j;
                        b.resetcursor();
                        markdoc = b.getNextPos();
                        multidoc = b.getDelta();
                    } else {
                        break;
                    } // not in the same doc
                }
                if ((mark > maxmarked) && ((mark - i) >= minlength - seqmax)) { // not included in a bigger ref
                    maxmarked = mark; // new max
                    newMark();
                    begM[i] = currentMark;
                    endM[maxmarked + seqmax] = currentMark;
                    b.resetcursor();
                    docM[i] = markdoc; // get the first ref (if many)
                    nbref++;

                    String dlist = glue.getFileNameForDocument(multidoc[0]); // liste des références
                    for (int k = 1; k < multidoc.length; k++) {
                        dlist += REFResultNice.DOC_REF_SEPARATOR + glue.getFileNameForDocument(multidoc[k]);
                    }
                    docMultiRef.add(dlist);
                    String tlist = glue.getStringforW(cpW[i]); // texte des références
                    for (int k = i + 1; k < maxmarked + seqmax; k++) {
                        tlist += " " + glue.getStringforW(cpW[k]);
                    }
                    txtRef.add(tlist);
                    txtRefOrigin.add(
                            textforhtml.substring(idxpos[idxorig[i]] - word[idxorig[i]].length() - 1,
                            idxpos[idxorig[maxmarked + seqmax - 1]] - 1));
                }
            }// if
        }// for
        System.out.println("nbref: " + nbref);
//        for (int i = 0; i < nbref; i++) {
//            System.out.println(i + " -------------");
//            System.out.println("doc:" + docMultiRef.get(i));
//            System.out.println("origin:" + txtRefOrigin.get(i));
//            System.out.println("indexed:" + txtRef.get(i));
//        }

    }

    public final String getHTML() {

        StringBuilder s = new StringBuilder("");
        s.append("<html>\n");
        s.append("<head>\n");
        s.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\">\n");
        s.append("<title>myQuote</title>");
        s.append("</head>\n");
        s.append("<body>\n");
        s.append(getXML());
        if (MYQUOTE_STAT) {
            s.append(getSTAT());
            s.append(getREF());
        }
        s.append("</body>\n");
        s.append("</html>\n");
        return s.toString();
    }

    public final String getREF() {
        //Timer timing = new Timer("--------------------------------Total getXML");
        StringBuilder s = new StringBuilder("");
        // s.append("<P>\nHere the references to be saved\n");

        s.append("\n<!--MYQUOTEREF");
        s.append("\n" + nbref);
        for (int i = 0; i < nbref; i++) {
            s.append("\n" + i + REFResultNice.DOC_REF_SEPARATOR + txtRef.get(i) + REFResultNice.DOC_REF_SEPARATOR + docMultiRef.get(i));
        }
        s.append("\nMYQUOTEREF-->");

        s.append("</P>\n");
        //timing.stop();
        return s.toString();
    }

    public final String getSTAT() {
        //Timer timing = new Timer("--------------------------------Total getXML");

        StringBuilder s = new StringBuilder("<P>\n");
        s.append("<hr/>\n");
        ReferenceStatistic rs = new ReferenceStatistic(txtRefOrigin, docMultiRef, totwordspacesep);
        s.append(rs.getHeaderSat(uploadFileName, collectList, minlength));
        s.append("<hr/>\n");
        s.append(rs.getStatByRef());
        s.append("<hr/>\n");
        s.append(rs.getStatByQuote());
        s.append("<hr/>\n");
        //timing.stop();
        return s.toString();
    }

    public final String getXML() {
        //Timer timing = new Timer("--------------------------------Total getXML");
        StringBuilder s = new StringBuilder("<P>\n");
        posRef = new int[nbref];
        int countchar = 1;
        int countrefstart = 0;
        int countrefstop = 0;
        int openRef = 0;  // pour gérer les intersections de références
        for (int i = 0; i < lastscan - 1; i++) {
//            System.out.println(idxcp[i + 1]);
//            System.out.println(idxcp[i + 1]+", "+begM[idxcp[i + 1]]+", "+endM[idxcp[i + 1]]);
            if (idxcp[i + 1] != NoMark) {
//              System.out.println(idxcp[i + 1]+", "+begM[idxcp[i + 1]]+", "+endM[idxcp[i + 1]]);              
                if (endM[idxcp[i + 1]] != NoMark) {
                    openRef--;
                    countrefstop++;
                    String targetTxt = CLOSE_REF_BEG + countrefstop + CLOSE_REF_END;
                    countchar += targetTxt.length();
                    if (openRef >= 1) {
                        s.append(targetTxt);
                    } else {
                        s.append(targetTxt).append("</a>");
                    }
                } // if
                if (begM[idxcp[i + 1]] != NoMark) {
                    openRef++;
                    posRef[countrefstart] = countchar;
                    countrefstart++;
                    String targetTxt = OPEN_REF_BEG + countrefstart + OPEN_REF_END;
                    countchar += targetTxt.length();
                    if (openRef > 1) {
                        s.append("</a>");
                    }
                    s.append("<a href=\"#").append(countrefstart).append("\" onClick=\"return gwtnav(this);\">").append(targetTxt);
                } //if
            } //if
            String addtohtml;
            if (i < lastscan - 1) {
                addtohtml = textforhtml.substring(idxpos[i], idxpos[i + 1]);
            } else {
                addtohtml = textforhtml.substring(idxpos[i], textforhtml.length());
            }
            countchar += addtohtml.length();
            s.append(addtohtml);
        } // for
//        if (endM[idxcp[lastscan - 1] + 1] != NoMark) { // si la référence va jusqu'à la fin
        if (openRef > 0) { // si la référence va jusqu'à la fin
            countrefstop++;
            String targetTxt = CLOSE_REF_BEG + countrefstop + CLOSE_REF_END;
            countchar += targetTxt.length();
            s.append(targetTxt).append("</a>");
        } // if
        s.append("</P>\n");
        //timing.stop();
        return s.toString().replace("\n", "<br/>");
    }

    private final void print() {
        System.out.println("Scanned String lenght" + lastscan);
        for (int i = 0; i < lastscan - 1; i++) {
            System.out.println(i + "/" + word[i] + "/" + idxpos[i] + "/" + idxW[i] + "/" + idxcp[i] + "/" + begM[i] + "/" + endM[i] + "/" + docM[i]);
            doc[i].print();
        }
    }
}
