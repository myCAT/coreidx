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

package org.olanto.onto;

import java.util.*;
import java.io.*;
import org.olanto.idxvli.extra.*;
import static org.olanto.util.Messages.*;

/**
 *  gestionnaire de lexique.
 *
 * 
 *
 *
 *  // on doit encore ajouter un cache !!!
 */
public class LexicBetter implements LexicManager {

    // t est un lemme, c est un concept, w est sans stemming 
    private HashMap<String, Concepts> tc;
    private HashMap<Integer, Terms> ct;
    private HashMap<Integer, Terms> cw;
    private Stem stem;
    private boolean stemActive = true;
    private final boolean verbose = false;

    /** crï¿½er une nouvelle instance de gestionnaire*/
    public LexicBetter() {
    }

    /**  crï¿½e un gestionnaire de lexique */
    public final LexicManager create(String _path, String lang, String stemName) {
        return (new LexicBetter(_path, lang, stemName));
    }

    private LexicBetter(String _path, String lang, String stemName) {  // crï¿½e un gestionnaire
        tc = new HashMap<String, Concepts>();
        ct = new HashMap<Integer, Terms>();
        cw = new HashMap<Integer, Terms>();
        if (stemName == null) {
            stem = null;
        } else {
            stem = new Stem(stemName);
        }
        getFromFile(_path);
    }

    private void getFromFile(String _path) {
        msg("load ontology :" + _path);
        String w = null;
        int conceptid = 0;
        int count = 0;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(_path), "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            w = in.readLine();
            while (w != null) {
                count++;
                //msg(w);
                if (w.indexOf("***") == -1) { // ne contient pas le symbole d'invalidation
                    int sep = w.indexOf(";");
                    if (sep != -1) {
                        try {
                            conceptid = Integer.valueOf(w.substring(0, sep));
                            String term = w.substring(sep + 1, w.length());
                            //msg(conceptid+","+term);
                            put(term, conceptid);
                        } catch (Exception e) {
                            msg(" ************* malformed number :" + count + " / " + w);
                        }
                    } else {
                        msg("malformed line :" + count + " / " + w);
                    }
                }
                w = in.readLine();
            }
        } catch (Exception e) {
            error("getFromFile line :" + count + " / " + w, e);
        }
        msg("#concept=" + count);
    }

    /**
     * ajoute un mot au gestionnaire
     * retourne son id s'il existe dï¿½ja
     * @param word mot
     * @param id id
     */
    public void put(String word, int id) {
        String sw;
        if (stemActive && stem != null) {
            sw = stem.stemmingOfW(word.toLowerCase());
        } else {
            sw = word;
        }
        putTC(sw, id);
        putCT(sw, id);
        putCW(word, id);
    }

    private void putTC(String word, int id) {
        Concepts c = tc.get(word);
        if (c == null) { // nouveau
            c = new Concepts();
            c.add(id);
            tc.put(word, c);
        } else { // existe dï¿½jï¿½
            c.add(id);
        }
    }

    private void putCT(String word, int id) {
        Terms t = ct.get(id);
        if (t == null) { // nouveau
            t = new Terms();
            t.add(word);
            ct.put(id, t);
        } else { // existe dï¿½jï¿½
            t.add(word);
        }
    }

    private void putCW(String word, int id) {
        Terms t = cw.get(id);
        if (t == null) { // nouveau
            t = new Terms();
            t.add(word);
            cw.put(id, t);
        } else { // existe dï¿½jï¿½
            t.add(word);
        }
    }

    /**
     * cherche les concepts du mot, retourne NULL s'il n'est pas dans le dictionnaire
     * @param word stemming dï¿½jï¿½ appliquï¿½
     * @return un vecteur de concepts
     */
    public final Concepts get(String word) {
        if (verbose) {
            msg("get stem->" + word);
        }
        return tc.get(word);
    }

    public final Concepts get(String stem, String word) {
        if (verbose) {
            msg("get word->" + word);
        }
        Concepts stemConcepts = tc.get(stem);
        for (int i = word.length(); i > stem.length(); i--) {
            Concepts matchC = filter(stemConcepts, word.substring(0, i));
            if (matchC != null) {
                return matchC;
            }
        }
        return stemConcepts;
    }

    private final Concepts filter(Concepts test, String match) {
        if (verbose) {
            msg("match:" + match);
        }
        if (test == null) {
            return null;
        }
        Concepts res = new Concepts();
        for (int i = 0; i < test.c.length; i++) {
            if (getw(test.c[i]).testIn(match)) {
                res.add(test.c[i]);
            }
        }
        if (res.c == null) {
            return null;
        } else {
            return res;
        }

    }

    /**
     * cherche les mots associï¿½s ï¿½ un identifiant de concepts.
     * @param i numï¿½ro du concepts
     * @return la liste des termes
     */
    public Terms get(int i) {
        return ct.get(i);
    }

    /**
     * cherche les mots sans stemming associï¿½s ï¿½ un identifiant de concepts.
     * @param i numï¿½ro du concepts
     * @return la liste des termes
     */
    public Terms getw(int i) {
        return cw.get(i);
    }

    /**  imprime des statistiques  du gestionnaire de mots*/
    public void printStatistic() {
    }
}
