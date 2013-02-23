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
public class TestIntVector_DirectIO_2 {

    static IntVector o;

    public static void main(String[] args) {
        String s;
        int max = 27;
        o = (new IntVector_DirectIO()).create("C:/JG/gigaversion/data/objsto", "test", max);
        o = (new IntVector_DirectIO()).open("C:/JG/gigaversion/data/objsto", "test");
        int last = (int) Math.pow(2, max);
        for (int i = 0; i < last; i++) {
            o.set(i, -1);
        }
        o.close();
        System.out.println("open again ...");
        o = (new IntVector_DirectIO()).open("C:/JG/gigaversion/data/objsto", "test");
        System.out.println(+o.get(12));
        System.out.println(+o.get(0));
        System.out.println(+o.get(last - 1));
        o.printStatistic();

        o.close();


    }
}
