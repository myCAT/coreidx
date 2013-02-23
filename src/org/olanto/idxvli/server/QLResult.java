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

package org.olanto.idxvli.server;

import java.io.*;

/**
 * Classe stockant les  résultats d'une requête.
 * <p>
 * 
 *
 */
public class QLResult implements Serializable {

    /* les documents r�sultats */
    public int[] result;
    /* la dur�e d'ex�cution */
    public long duration;  // en ms

    /** cr�e un r�sultat
     * @param result id des documents
     * @param duration dur�e
     */
    public QLResult(int[] result, long duration) {
        this.result = result;
        this.duration = duration;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(result);
        out.writeLong(duration);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        result = (int[]) in.readObject();
        duration = in.readLong();
    }
}
