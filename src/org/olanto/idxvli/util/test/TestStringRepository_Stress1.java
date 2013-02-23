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

/* g�n�rique test pour les diff�rentes impl�menations remplacer le nom de l'impl�mentation .... */
import org.olanto.idxvli.util.*;
import org.olanto.util.Timer;

/**
 * 
 * 
 *
 *
 */
public class TestStringRepository_Stress1 {

    static StringRepository o;

    public static void main(String[] args) {
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).create("C:/JG/gigaversion/data/objsto", "test", 27, 32);
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).open("C:/JG/gigaversion/data/objsto", "test");
        Timer t0 = new Timer("global put");
        Timer t1 = new Timer("put 0");
        int max = 1000000;
        for (int i = 1; i < max; i++) {
            if (i % 100000 == 0) {
                t1.stop();
                t1 = new Timer("put:" + (i / 100000 + 1));
            }
            o.put(String.valueOf(i));
        }
        t0.stop();
        o.printStatistic();
        o.close();
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).open("C:/JG/gigaversion/data/objsto", "test");
        t0 = new Timer("global get");
        t1 = new Timer("get 0");
        for (int i = max; i > 0; i--) {
            if (i % 100000 == 0) {
                t1.stop();
                t1 = new Timer("get:" + (i / 100000 + 1));
            }
            o.get(String.valueOf(i));
        }
        t0.stop();
//          for (int i=1;i<max/1000;i++){
//           System.out.println(o.get(i));
//        }
        o.printStatistic();


    }
}
