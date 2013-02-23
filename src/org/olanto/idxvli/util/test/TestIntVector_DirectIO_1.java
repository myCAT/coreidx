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
public class TestIntVector_DirectIO_1 {

    static IntVector o;

    public static void main(String[] args) {
        String s;
        o = (new IntVector_InMemory()).create("C:/JG/gigaversion/data/objsto", "test", 20);
        o = (new IntVector_InMemory()).open("C:/JG/gigaversion/data/objsto", "test");
        o.set(12, 1212);
        System.out.println(+o.get(12));
        o.set(23, 2323);
        System.out.println(+o.get(23));
        o.close();
        System.out.println("open again ...");
        o = (new IntVector_InMemory()).open("C:/JG/gigaversion/data/objsto", "test");
        System.out.println(+o.get(12));
        System.out.println(+o.get(0));
        o.printStatistic();

        // performance test
        int res;
        int outer = 100;
        Timer t1;
        int[] v = new int[1024 * 1024];
        t1 = new Timer("IntVector set");
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                o.set(j, j);
            }
        }
        t1.stop();
        t1 = new Timer("Int[] set");
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                v[j] = j;
            }
        }
        t1.stop();
        t1 = new Timer("IntVector get");
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                res = o.get(j);
            }
        }
        t1.stop();
        t1 = new Timer("Int[] get");
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                res = v[j];
            }
        }
        t1.stop();

        o.close();


    }
}
