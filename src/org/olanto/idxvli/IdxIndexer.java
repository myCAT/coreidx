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
package org.olanto.idxvli;

import java.io.*;
import java.util.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.idxvli.IdxEnum.*;
import org.olanto.idxvli.cache.*;
import org.olanto.idxvli.extra.DocBag;
import org.olanto.idxvli.extra.DocSeq;
import org.olanto.idxvli.extra.DocPosChar;
import org.olanto.idxvli.util.BytesAndFiles;

/**
 * Une classe pour effectuer l'indexation des documents.
 *
 *
 *
 */
class IdxIndexer {

    /**
     * indice du dernier caractï¿½re lu dans le mfl
     */
    private long lastposinmfl = 0;
    /* variables pour mesurer la performance de l'indexeur */
    private static int nbstat1 = 0;
    /**
     * Timer interne pour des mesures de performance
     */
    private static long timer1;
    /**
     * frï¿½quence de la mesure du timer1 actuellement 10000 mots en n ms
     */
    private static final int nbstatidx = 1000000;
    IdxStructure glue;

    /**
     * crï¿½e une instance d'indexation. Cette instance est associï¿½e ï¿½ la
     * structure racine
     *
     * @param id structure racine
     */
    protected IdxIndexer(IdxStructure id) { // empty constructor

        glue = id;
    }

    /**
     * indexe tous les documents se trouvant dans le directoire (inclus ceux des
     * sous-dossiers)
     *
     * @param path directoire ï¿½ indexer
     */
    protected final void indexdir(String path) {
        Date starttime = new Date();
        onePassIndexdir(path);
    }

    private final void onePassIndexdir(String path) {
        File f = new File(path);
        if (f.isFile()) {
            // f.setReadOnly();  // marque le fichier en lecture seulement, mis en commentaire car ralentit
            long fdate = f.lastModified(); // date de la derniï¿½re modification
            //long fdate = f.lastModified() + (long) (100000L * Math.random()); // provoque des  modifications

            if (glue.docstable.IndexThisDocument(path, fdate)) {
                if (path.endsWith(".htm") || path.endsWith(".txt") || path.endsWith(".TXT") || path.endsWith(".html") || path.endsWith(".xml") || path.endsWith(".java") || path.endsWith(".sql") || path.endsWith(".lzy")) {
                    onePassIndexdoc(f, path, fdate);
                }
                if (path.endsWith(".mflf")) {
                    onePassFastmflf(path, fdate);
                }
            } else {
            }
        } else {
            msg("indexdir:" + path);
            String[] lf = f.list();
            int ilf = lf.length;
            for (int i = 0; i < ilf; i++) {
                onePassIndexdir(path + "/" + lf[i]);
            }
        }
    }

    private final void onePassFastmflf(String f, long fdate) {
        // no mfl no stemming no moreinfo
        if (IDX_ZIP_CACHE) {
            error("FATAL ERROR: IndexThisContent cant used IDX_ZIP_CACHE=true (code need to be upgraded)...");
            error("IDX_ZIP_CACHE must be set to false");
            System.exit(0);
        }

        if (WORD_USE_STEMMER) {
            error("WORD_USE_STEMMER must be set to false");
        }
        if (IDX_MORE_INFO) {
            error("IDX_MORE_INFO must be set to false");
        }
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f), IDX_MFLF_ENCODING);
            BufferedReader fin = new BufferedReader(isr, 1000000);
            msg(f + ":open in: " + IDX_MFLF_ENCODING);
            String w = null;
            StringBuilder news = new StringBuilder("");
            long firstCharOfDoc = 0;
            try {  // first doc align

                w = fin.readLine();
                while (w != null && !w.startsWith("#####")) {
                    w = fin.readLine();
                }
            } catch (Exception e) {
                System.err.println("IO error during read wipo file:" + w);
                e.printStackTrace();
            }
            while (w != null) {  // repeat for all documents in this file
                // String fshort=w.substring(5,w.length()-10); // skip ######  2005

                String fshort = "noName";
                try {
                    fshort = w.substring(5, w.length() - 5);// skip ######  2005

                } catch (Exception e) {
                    error("document Name is incorrect:" + w);
                }
                news.setLength(0);
//usedMemory("Before GC onePassFastmflf " + glue.lastRecordedDoc);
//if (glue.lastRecordedDoc%100==0)compactMemory("compact onePassFastmflf");
                try {
                    firstCharOfDoc = lastposinmfl;
                    w = fin.readLine();
                    while (w != null && !w.startsWith("#####")) {
                        news.append(w);
                        news.append("\n");
                        w = fin.readLine();
                    }
                } catch (Exception e) {
                    System.err.println("IO error during read wipo file:" + w);
                    e.printStackTrace();
                }
                if (glue.docstable.IndexThisDocument(fshort, fdate)) { // test si on doit indexer

                    DoParse a = new DoParse(news.toString(), glue.dontIndexThis);
                    if (IDX_WITHDOCBAG && DO_DOCBAG) {
                        DocBag.reset();
                    } // for winnow or perceptron

                    if (IDX_MORE_INFO) {
                        DocSeq.reset();
                        DocPosChar.reset();
                    } // for more info ...

                    a.start(glue);
                    if (IDX_WITHDOCBAG && DO_DOCBAG) {
                        glue.IO.saveBag(glue.lastRecordedDoc, DocBag.compact());
                    }
                    //msg("docidx" + lastdoc );
                    if (IDX_MORE_INFO) {
                        glue.IO.saveSeq(glue.lastRecordedDoc, DocSeq.compact());
                        glue.IO.savePosChar(glue.lastRecordedDoc, DocPosChar.compact());
                    }

                    if (DO_DOCRECORD) {
                        glue.docstable.put(fshort); // enregistre le nom du document


                        glue.docstable.setDate(glue.lastRecordedDoc, fdate); // enregistre la date du document

                        glue.docstable.setSize(glue.lastRecordedDoc, a.nbToken); // enregistre la taille du document

                        glue.lastRecordedDoc = glue.docstable.getCount();
                    } else {
                        glue.lastRecordedDoc++;
                    }  // avance le compteur de doc quand mï¿½me
                    //msg("file:" + f + ", total words=" + glue.lastword);
                    //msg("doc:"+glue.lastdoc+" actual size:"+glue.indexCoord.cacheCurrentSize());

                    if (glue.indexCoord.cacheOverFlow()) {
                        glue.indexCoord.freecache();
                    } // start saving memory

                } // if
//                else {
//                    msg("reject document:"+fshort);
//                }

            }  // while

            fin.close();
        } catch (IOException e) {
            error("IO error", e);
        }
    }

    /**
     * pour des strings individuels
     */
    protected final void IndexThisContent(String f, String content) {
        DoParse a = new DoParse(content, glue.dontIndexThis);
        if (IDX_WITHDOCBAG && DO_DOCBAG) {
            DocBag.reset();
        } // for winnow or perceptron

        if (IDX_MORE_INFO) {
            DocSeq.reset();
            DocPosChar.reset();
        } // for more info ...

        a.start(glue);
        if (IDX_WITHDOCBAG && DO_DOCBAG) {
            glue.IO.saveBag(glue.lastRecordedDoc, DocBag.compact());
        }
        if (IDX_MORE_INFO) {
            glue.IO.saveSeq(glue.lastRecordedDoc, DocSeq.compact());
            glue.IO.savePosChar(glue.lastRecordedDoc, DocPosChar.compact());
        }
        if (DO_DOCRECORD) {
            glue.docstable.put(f);// enregistre le nom du document

            glue.docstable.setSize(glue.lastRecordedDoc, a.nbToken); // enregistre la taille du document

            if (IDX_ZIP_CACHE) {
                glue.zipCache.set(glue.lastRecordedDoc, content); // enregistre le document dans le zipcache
            }

            glue.lastRecordedDoc = glue.docstable.getCount();
        } else {
            glue.lastRecordedDoc++;
        }  // avance le compteur de doc quand mï¿½me

        if (glue.indexCoord.cacheOverFlow()) {
            glue.indexCoord.freecache();
        } // start saving memory

    }

    /**
     * pour des fichiers individuels
     */
    private final void onePassIndexdoc(File file, String f, long fdate) {
        try {
            //msg("idx:"+f+" id:"+glue.lastRecordedDoc);
                     
            String content=BytesAndFiles.file2String(f,DOC_ENCODING);
            DoParse a = new DoParse(content, glue.dontIndexThis);
            if (IDX_WITHDOCBAG && DO_DOCBAG) {
                DocBag.reset();
            } // for winnow or perceptron

            if (IDX_MORE_INFO) {
                DocSeq.reset();
                DocPosChar.reset();
            } // for more info ...

            a.start(glue);
            if (IDX_WITHDOCBAG && DO_DOCBAG) {
                glue.IO.saveBag(glue.lastRecordedDoc, DocBag.compact());
            }
            if (IDX_MORE_INFO) {
                glue.IO.saveSeq(glue.lastRecordedDoc, DocSeq.compact());
                glue.IO.savePosChar(glue.lastRecordedDoc, DocPosChar.compact());
            }
            // enregistre le document
            if (DO_DOCRECORD) {
                int currentDoc = glue.docstable.put(f);// enregistre le nom du document
                //msg("current ID:"+currentDoc);

                glue.docstable.setDate(glue.lastRecordedDoc, fdate); // enregistre la date du document

                glue.docstable.setSize(glue.lastRecordedDoc, a.nbToken); // enregistre la taille du document
                if (IDX_ZIP_CACHE) {
                    glue.zipCache.set(glue.lastRecordedDoc,content); // enregistre le document dans le zipcache
                }
                glue.lastRecordedDoc = glue.docstable.getCount();
            } else {

                glue.lastRecordedDoc++;
            }// avance le compteur de doc quand mï¿½me

            if (glue.indexCoord.cacheOverFlow()) {
                glue.indexCoord.freecache();
            } // start saving memory

        } catch (Exception e) {
            error("onePassIndexdoc", e);
        }
    }

    private final void stattimer1() {
        if (nbstat1 == 0) {
            Date starttime = new Date();
            timer1 = starttime.getTime();
        } else if (nbstat1 == nbstatidx) {
            Date stoptime = new Date();
            long timing = stoptime.getTime() - timer1;
            nbstat1 = -1;
            msg("block idx " + timing + " ms" + " #docs" + glue.lastRecordedDoc);
            glue.wordstable.printStatistic();
        }
        nbstat1++;
    }

    protected final void addref(int iwreal, // word number
            int id, // document number
            int pa, //  position absolu
            int poschar //  position of char in file
            ) {
        CacheWrite indexdoc = glue.indexdoc;  // crï¿½e une rï¿½fï¿½rence locale

        CacheWrite indexpos = glue.indexpos;
        int iw = glue.idxtrans.registerCacheId(iwreal);  // on continue ï¿½ travailler avec l'index du cache !
        //msg("iwreal:"+iwreal+" iw:"+iw);

        glue.indexCoord.incTotalIdx();   // count a new occurence
        //stattimer1();  // uncomment

        if (IDX_WITHDOCBAG && DO_DOCBAG) {
            if (WORD_LIST) {
                DocBag.addWord(iw);
            } else {
                int nbocc = glue.getOccOfW(iwreal);
                if (nbocc >= WORD_MINOCCKEEP && nbocc * 1000 / glue.nbdoctoclassify < WORD_MAXOCCDOCKEEP) {
                    DocBag.addWord(iw);
                }
            } // for winnow ...

        }
        if (MODIFY_IDX) {
            if (IDX_MORE_INFO) {// for more info ...

                DocSeq.addWord(iw);
                DocPosChar.addWord(poschar);
            }
            if (IDX_SAVE_POSITION) {
                if (indexdoc.getCountOf(iw) == 0) { // new word

                    indexdoc.newVector(iw, 2 * AVG_INDEXDOC);
                    indexdoc.setCountOf(iw, 1); // actual use size

                    indexdoc.setv(iw, 0, id);
                    indexdoc.setv(iw, 1, 1);
                    if (IDX_SAVE_POSITION) {
                        indexpos.newVector(iw, AVG_INDEXPOS); // ask a vector to store the position

                        indexpos.setv(iw, 0, pa);
                        indexpos.setCountOf(iw, 1);
                    }
                    //showVector(indexdoc.v(iw));
                } else {
                    // <<1 =*2
                    int il = indexdoc.getCountOf(iw) << 1; // actual use length

                    if (indexdoc.v(iw, il - 2) != id) { //new ref

                        int tl = indexdoc.length(iw); // total capacity

                        indexdoc.incCountOf(iw); // new position

                        if (il >= tl) {
                            indexdoc.resize(iw, tl << 1);
                        }
                        indexdoc.setv(iw, il, id); // il is already minus one !!!

                        indexdoc.setv(iw, il + 1, 1);
                        // save pos
                        if (indexpos.getCountOf(iw) >= indexpos.length(iw)) {
                            indexpos.resize(iw, indexpos.getCountOf(iw) << 1);
                        }
                        indexpos.setv(iw, indexpos.getCountOf(iw), pa);
                        indexpos.incCountOf(iw);
                    } else {
                        // save pos
                        if (indexpos.getCountOf(iw) >= indexpos.length(iw)) {
                            indexpos.resize(iw, indexpos.getCountOf(iw) << 1);
                        }
                        indexpos.setv(iw, indexpos.getCountOf(iw), pa);
                        indexpos.incCountOf(iw);
                        indexdoc.incv(iw, il - 1); //inc occ count

                    }
                }
            } else {  // pas les positions

                if (MODE_RANKING == RankingMode.NO) {// sauver seulement les documents

                    if (indexdoc.getCountOf(iw) == 0) { // new word

                        indexdoc.newVector(iw, AVG_INDEXDOC);
                        indexdoc.setCountOf(iw, 1); // actual use size

                        indexdoc.setv(iw, 0, id);
                    } else {
                        int il = indexdoc.getCountOf(iw); // actual use length

                        if (indexdoc.v(iw, il - 1) != id) { //new ref

                            int tl = indexdoc.length(iw); // total capacity

                            indexdoc.incCountOf(iw); // new position

                            if (il >= tl) {
                                indexdoc.resize(iw, tl << 1);
                            }
                            indexdoc.setv(iw, il, id); // il is already minus one !!!

                        }
                    }
                } else {// sauver seulement les documents et les occurences

                    if (indexdoc.getCountOf(iw) == 0) { // new word

                        indexdoc.newVector(iw, 2 * AVG_INDEXDOC);
                        indexdoc.setCountOf(iw, 1); // actual use size

                        indexdoc.setv(iw, 0, id);
                        indexdoc.setv(iw, 1, 1);
                    } else {
                        // <<1 =*2
                        int il = indexdoc.getCountOf(iw) << 1; // actual use length

                        if (indexdoc.v(iw, il - 2) != id) { //new ref

                            int tl = indexdoc.length(iw); // total capacity

                            indexdoc.incCountOf(iw); // new position

                            if (il >= tl) {
                                indexdoc.resize(iw, tl << 1);
                            }
                            indexdoc.setv(iw, il, id); // il is already minus one !!!

                            indexdoc.setv(iw, il + 1, 1);
                        } else {  // juste incrï¿½menter

                            indexdoc.incv(iw, il - 1); //inc occ count

                        }
                    }
                }
            }
        }
    }
}
