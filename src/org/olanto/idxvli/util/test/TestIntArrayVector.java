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

/**
 * 
 * 
 *
 *
 */
public class TestIntArrayVector {

    static IntArrayVector o;

    public static void main(String[] args) {
        String s;
        int[] val3 = new int[3];
        int[] val2 = new int[2];
        int[] val6 = new int[6];
        val3[0] = 13;
        val3[1] = 23;
        val3[2] = 33;
        val2[0] = 12;
        val2[1] = 22;
        o = (new IntArrayVector_InMemory()).create("C:/JG/VLI_RW/objsto", "test", 20, 3);
        o = (new IntArrayVector_InMemory()).open("C:/JG/VLI_RW/objsto", "test");
        o.set(12, val3);
        System.out.println("" + o.get(12)[2]);
        o.set(13, val2);
        System.out.println("" + o.get(13)[1]);
        System.out.println("" + o.get(13, 0));
        o.set(14, val6);
        o.close();
        System.out.println("open again ...");
        o = (new IntArrayVector_InMemory()).open("C:/JG/VLI_RW/objsto", "test");
        System.out.println("" + o.get(12)[2]);
        System.out.println("" + o.get(13)[1]);
        System.out.println("" + o.get(200));
        o.printStatistic();

        // performance test

        o.close();


    }
}
