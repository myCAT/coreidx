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

package org.olanto.conman;

import org.olanto.idxvli.util.BytesAndFiles;
import java.io.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.util.BytesAndFiles.*;
import static org.olanto.conman.ContentConstant.*;
import org.apache.tools.bzip2.*;

/**
 * Une classe pour gérer le conteneur.
 * 
 */
public class ContentManager {

    ContentStructure glue;

    ContentManager(ContentStructure id) {
        glue = id;
    }

    /** ajoute un URL
     * @param url identifiant
     */
    protected void addURL(String url) {
        int docID = glue.docstable.put(url); // enregistre le nom du document
        //msg("addURL:"+url+" id:"+docID);
        if (docID == glue.lastdoc) { // un nouveau document
            glue.docstable.setDate(glue.lastdoc, System.currentTimeMillis()); // enregistre la date du document
            glue.docstable.setSize(glue.lastdoc, 0); // enregistre la taille du document
            glue.docstable.setPropertie(glue.lastdoc, "PROTOCOLE." + "URL");
            glue.docstable.setPropertie(glue.lastdoc, "STATE." + "NOTLOAD");


            glue.lastdoc = glue.docstable.getCount();
        }

    }

    /** ajoute un contenu.
     * @param docName identifiant
     * @param content contenu
     */
    protected void addContent(String docName, long fdate, byte[] content, String language, String type, String collection) {
        //msg("addContent: "+glue.lastdoc+"->"+docName);
        glue.docstable.put(docName); // enregistre le nom du document
        glue.docstable.setDate(glue.lastdoc, fdate); // enregistre la date du document
        glue.docstable.setSize(glue.lastdoc, content.length); // enregistre la taille du document
        glue.docstable.setPropertie(glue.lastdoc, "LANG." + language);
        glue.docstable.setPropertie(glue.lastdoc, "TYPE." + type);
        glue.docstable.setPropertie(glue.lastdoc, "COLLECTION." + collection);

        glue.IO.saveContent(glue.lastdoc, compress(content), content.length);

        if (type == "STRING") {
            glue.docstable.setPropertie(glue.lastdoc, "TYPE.INDEXABLE");
        }


        glue.lastdoc = glue.docstable.getCount();

    }

    /** r�cup�re un contenu type String.
     * @param doc identifiant
     */
    protected String getStringContent(int doc) {
        if (glue.docstable.getPropertie(doc, "TYPE.STRING")) {
            byte[] b = glue.IO.loadContent(doc);
            //msg("byte length:"+b.length);
            try {
                String res = new String(decompress(glue.IO.realSizeOfContent(doc), b), CONTENT_ENCODING);
                return res;
            } catch (Exception e) {
                error("getStringContent", e);
                return null;
            }
        }
        error("content is not a STRING for this docID:" + doc);
        return null;
    }

    /** r�cup�re un contenu type String.
     * @param doc identifiant
     */
    protected String getStringContentNOCOMP(int doc) {
        if (glue.docstable.getPropertie(doc, "TYPE.STRING")) {
            byte[] b = glue.IO.loadContent(doc);
            //msg("byte length:"+b.length);
            try {
                String res = new String(b, CONTENT_ENCODING);
                return res;
            } catch (Exception e) {
                error("getStringContent", e);
                return null;
            }
        }
        error("content is not a STRING for this docID:" + doc);
        return null;
    }

    /** r�cup�re un contenu type String sur un intervalle donn�.
     * @param doc identifiant
     */
    protected String getStringContent(int doc, int from, int to) {
        if (glue.docstable.getPropertie(doc, "TYPE.STRING")) {
            if (OBJ_COMPRESSION == Compression.YES) {
                byte[] b = glue.IO.loadContent(doc);
                //msg("byte length:"+b.length);
                try {
                    String res = new String(decompress(glue.IO.realSizeOfContent(doc), b), CONTENT_ENCODING);
                    if (from >= res.length()) {
                        return "";
                    } // vide
                    return res.substring(from, Math.min(to, res.length() - 1));
                } catch (Exception e) {
                    error("getStringContent", e);
                    return "";
                }
            } else { // no compression
                try {
                    byte[] b = glue.IO.loadContent(doc, from, to);
                    String res = new String(b, CONTENT_ENCODING);
                    return res;
                } catch (Exception e) {
                    error("getStringContent", e);
                    return "";
                }
            }
        }
        error("content is not a STRING for this docID:" + doc);
        return "";
    }

    /** r�cup�re un contenu type String.
     * @param docName identifiant
     */
    protected String getStringContent(String docName) {
        int doc = glue.getIntForDocument(docName);
        if (doc != NOT_FOUND) {
            return getStringContent(doc);
        }
        error("content is not found for this doc:" + docName);
        return null;
    }

    /** r�cup�re un contenu type Byte.
     * @param docName identifiant
     */
    protected byte[] getByteContent(String docName) {
        return getByteContent(glue.getIntForDocument(docName));

    }

    /** r�cup�re un contenu type Byte.
     * @param doc identifiant
     */
    protected byte[] getByteContent(int doc) {
        byte[] b = glue.IO.loadContent(doc);
        //msg("byte length:"+b.length);
        try {
            return decompress(glue.IO.realSizeOfContent(doc), b);
        } catch (Exception e) {
            error("getByteContent", e);
            return null;
        }
    }

    /** r�cu�re le contenu d'un r�pertoire.
     * @param pathName r�pertoire
     * @param language langage de la collection
     * @param collection nom de la collection
     * @param txt_encoding encodage des textes
     */
    protected void getFromDirectory(String pathName, String language, String collection, String txt_encoding) {
        File f = new File(pathName);
        if (f.isFile()) {
            long fdate = f.lastModified(); // date de la derni�re modification
            if (glue.docstable.IndexThisDocument(pathName, fdate)) {
                if (pathName.endsWith(".htm") || pathName.endsWith(".txt") || pathName.endsWith(".TXT") || pathName.endsWith(".html") || pathName.endsWith(".xls") || pathName.endsWith(".java") || pathName.endsWith(".sql") || pathName.endsWith(".lzy")) {  // du texte
                    processATextFile(f, pathName, fdate, language, collection, txt_encoding);
                } else {
                    //onePassIndexdocManyFile(path,fdate);
                }
            } else { // on ne fait rien car d�j� charg�
            }
        } else {
            msg("indexdir:" + pathName);
            String[] lf = f.list();
            int ilf = lf.length;
            for (int i = 0; i < ilf; i++) {
                getFromDirectory(pathName + "/" + lf[i], language, collection, txt_encoding);
            }
        }
    }

    /** pour des fichiers individuels */
    private final void processATextFile(File file, String fname, long fdate, String language, String collection, String txt_encoding) {
        try {
            String content = file2String(fname, txt_encoding);
            byte[] b = content.getBytes(CONTENT_ENCODING);
            addContent(fname, fdate, b, language, "STRING", collection);
        } catch (Exception e) {
            error("processATextFile", e);
        }
    }

    public static final byte[] decompress(int realSize, byte[] bb) {
        if (OBJ_COMPRESSION == Compression.YES) {
            return BytesAndFiles.decompress(realSize, bb);
        } else {
            return bb;
        }
    }

    public static final byte[] compress(byte[] bb) {
        if (OBJ_COMPRESSION == Compression.YES) {
            return BytesAndFiles.compress(bb);
        } else {
            return bb;
        }
    }

    /** distance de kolmogorov entre deux documents.
     * @param d1 document
     * @param d2 document
     * @return distance
     */
    protected double distOfKolmogorov(int d1, int d2, boolean bzip2) {
        String Sd1, Sd2;
        try {

            if (glue.docstable.getPropertie(d1, "TYPE.STRING")) {
                Sd1 = getStringContent(d1, 0, 10000);
            } else {
                Sd1 = new String(getByteContent(d1));
            }
            Sd2 = getStringContent(d2, 0, 10000);
            if (bzip2) {
                double kd1 = compressBZip2(Sd1.getBytes("UTF-8"));
                double kd2 = compressBZip2(Sd2.getBytes("UTF-8"));
                double kd12 = compressBZip2((Sd1 + Sd2).getBytes("UTF-8"));
                double kd21 = compressBZip2((Sd2 + Sd1).getBytes("UTF-8"));
                return ((kd12 - kd1) + (kd21 - kd2)) / (kd1 + kd2);
            } else {
                double kd1 = (BytesAndFiles.compress(Sd1.getBytes("UTF-8"))).length;
                double kd2 = (BytesAndFiles.compress(Sd2.getBytes("UTF-8"))).length;
                double kd12 = (BytesAndFiles.compress((Sd1 + Sd2).getBytes("UTF-8"))).length;
                double kd21 = (BytesAndFiles.compress((Sd2 + Sd1).getBytes("UTF-8"))).length;
                return ((kd12 - kd1) + (kd21 - kd2)) / (kd1 + kd2);
            }

        } catch (Exception e) {
            error("distOfKolmogorov", e);
        }
        return 0;  //
    }

    /** distance de kolmogorov entre deux documents.
     * @return distance
     */
    protected double distOfKolmogorov(double kd1, double kd2, String Sd1, String Sd2, boolean bzip2) { // il faut que la compression soit YES
        try {
            if (bzip2) {
                double kd12 = compressBZip2((Sd1 + Sd2).getBytes("UTF-8"));
                double kd21 = compressBZip2((Sd2 + Sd1).getBytes("UTF-8"));
                return ((kd12 - kd1) + (kd21 - kd2)) / (kd1 + kd2);
            } else {
                double kd12 = (BytesAndFiles.compress((Sd1 + Sd2).getBytes("UTF-8"))).length;
                double kd21 = (BytesAndFiles.compress((Sd2 + Sd1).getBytes("UTF-8"))).length;
                return ((kd12 - kd1) + (kd21 - kd2)) / (kd1 + kd2);
            }
        } catch (Exception e) {
            error("distOfKolmogorov", e);
        }
        return 0;  //
    }

    protected double kdlength(String Sd1, boolean bzip2) {
        try {
            if (bzip2) {
                return compressBZip2(Sd1.getBytes("UTF-8"));
            } else {
                return (BytesAndFiles.compress(Sd1.getBytes("UTF-8"))).length;
            }
        } catch (Exception e) {
            error("distOfKolmogorov", e);
        }
        return 0;  //

    }

    private static int compressBZip2(byte[] buffer) {
        try {
            CBZip2OnlyCount doc = new CBZip2OnlyCount();
            return doc.getLength(buffer, 0, buffer.length);
        } catch (Exception e) {
            error("error in compressBZip2", e);
        }
        return 0;
    }
}
