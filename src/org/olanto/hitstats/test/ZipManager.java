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
package org.olanto.hitstats.test;

import org.olanto.idxvli.util.BytesAndFiles;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.util.BytesAndFiles.*;
import static org.olanto.conman.ContentConstant.*;
import org.olanto.util.Timer;

/**
 * Une classe pour gérer le conteneur.
 *
 */
public class ZipManager {

    private static byte[][] zip;
    private static int[] zipSize;
    private static int last = 0;

    public ZipManager(int sizeMAX) {
        zip = new byte[sizeMAX][];
        zipSize = new int[sizeMAX];

    }

    public static void main(String[] args) throws FileNotFoundException {

        ZipManager ZM = new ZipManager(1000000);
        //   String path="C:/MYCAT/corpus/txt/EN";
        //String path = "C:/MYCAT/corpus_cern/txt/EN";
          String path="C:/MYCAT/corpus_OMC/txt/EN";
        Timer t = new Timer("Zip all the file");
        ZM.getFromDirectory(path, "UTF-8");
        t.stop();
        System.out.println("nb files:" + last);

        long count = 0;
        t = new Timer("unZip all files from memory");
        for (int i = 0; i < last; i++) {
            count += ((String) ZM.getStringContent(i)).length();
        }
        t.stop();
        System.out.println("nb chars:" + count);

        long zipcount = 0;
        for (int i = 0; i < last; i++) {
            zipcount += zip[i].length;
        }
        System.out.println("zip size:" + zipcount);


    }

    protected void addContent(int pos, byte[] content) {
        //msg("addContent: "+glue.lastdoc+"->"+docName);

        zip[pos] = compress(content);
        zipSize[pos] = content.length;



    }

    /**
     * r�cup�re un contenu type String.
     *
     * @param doc identifiant
     */
    protected String getStringContent(int pos) {

        String res;
        try {
            res = new String(decompress(zipSize[pos], zip[pos]), CONTENT_ENCODING);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ZipManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return res;

    }

    /**
     * r�cu�re le contenu d'un r�pertoire.
     *
     * @param pathName r�pertoire
     * @param language langage de la collection
     * @param collection nom de la collection
     * @param txt_encoding encodage des textes
     */
    protected void getFromDirectory(String pathName, String txt_encoding) {

        File f = new File(pathName);
        if (f.isFile()) {
            if (pathName.endsWith(".txt")) {  // du texte
                processATextFile(last, pathName, txt_encoding);
                last++;
            } else {
                //onePassIndexdocManyFile(path,fdate);
            }


        } else {
            msg("indexdir:" + pathName);
            String[] lf = f.list();
            int ilf = lf.length;
            for (int i = 0; i < ilf; i++) {
                getFromDirectory(pathName + "/" + lf[i], txt_encoding);
            }
        }

    }

    /**
     * pour des fichiers individuels
     */
    private final void processATextFile(int pos, String fname, String txt_encoding) {
        try {
            String content = file2String(fname, txt_encoding);
            byte[] b = content.getBytes(CONTENT_ENCODING);
            addContent(pos, b);
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
}
