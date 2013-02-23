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
import org.olanto.util.Timer;

/**
 * 
 * 
 *
 *
 */
public class TestBitVector {

    static BitVector o;

    public static void main(String[] args) {
        String s;
        o = (new BitVector_InMemoryZIP()).create("C:/JG/gigaversion/data/objsto", "test", 1024 * 1024);
        o = (new BitVector_InMemoryZIP()).open("C:/JG/gigaversion/data/objsto", "test");
        int i = 0;
        boolean b = false;
        i = 0;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 12;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 13;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 31;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 32;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 63;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        o.close();
        System.out.println("open again ...");
        o = (new BitVector_InMemoryZIP()).open("C:/JG/gigaversion/data/objsto", "test");
        System.out.println(o.get(12));
        System.out.println(o.get(23));
        i = 0;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 12;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 13;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 31;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 32;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 63;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 0;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 12;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 13;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 31;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 32;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 63;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 0;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 12;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 13;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 31;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 32;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 63;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        o.printStatistic();

        Timer t1 = new Timer("IntVector get");
        for (int k = 0; k < 100000; k++) {
            for (int j = 0; j < 1024; j++) {
                boolean x = o.get(j);
            }
        }
        t1.stop();

        o.close();


    }
}
