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
 */
public class LexicBasic implements LexicManager {

    private HashMap<String, Concepts> tc;
    private HashMap<Integer, Terms> ct;
    private Stem stem;
    private boolean stemActive = true;
    private final boolean verbose = false;

    /** crï¿½er une nouvelle instance de gestionnaire*/
    public LexicBasic() {
    }

    /**  crï¿½e un gestionnaire de lexique */
    public final LexicManager create(String _path, String lang, String stemName) {
        return (new LexicBasic(_path, lang, stemName));
    }

    private LexicBasic(String _path, String lang, String stemName) {  // crï¿½e un gestionnaire
        tc = new HashMap<String, Concepts>();
        ct = new HashMap<Integer, Terms>();
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
        if (stemActive && stem != null) {
            word = stem.stemmingOfW(word.toLowerCase());
        }
        putTC(word, id);
        putCT(word, id);
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

    /**
     * cherche les concepts du mot, retourne NULL s'il n'est pas dans le dictionnaire
     * @param word mot
     * @return un vecteur de concepts
     */
    public final Concepts get(String word) {
        if (stemActive && stem != null) {
            word = stem.stemmingOfW(word);
        }
        if (verbose) {
            msg("get->" + word);
        }
        return tc.get(word);
    }

    public final Concepts get(String stem, String word) {
        error("get not implemented");
        return null;
    }

    /**
     * cherche les mot associï¿½ ï¿½ un identifiant de concepts.
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
        error("getw not implemented");
        return null;
    }

    /**  imprime des statistiques  du gestionnaire de mots*/
    public void printStatistic() {
    }
}
