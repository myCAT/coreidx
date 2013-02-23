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

import static java.util.Arrays.*;
import org.olanto.idxvli.*;
import java.io.*;

/**
 * Classe stockant les  Concepts associés à un terme.
 * <p>
 * 
 *<p>
 *
 */
public class Concepts implements Serializable {

    /* les concepts */
    public int[] c;

    /** crée une liste
     * @param c une liste externe
     */
    public Concepts(int[] c) {
        this.c = c;
    }

    /** crée une liste vide
     */
    public Concepts() {
        this.c = null;
    }

    public static final Concepts getConcept(IdxStructure id, ManyOntologyManager ontologyLib, String lang, String word) {
        LexicManager currentLexic = ontologyLib.get(lang);
        String stemOfWord = DoParse.stem(id, lang, word);
        Concepts concepts = currentLexic.get(stemOfWord);
        return concepts;
    }

    public void add(int id) {
        if (c == null) {
            c = new int[1];
            c[0] = id;
        } else {
            if (binarySearch(c, id) < 0) { // pas dans la liste
                int[] copy = new int[c.length + 1];
                System.arraycopy(c, 0, copy, 0, c.length);
                copy[c.length] = id;
                sort(copy);
                c = copy;
            }
        }
    }

    public void add(int[] id) {
        for (int i = 0; i < id.length; i++) {
            add(id[i]);
        }
    }

    public int length() {
        if (c == null) {
            return 0;
        } else {
            return c.length;
        }
    }

    public boolean testIn(int id) {
        if (c == null) {
            return false;
        } else {
            if (binarySearch(c, id) < 0) { // pas dans la liste
                return false;
            } else {
                return true;
            }
        }
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(c);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        c = (int[]) in.readObject();
    }
}
