/**********
    Copyright © 2010-2012 Olanto Foundation Geneva

   This file is part of myCAT.

   myCAT is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/

package org.olanto.idxvli;

import java.io.*;
//import static isi.jg.idxvli.ql.QueryOperator.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxConstant.*;
import org.olanto.idxvli.extra.DocPosChar;
import org.olanto.idxvli.ref.IdxReference;
import org.olanto.idxvli.ql.QLCompiler;

/**
 * Une classe pour interroger l'index.
 * 
 *
 */
public class IdxQuery {

    IdxStructure glue; // local ref to index

    /** utilisation interne seulement
     * @param id index de rÃ©fÃ©rence
     */
    IdxQuery(IdxStructure id) {
        glue = id;
    }

    /** analyseur simple de requÃªte et formatage en XML du rÃ©sultat (conservÃ©e seulement pour la compatibilitÃ©).
     * Cette mÃ©thode est Ã  utiliser lors d'appel par un noeud LAZY
     * @param command SINGLE,AND,NEAR,NEXT
     * @param p1 premier terme de la requï¿½te
     * @param p2 second terme de la requï¿½te
     * @return liste de rÃ©fÃ©rence en XML
     */
    public final String parseQuery(String command, String p1, String p2) { // from servlet
        if (command.equals("NEAR")) {
            return searchforWnearW(p1, p2);
        }
        if (command.equals("AND")) {
            return searchforWandW(p1, p2);
        }
        if (command.equals("NEXT")) {
            return searchforWnextW(p1, p2);
        }
        if (command.equals("SINGLE")) {
            return searchforW(p1);
        }
        if (command.equals("MULTI")) {
            return searchforString(p1, p2);
        }
        if (command.equals("SIMILAR")) {

            return "<h3>to be implemented</h3>"; //DistLexical.similardoc(Integer.parseInt(p1));
        }
        if (command.equals("")) {
            return "<h3>select a command and a term please</h3>";
        }

        return "<h3>Error in Query Unknow command: " + command + "</h3>";
    }

    private final String niceLemma(String w) {
        String lemma = WORD_DEFINITION.normaliseWord(glue, w);
        String lemmaExpansion = "";
        if (WORD_USE_STEMMER) {
            int n = glue.getIntForW(w);
            if (n != -1) {
                lemmaExpansion = "<ICON><COMMENT>" + glue.getStemList(n) + "</COMMENT><IMG>plus</IMG></ICON>";
            }
        }
        String res = "<b>" + lemma + "</b><i>" + w.substring(lemma.length()) + "</i>" + lemmaExpansion;
        return res;
    }

    private final String searchforW(String w) { // without semantic context
        int[] docs = (new QLCompiler(new StringReader(w), glue)).execute();
        String s = "<p>Cherche: " + niceLemma(w) + "</p>";
        return s + getXMLFNDocforW(getFNDocforD(docs), docs, w);
    }

    private final String searchforWandW(String w1, String w2) {
        int[] docs = (new QLCompiler(new StringReader(w1 + " AND " + w2), glue)).execute();
        String s = "<p>Cherche: " + niceLemma(w1) + " ET " + niceLemma(w2) + "</p>";
        return s + getXMLFNDocforW(getFNDocforD(docs), docs, w1);
    }

    private final String searchforWnearW(String w1, String w2) {
        int[] docs = (new QLCompiler(new StringReader("NEAR(" + w1 + "," + w2 + ")"), glue)).execute();
        ;
        String s = "<p>Cherche: " + niceLemma(w1) + " PROCHE DE " + niceLemma(w2) + "</p>";
        return s + getXMLFNDocforW(getFNDocforD(docs), docs, w1);
    }

    private final String searchforWnextW(String w1, String w2) {
        int[] docs = (new QLCompiler(new StringReader("NEXT(" + w1 + "," + w2 + ")"), glue)).execute();
        ;
        String s = "<p>Cherche: " + niceLemma(w1) + " SUIVI DE " + niceLemma(w2) + "</p>";
        return s + getXMLFNDocforW(getFNDocforD(docs), docs, w1);
    }

    private final String searchforString(String w, String min) { // with semantic context
        Integer n = new Integer(min);
        IdxReference s = new IdxReference(glue, w, n.intValue(), null, null, false, null);
        // return s.getXML();
        return "OK done";
    }

    private final String getXMLFNDocforW(String[] fn, int[] docs, String w1) {
        String r = "";
        if (fn != null) {
            int l = fn.length;
            for (int i = 0; i < l; i++) {
                r = r + XMLrefFN(fn[i], docs[i], w1);
            }
            return r;
        } else {
            return "<paragraphe>no documents for this condition</paragraphe>";
        }
    }

    private final String XMLrefFN(String fn, int doc, String w1) {

        //    "<a href=\"ns?a=similarTo&amp;u="+fn+"\">sim("+fn+") </a>"; test similarity


        String context = "";
        if (IDX_MORE_INFO && w1 != null) {
            context = DocPosChar.extractForW(doc, glue, w1, 5);
        }
        String res = "<P_small><URL target=\"document\" href=\"" + cleanValue(fn) + "\">" + cleanValue(fn) + "</URL>" + context + "</P_small>";
        return res;

    }

    private final String[] getFNDocforD(int[] d) {
        if (d != null) {
            int l = d.length;
            String r[] = new String[l];
            for (int i = 0; i < l; i++) {
                r[i] = glue.getFileNameForDocument(d[i]);
            }
            return r;
        } else {
            return null;
        }
    }

    /**
     * nettoye les caractÃ¨res pas supportÃ©s dans XML
     * @param s chaine Ã  nettoyer
     * @return une chaine plus propre
     */
    protected final static String cleanValue(String s) {  // Ã©liminie les & et x pour ï¿½tre xml compatible
        int ix = 0;
        while ((ix = s.indexOf("&", ix)) > -1) {
            s = s.substring(0, ix) + "&amp;" + s.substring(ix + 1, s.length());
            ix += 4;
        }
        ix = 0;
        while ((ix = s.indexOf("<", ix)) > -1) {
            s = s.substring(0, ix) + "&lt;" + s.substring(ix + 1, s.length());
            ix += 3;
        }
        return s;

    }
}
