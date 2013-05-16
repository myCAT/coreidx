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
import java.util.*;

/**
 * Une classe pour gérer des listes de termes.
 * Les listes de stop words sont gérées ainsi
 *
 * 
 */
public class SetOfWords {

    HashMap<String, Integer> words = new HashMap<String, Integer>();
    int count = 0;

    /**
     * création d'une liste chargée depuis un fichier.
     * Chaque ligne du fichier est un terme de la liste
     * 
     * @param fname nom du fichier
     */
    public SetOfWords(String fname) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            while (w != null) {
                //System.out.println("dont index:"+w);
                w=w.trim(); // to clean some space
                words.put(w, count);
                w = in.readLine();
            }
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
    }

    /** vérifie si un terme appartient à la liste.
     * @param w terme à vérifier
     * @return vrai si dans la liste
     */
    public final boolean check(String w) {
        //System.out.println("test dont index:"+w+" . "+words.get(w));
        if (words.get(w) == null) {
            return false;
        }
        return true;
    }

    /** vérifie si un terme appartient à la liste.
     * @param w terme à vérifier
     * @return vrai si dans la liste
     */
    public final boolean check(StringBuffer w) {
        return check(w.toString());
    }

    /** vérifie si un terme appartient à la liste.
     * @param w terme à vérifier
     * @return vrai si dans la liste
     */
    public final String[] getListOfWords() {
        String[] result = new String[words.size()];
        words.keySet().toArray(result);
        return result;
    }
}
