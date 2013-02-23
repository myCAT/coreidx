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
public class TestObjectStore4 {

    static ObjectStorage4 o;

    public static void main(String[] args) {

        implementationMode mode = implementationMode.BIG;

        o = (new ObjectStore4()).create(mode, "C:/JG/gigaversion/data/objsto", 18, 32);
        o = o.open(mode, "C:/JG/gigaversion/data/objsto", readWriteMode.rw);

        int[] big = new int[5];
        big[1] = 51;
        big[2] = 52;
        int bigref = 5;
        int[] small = new int[3];
        small[2] = 32;
        int smallref = 6;

        msg("---------------------------------------------------------------");
        showVector(big);
        o.append(big, bigref, big.length);
        o.printNiceId(bigref);
        msg("5 bigreal:" + o.realSize(bigref) + " bigstore:" + o.storedSize(bigref));
        showVector(o.readInt(bigref));
        msg("---------------------------------------------------------------");
        msg("" + o.append(small, smallref, small.length));
        o.printNiceId(smallref);
        showVector(o.readInt(smallref));
        msg("----append big---------------------------------------------");
        o.append(big, bigref, big.length);
        o.printNiceId(bigref);
        showVector(o.readInt(bigref));
        msg("10 bigreal:" + o.realSize(bigref) + " bigstore:" + o.storedSize(bigref));
        o.append(big, bigref, big.length);
        o.printNiceId(bigref);
        showVector(o.readInt(bigref));
        msg("15 bigreal:" + o.realSize(bigref) + " bigstore:" + o.storedSize(bigref));
        msg("----append small---------------------------------------------");
        int[] unit = new int[1];
        unit[0] = 11;
        o.append(unit, smallref, unit.length);
        o.printNiceId(smallref);
        showVector(o.readInt(smallref));
        unit[0] = 12;
        o.append(unit, smallref, unit.length);
        o.printNiceId(smallref);
        showVector(o.readInt(smallref));

        o.append(unit, smallref + 1, unit.length);
        o.printNiceId(smallref + 1);
        showVector(o.readInt(smallref + 1));
        o.printStatistic();

        o.close();
        o = o.open(mode, "C:/JG/gigaversion/data/objsto", readWriteMode.rw);
        o.printNiceId(smallref);
        showVector(o.readInt(smallref));
        o.printNiceId(smallref + 1);
        showVector(o.readInt(smallref + 1));
        msg("bigreal:" + o.realSize(bigref) + " bigstore:" + o.storedSize(bigref));

        o.close();


    }
}
