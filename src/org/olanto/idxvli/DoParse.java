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

import org.olanto.idxvli.extra.DocBagInteractive;
import org.olanto.idxvli.extra.Stemmer;
import org.olanto.idxvli.ref.IdxReference;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.TreeSet;
import static org.olanto.idxvli.IdxConstant.*;

/**
 * Une classe pour effectuer le parsing des documents.
 * 
 *Une classe pour effectuer le parsing des documents.<br>
 *
 * On peut changer le tokenizer en modifiant l'initialisation de WORD_DEF
 * et associant le bon code.<br>
 *
 * On peut penser que chaque client a un mÃ©thode next() et normalise () et dÃ©finir un type
 * associÃ© Ã  un case pour sÃ©lectionner le bon next et normalise (Ã  faire)
 *
 */
public class DoParse {

    static final int EOF = -1;
    // attention les variables sont statiques pour accï¿½lerer mais si des accï¿½s concurrents sont possibles
    // alors il faut protï¿½ger le parsing
    private SetOfWords DontIndexThis;
    public int nbToken = 0;
    public IdxStructure glue;
    /** variable contenant le symbole courant*/
    public  StringBuilder cw = new StringBuilder();
    /** la source de caractï¿½re ï¿½ parser */
    public  Reader in;
    /** indique si la fin du fichier est atteinte */
    public  boolean EOFflag = true;
    /** posistion dans la source de caractï¿½re */
    public  int poschar = 0;
    
    // supporte pas la concurrence
    public static boolean acceptedToken = true; // pour le Tokeniser
    public static boolean conceptFound = false; // pour le Tokeniser attention ne supporte pas la concurrence
    public static boolean wordFound = false; // pour le Tokeniser attention ne supporte pas la concurrence
    public static boolean wordExternal = false; // pour le Tokeniser attention ne supporte pas la concurrence

    /** Crï¿½e un analyseur lexical. sur la source (Reader) avec la liste de mots
     * ï¿½ ne pas indexer SOF. Se place sur la premiï¿½re occurence de la source
     * @param s chaï¿½ne de caractï¿½res ï¿½ parser
     * @param SOF la liste de mots ï¿½ ne pas indexer
     */
    public DoParse(String s, SetOfWords SOF) {
        //msg("doparse:"+s);
        try {

            in = new StringReader(s);
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
        poschar = 0;
        acceptedToken = false;
        EOFflag = false;
        WORD_DEFINITION.next(this);
        DontIndexThis = SOF;
    }

    /**
     * Crï¿½e un analyseur lexical. sur la source (Reader) avec la liste de mots
     * ï¿½ ne pas indexer SOF. Se place sur la premiï¿½re occurence de la source
     * @param rdr la source ï¿½ analyser
     * @param SOF la liste de mots ï¿½ ne pas indexer
     */
    public DoParse(Reader rdr, SetOfWords SOF) {
        try {

            in = new BufferedReader(rdr);
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
        poschar = 0;
        acceptedToken = false;
        EOFflag = false;
        WORD_DEFINITION.next(this);
        DontIndexThis = SOF;
    }

    /**
     * cette mï¿½thode est utilisï¿½e pour ï¿½tablir le coï¿½t minimum du parsing.Les indices ne sont pris que dans l'intervalle total
    String word="";
     * pas de minuscule, pas de normalisation, ...
     * pour l'utiliser, il faut la renommer start !
     * @param glue l'indexeur de rï¿½fï¿½rence
     */
    protected final void start_quick(IdxStructure glue) {
        String word = "";
        int n;
        int pa = 1;  /* les positions partent de 1 */
        while (!EOFflag) {  // WIPO break length
            //System.out.println(id.lastdoc+","+DocBag.differentWords+","+id.lastword+","+cw);

            if (cw.length() >= WORD_MINLENGTH) {  //&& !DontIndexThis.check(cw)  ){
                // ici word est NORMALISE

                word = (cw.toString());
                //   one pass, on ajoute des termes au fur et a mesure ...
                n = glue.wordstable.get(word);
                if (n == -1) { // new word

                    n = glue.wordstable.put(word);
                    // System.out.print(word+",");
                    //n = id.wordstable.get(word);
                }
                glue.Indexer.addref(n, glue.lastRecordedDoc, pa, poschar);
                pa++;
            }
            WORD_DEFINITION.next(this);
        }
        nbToken = pa;  // memorise la nombre de termes reconnus

    }

    /**
     * commence le parsing, et indexe. n'est pas synchronisï¿½e! Les indices ne sont pris que dans l'intervalle total
     * @param id l'indexeur de rï¿½fï¿½rence
     */
    protected final void start(IdxStructure id) {
        glue = id;
        String word = "", wordWithoutStem = "";
        int n;
        int pa = 1;  /* les positions partent de 1 */
        int cntword = 0;
        int ratiolimit = WORD_NFIRSTOFDOC * 8 / 3;  // from experiment !!
        //       while (!EOFflag) {
        //       while (!EOFflag&&DocBag.differentWords<=id.NfirstWordOfDoc) {  // WIPO break length with docBag

        while (!EOFflag && cntword <= ratiolimit) {  // WIPO break length
            //System.out.println(id.lastdoc+","+DocBag.differentWords+","+id.lastword+","+cw);

            word = WORD_DEFINITION.normaliseWord(id, cw.toString());
            if (word.charAt(0) > 0x0370 ||Character.isDigit(word.charAt(0)) || (word.length() >= WORD_MINLENGTH && !DontIndexThis.check(word))) { // si chinois alors pas de limitation
                acceptedToken = true;
                // System.out.println("doparse:"+word);
                //                if (WORD_USE_STEMMER){
                //                    wordWithoutStem=word;
                //                    word=Stemmer.stemmingOfW(word);}
                cntword++;
                // ici word est NORMALISE
                if (WORD_TWOPASS) {// two pass, on ajoute plus de terme, ils sont sï¿½lectionnï¿½s dans la passe 1, pour la classification 

                    n = id.wordstable.get(word);
                    if (n != -1) { // existing word

                        id.Indexer.addref(n, id.lastRecordedDoc, pa, poschar);
                        pa++;
                    }
                } else {// one pass, on ajoute des termes au fur et a mesure ...

                    n = id.wordstable.get(word);
                    //msg("get"+n+","+word);
                    if (n == -1) { // new word

                        n = id.wordstable.put(word);
                        //msg("put"+n+","+word);
                        // System.out.print(word+",");
                        //n = id.wordstable.get(word);
                        if (STEM_KEEP_LIST && WORD_USE_STEMMER) { // ouvre une liste pour enregistrer les mots origines

                            id.stemList[n] = new TreeSet<String>();
                        }
                    }
                    id.Indexer.addref(n, id.lastRecordedDoc, pa, poschar);
                    if (STEM_KEEP_LIST && WORD_USE_STEMMER) { // sauver les termes sans lemmatisation

                        id.stemList[n].add(wordWithoutStem);
                    }
                    pa++;
                }
            } else {
                acceptedToken = false;
            }
            //TimerNano t=new TimerNano("next",false);
            WORD_DEFINITION.next(this);
            //t.stop(false);
        }
        nbToken = pa;  // memorise la nombre de termes reconnus

        id.lastRecordedWord = id.wordstable.getCount(); // met a jour le dernier mot connu

    }

    /** construit un doc bag a partir de l'entrï¿½e du parseur. Les indices ne sont pris que dans l'intervalle de lecture
     * la mï¿½thode est protï¿½gï¿½e pour sï¿½quentialiser les parsing concurrent
     * @param id indexeur de rï¿½fï¿½rence
     * @return vecteur au format DocBag
     *
     */
    synchronized public final int[] getDocBag(IdxStructure id) {
        String word;
        int n;
        DocBagInteractive dbi = new DocBagInteractive(id);

        //System.out.println("getDocBag eof:"+EOFflag);

        //       while (!EOFflag) {
        while (!EOFflag && dbi.getDifferentWords() <= WORD_NFIRSTOFDOC) {  // WIPO break length
            //System.out.println(cw);

            if (cw.length() >= WORD_MINLENGTH) {
                word = WORD_DEFINITION.normaliseWord(id, cw.toString());
                if (!DontIndexThis.check(word)) {
                    acceptedToken = true;
                    if (WORD_USE_STEMMER) {
                        word = Stemmer.stemmingOfW(word);
                    }
                    // ici word est NORMALISE
                    n = id.wordstable.get(word);
                    if (n != -1 && n < id.lastUpdatedWord) { // existing word
                        //System.out.println("in doc bag:"+word);

                        dbi.addWord(n); // for winnow ...  // could be optimize for interactive purpose

                    }
                } else {
                    acceptedToken = false;
                }
            } else {
                acceptedToken = false;
            }
            WORD_DEFINITION.next(this);
        }
        return dbi.compact();
    }

    /** construit un doc bag a partir de l'entrï¿½e du pareur. Les indices ne sont pris que dans l'intervalle de lecture
     * la mï¿½thode est protï¿½gï¿½e pour sï¿½quentialiser les parsing concurrent
     * @param id indexeur de rï¿½fï¿½rence
     * @return vecteur au format DocBag
     *
     */
    synchronized public final int[] getConceptualDocBag(IdxStructure id) {
        String word;
        int n;
        DocBagInteractive dbi = new DocBagInteractive(id);

        //System.out.println("getDocBag eof:"+EOFflag);

        while (!EOFflag) {
            String wordNoStem = cw.toString();
            //System.out.println("conceptualDB:"+cw+".");
            word = WORD_DEFINITION.normaliseWord(id, cw.toString());
            //System.out.println("after normal:"+word+".");
            if (!DontIndexThis.check(word)) {
                //msg(word+","+DontIndexThis.check(word));
                acceptedToken = true;
                // ici word est NORMALISE
                n = id.wordstable.get(word);
                if (n != -1 && n < id.lastUpdatedWord) { // existing word
                    //System.out.println("in doc bag:"+word);

                    if (conceptFound && !wordFound) {
                        dbi.addWord(n);
                        System.out.println("in doc bag:" + word);
                    }
                    if (!conceptFound && wordFound) {
                        if (IDX_CONCEPTUAL) { //conceptuel

                            n = id.wordstable.get(stem(id, "_EN", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_FR", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_IT", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_DU", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_DE", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_ES", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_FI", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_SW", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                        } else {
                            dbi.addWord(n);  // native seulement
                            //System.out.println("in doc bag:"+word);

                        }
                    }//System.out.println("in doc bag:"+word);}

                }
            } else {
                if (!ACTUAL_LANGUAGE.equals("_RU") && conceptFound) {
                    acceptedToken = false;
                } else {
                    acceptedToken = true;
                }
            }
            WORD_DEFINITION.next(this);
        }
        return dbi.compact();
    }

    /** construit une requï¿½te a partir de l'entrï¿½e du parseur. Les indices ne sont pris que dans l'intervalle de lecture
     * la mï¿½thode est protï¿½gï¿½e pour sï¿½quentialiser les parsing concurrent
     * @param id indexeur de rï¿½fï¿½rence
     * @return une requete QL
     *
     */
    synchronized public final String buildConceptualRequest(IdxStructure id) {
        String word;
        int n;
        String request = "(";
        boolean firstAND = true;
        boolean firstOR = true;
        while (!EOFflag) {
            //System.out.println(cw);
            String wordNoStem = cw.toString();
            word = WORD_DEFINITION.normaliseWord(id, cw.toString());
            if (!DontIndexThis.check(word)) {
                acceptedToken = true;
                // ici word est NORMALISE

                n = id.wordstable.get(word);
                //msg(word+" "+n+" "+firstOR+" "+conceptFound+" "+wordFound);
                if (n != -1 && n < id.lastUpdatedWord) { // existing word

                    if (wordFound) {
                        firstOR = true;
                    }
                    if (!firstAND && wordFound && !ACTUAL_LANGUAGE.equals("_RU")) {
                        request += ") AND (";
                    }
                    if (!firstAND && wordFound && ACTUAL_LANGUAGE.equals("_RU")) {// cas du russe

                        request += ") AND ( \"" + word + "\"";
                    }
                    if (firstAND && wordFound && ACTUAL_LANGUAGE.equals("_RU")) {// cas du russe

                        request += "\"" + word + "\"";
                    }
                    if (!firstOR && conceptFound && !wordFound) {
                        request += " OR \"" + word + "\"";
                    }
                    if (firstOR && !conceptFound && wordFound) {
                        request += "\""
                                + stem(id, "_EN", wordNoStem) + "\" OR \"" + stem(id, "_FR", wordNoStem) + "\" OR \"" + stem(id, "_IT", wordNoStem) + "\" OR \"" + stem(id, "_DU", wordNoStem) + "\" OR \"" + stem(id, "_DE", wordNoStem) + "\" OR \"" + stem(id, "_ES", wordNoStem) + "\" OR \"" + stem(id, "_FI", wordNoStem) + "\" OR \"" + stem(id, "_SW", wordNoStem) + "\"";
                        firstOR = false;
                    } // cas d'un mot non reconnu

                    if (firstOR && conceptFound && !wordFound) {
                        request += "\"" + word + "\"";
                        firstOR = false;
                    }
                    firstAND = false;
                }
            } else {
                if (!ACTUAL_LANGUAGE.equals("_RU") && conceptFound) {
                    acceptedToken = false;
                } else {
                    acceptedToken = true;
                }

            }
            WORD_DEFINITION.next(this);
        }
        request += ")";
        return request;
    }

    synchronized public static String stem(IdxStructure id, String lang, String word) {
        if (lang.equals("_EN")) {
            Stemmer.init("english");
        }
        if (lang.equals("_FR")) {
            Stemmer.init("french");
        }
        if (lang.equals("_IT")) {
            Stemmer.init("italian");
        }
        if (lang.equals("_DU")) {
            Stemmer.init("dutch");
        }
        if (lang.equals("_DE")) {
            Stemmer.init("german");
        }
        if (lang.equals("_ES")) {
            Stemmer.init("spanish");
        }
        if (lang.equals("_FI")) {
            Stemmer.init("finnish");
        }
        if (lang.equals("_SW")) {
            Stemmer.init("swedish");
        }
        String res = WORD_DEFINITION.normaliseWord(id, word);
        // recharge le stemmer courant
        if (ACTUAL_LANGUAGE.equals("_EN")) {
            Stemmer.init("english");
        }
        if (ACTUAL_LANGUAGE.equals("_FR")) {
            Stemmer.init("french");
        }
        if (ACTUAL_LANGUAGE.equals("_IT")) {
            Stemmer.init("italian");
        }
        if (ACTUAL_LANGUAGE.equals("_DU")) {
            Stemmer.init("dutch");
        }
        if (ACTUAL_LANGUAGE.equals("_DE")) {
            Stemmer.init("german");
        }
        if (ACTUAL_LANGUAGE.equals("_ES")) {
            Stemmer.init("spanish");
        }
        if (ACTUAL_LANGUAGE.equals("_FI")) {
            Stemmer.init("finnish");
        }
        if (ACTUAL_LANGUAGE.equals("_SW")) {
            Stemmer.init("swedish");
        }
        if (ACTUAL_LANGUAGE.equals("_RU")) {
            Stemmer.init("russian");
        }
        return res;
    }

    /** utilise ce parseur pour le compte d'un rï¿½fï¿½renceur (construire la liste des mots). Les indices ne sont pris que dans l'intervalle de lecture
     * la mï¿½thode est protï¿½gï¿½e pour sï¿½quentialiser les parsing concurrent
     * @param id indexeur de rï¿½fï¿½rence
     * @param s le rï¿½fï¿½renceur faisant la demande
     *
     */
    /*synchronized*/ public final void scanString(IdxStructure id, IdxReference s) {  // utilisï¿½ par le rï¿½fï¿½renceur

        String word;
        int n;
        int idxW;

        while (!EOFflag) {
            //System.out.println(cw);
            idxW = IdxReference.NotIndexed;
            {
                s.word[s.lastscan] = (cw.toString()); // save the value
                s.idxpos[s.lastscan] = poschar;
                   word = WORD_DEFINITION.normaliseWord(id, cw.toString());
                if (cw.toString().charAt(0) > 0x0370 || (word.length() >= WORD_MINLENGTH && !DontIndexThis.check(word))) { // test chinois
                    if (WORD_USE_STEMMER) {
                        word = Stemmer.stemmingOfW(word);
                    }
                    //System.out.println("scan:"+word);
                    n = id.wordstable.get(word);
                    if (n != -1 && n < id.lastUpdatedWord) {
                        idxW = n;
                    } // get the ref}

                }
                s.idxW[s.lastscan] = idxW;
                s.lastscan++;
            }
            if (s.lastscan >= IdxReference.MaxIndexedW) {
                System.out.print("WARNING TRY to overpass ScannedString.MaxIndexedW");
                break;
            }
            WORD_DEFINITION.next(this);
        }  // while

    }

    /** utilise ce parseur pour rï¿½cupï¿½rer une liste d'id de termes (construire la liste des mots). Les indices ne sont pris que dans l'intervalle de lecture
     * la mï¿½thode est protï¿½gï¿½e pour sï¿½quentialiser les parsing concurrent
     * @param id indexeur de rï¿½fï¿½rence
     *
     */
    synchronized public final int[] scanString(IdxStructure id) {  // utilisï¿½ par QL

        String word;
        int lastscan = 0, n;
        int[] idxW = new int[MAX_CITATION]; // max de terme dans une citation

        while (!EOFflag) {
            //System.out.println(cw);
           word = WORD_DEFINITION.normaliseWord(id, cw.toString());
           if (cw.toString().charAt(0) > 0x0370 || (word.length() >= WORD_MINLENGTH && !DontIndexThis.check(word))) { // test chinois
                if (WORD_USE_STEMMER) {
                    word = Stemmer.stemmingOfW(word);
                }
//                System.out.println("debug scan:"+WORD_DEFINITION.toString());
//                System.out.println("debug scan:"+word);
                n = id.wordstable.get(word);
                if (n != -1 && n < id.lastUpdatedWord) {
                    if (lastscan < MAX_CITATION) {
                        idxW[lastscan] = n;
                        lastscan++;
                    } // get the ref}

                } else {
                    return null; // on a trouvï¿½ un terme impossible (JG 16.06.2011)
                }
            }
            WORD_DEFINITION.next(this);
        }  // while

        int[] res = new int[lastscan];
        System.arraycopy(idxW, 0, res, 0, lastscan);
        return res;
    }

    /** utilise ce parseur pour rï¿½cupï¿½rer une liste d'id de termes (construire la liste des mots). Les indices ne sont pris que dans l'intervalle de lecture
     * la mï¿½thode est protï¿½gï¿½e pour sï¿½quentialiser les parsing concurrent
     * @param id indexeur de rï¿½fï¿½rence
     *
     */
    synchronized public final String[] tokenizeString(IdxStructure id) {  // utilisï¿½ par QL
        int MAXWORD = 1000;
        String word;
        int lastscan = 0, n;
        String[] idxW = new String[MAXWORD]; // max de terme dans une phrase

        while (!EOFflag) {
            //System.out.println(cw);
            word = WORD_DEFINITION.normaliseWord(id, cw.toString());
            if (word.length() >= WORD_MINLENGTH && !DontIndexThis.check(word)) {
                if (WORD_USE_STEMMER) {
                    word = Stemmer.stemmingOfW(word);
                }
                //System.out.println("scan:"+word);
                if (lastscan < MAXWORD) {
                    idxW[lastscan] = word;
                    lastscan++;

                }
            }
            WORD_DEFINITION.next(this);
        }  // while

        String[] res = new String[lastscan];
        System.arraycopy(idxW, 0, res, 0, lastscan);
        return res;
    }
}
