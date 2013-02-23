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

package org.olanto.idxvli.jjbg.test;

import org.olanto.idxvli.jjbg.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 * test de base.
 * 

 *
 *
 */
public class TestObjectStore4_Async {

    static ObjectStorage4 o;

    public static void main(String[] args) {

        implementationMode mode = implementationMode.BIG;

        o = (new ObjectStore4_Async()).create(mode, "C:/JG/VLI_RW/objsto", 20, 32);
        o.close();
        o = o.open(mode, "C:/JG/VLI_RW/objsto", readWriteMode.rw);

        int[] big = new int[100];
        big[1] = 51;
        big[2] = 52;

        msg("---------------------------------------------------------------");
        showVector(big);
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 1000; i++) {
                o.append(big, i, big.length);
            }
        }
        o.close();

    }
}
