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

package org.olanto.idxvli.util.test;

import org.olanto.idxvli.util.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 * 
 * 
 *
 *
 */
public class TestByteArrayVector {

    static ByteArrayVector o;

    public static void main(String[] args) {
        String s;
        byte[] val3 = new byte[3];
        byte[] val2 = new byte[2];
        byte[] val6 = new byte[6];
        val3[0] = 13;
        val3[1] = 23;
        val3[2] = 33;
        val2[0] = 12;
        val2[1] = 22;
        o = (new ByteArrayVector_OnDisk()).create("C:/JG/gigaversion/data/objsto", "test", 20, 3);
        o = (new ByteArrayVector_OnDisk()).open("C:/JG/gigaversion/data/objsto", "test", readWriteMode.rw);
        showVector(val3);
        o.set(12, val3);
        showVector(o.get(12));
        o.set(13, val2);
        System.out.println("" + o.get(13)[1]);
        System.out.println("13,0:" + o.get(13, 0));
        o.set(14, val6);
        o.clear(14);
        o.close();
        System.out.println("open again ...");
        o = (new ByteArrayVector_OnDisk()).open("C:/JG/gigaversion/data/objsto", "test", readWriteMode.r);
        System.out.println("" + o.get(12)[2]);
        System.out.println("" + o.get(13)[1]);
        System.out.println("" + o.get(200));
        o.printStatistic();

        // performance test

        o.close();


    }
}
