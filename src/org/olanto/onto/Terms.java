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
import java.io.*;

/**
 * Classe stockant les  termes associés à un concept.
 * <p>
 * 
 *
 */
public class Terms implements Serializable {

    /* les termes */
    public String[] t;

    /** cr�e une liste
     * @param t une liste externe
     */
    public Terms(String[] t) {
        this.t = t;
    }

    /** crée une liste vide
     */
    public Terms() {
        this.t = null;
    }

    public String dump() {
        if (t == null) {
            return null;
        }
        String s = "";
        for (int i = 0; i < t.length; i++) {
            s += t[i] + " / ";
        }
        return s;
    }

    public void add(String id) {
        if (t == null) {
            t = new String[1];
            t[0] = id;
        } else {
            if (binarySearch(t, id) < 0) { // pas dans la liste
                String[] copy = new String[t.length + 1];
                System.arraycopy(t, 0, copy, 0, t.length);
                copy[t.length] = id;
                sort(copy);
                t = copy;
            }
        }
    }

    public boolean testIn(String id) {
        if (t == null) {
            return false;
        } else {
            if (binarySearch(t, id) < 0) { // pas dans la liste
                return false;
            } else {
                return true;
            }
        }
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(t);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        t = (String[]) in.readObject();
    }
}
