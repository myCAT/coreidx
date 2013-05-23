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
package org.olanto.idxvli.util.test;

import java.io.File;
import org.olanto.idxvli.util.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.util.BytesAndFiles.*;
import org.olanto.util.Timer;

/**
 *
 *
 *
 *
 */
public class TestZipVector_InMemory {

    static ZipVector o;
    static int last = 0;

    public static void main(String[] args) {
    /*    String s0 = "0";
        String s1 = "s1s1s1";
        String s2 = "s2s2s2s2s2s2s2s2s2s2s2s2";
        String s3 = "abcdefghijklmnopqrstuvwxyz";
        o = (new ZipVector_InMemory()).create("C:/MYCAT/TEMP", "zipvect", 20);
        o = (new ZipVector_InMemory()).open("C:/MYCAT/TEMP", "zipvect", readWriteMode.rw);
        o.set(0, s0);
        System.out.println(s0 + "=" + o.get(0));
        o.set(1, s1);
        System.out.println(s1 + "=" + o.get(1));
        o.set(2, s2);
        System.out.println(s2 + "=" + o.get(2));
        o.set(3, s3);
        System.out.println(s3 + "=" + o.get(3));

        System.out.println("o.length()" + o.length());
        System.out.println("o.lengthZip(3)" + o.lengthZip(3));
        System.out.println("o.lengthString(3)" + o.lengthString(3));
        System.out.println("o.totalLengthZip()" + o.totalLengthZip());
        System.out.println("o.totalLengthString()" + o.totalLengthString());
        o.clear(0);
        System.out.println(s0 + " after a clear=" + o.get(0));
        System.out.println("o.lengthZip(0)" + o.lengthZip(0));
        System.out.println("o.lengthString(0)" + o.lengthString(0));
        o.printStatistic();
        o.close();
        System.out.println("open again ...");
        o = (new ZipVector_InMemory()).open("C:/MYCAT/TEMP", "zipvect", readWriteMode.rw);
        System.out.println(s0 + "=" + o.get(0));
        System.out.println(s1 + "=" + o.get(1));
        System.out.println(s2 + "=" + o.get(2));
        System.out.println(s3 + "=" + o.get(3));
        System.out.println("o.length()" + o.length());
        System.out.println("o.lengthZip(3)" + o.lengthZip(3));
        System.out.println("o.lengthString(3)" + o.lengthString(3));
        System.out.println("o.totalLengthZip()" + o.totalLengthZip());
        System.out.println("o.totalLengthString()" + o.totalLengthString());
        o.printStatistic();

        // performance test



        o.close();

        // performance test

        o = (new ZipVector_InMemory()).create("C:/MYCAT/TEMP", "zipvect", 20);
        o = (new ZipVector_InMemory()).open("C:/MYCAT/TEMP", "zipvect", readWriteMode.rw);

        //String path = "C:/MYCAT/corpus/txt/EN";
        //String path = "C:/MYCAT/corpus_cern/txt/EN";
             String path="C:/MYCAT/corpus_OMC/txt/EN";
        Timer t = new Timer("------Zip all the file");
        getFromDirectory(path, "UTF-8");
        t.stop();
        System.out.println("nb files:" + last);
        o.printStatistic();
        t = new Timer("------close Zip object");
        o.close();
        t.stop();
       */ Timer t = new Timer("------open Zip object");
        System.out.println("open again ...");
        
        
        o = (new ZipVector_InMemory()).open("C:/MYCAT/TEMP", "zipvect", readWriteMode.rw);
        t.stop();
        o.printStatistic();
        o.close();

    }

    /**
     * récupère le contenu d'un répertoire.
     *
     * @param pathName répertoire
     * @param language langage de la collection
     * @param collection nom de la collection
     * @param txt_encoding encodage des textes
     */
    protected static void getFromDirectory(String pathName, String txt_encoding) {

        File f = new File(pathName);
        if (f.isFile()) {
            if (pathName.endsWith(".txt")) {  // du texte
                processATextFile(last, pathName);
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
    private static void processATextFile(int pos, String fname) {
        try {
            String content = file2String(fname, "UTF-8");
            o.set(pos, content);
        } catch (Exception e) {
            error("processATextFile", e);
        }
    }
}
