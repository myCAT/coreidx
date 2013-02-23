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

/* générique test pour les différentes implémenations remplacer le nom de l'implémentation .... */
import org.olanto.idxvli.util.*;

/**
 * 
 * 
 *
 *
 */
public class TestStringRepository {

    static StringRepository o;

    public static void main(String[] args) {
        String s;
        int i;
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).create("C:/JG/gigaversion/data/objsto", "test", 14, 32);
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).open("C:/JG/gigaversion/data/objsto", "test");
        s = "voilà";
        System.out.println(s + ":" + o.put(s));
        s = "un";
        System.out.println(s + ":" + o.put(s));
        s = "test";
        System.out.println(s + ":" + o.put(s));
        s = "très";
        System.out.println(s + ":" + o.put(s));
        s = "simple";
        System.out.println(s + ":" + o.put(s));
        s = "test";
        System.out.println(s + " est cherché:" + o.get(s));
        s = "un";
        System.out.println(s + " est cherché:" + o.get(s));
        s = "oups";
        System.out.println(s + " est cherché:" + o.get(s));
        i = 2;
        System.out.println(i + " est cherché:" + o.get(i));
        i = 0;
        System.out.println(i + " est cherché:" + o.get(i));
        i = 99;
        System.out.println(i + " est cherché:" + o.get(i));
        o.close();
        System.out.println("open again ...");
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).open("C:/JG/gigaversion/data/objsto", "test");
        s = "simple";
        System.out.println(s + " est cherché:" + o.get(s));
        o.printStatistic();


        o.close();

//        StringTable_HomeHash_OnDisk_DIO_Clue x=new StringTable_HomeHash_OnDisk_DIO_Clue();
//        System.out.println(x.hdocclue(1,1));

    }
}
