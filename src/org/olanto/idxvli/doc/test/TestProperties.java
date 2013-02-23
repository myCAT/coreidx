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

package org.olanto.idxvli.doc.test;

import org.olanto.idxvli.doc.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;

/** tests.
 * 
 *
 *
 */
public class TestProperties {

    static PropertiesManager o;

    public static void main(String[] args) {
        implementationMode imp = implementationMode.BIG;
        String s;
        o = (new Properties1()).create(imp, "C:/JG/gigaversion/data/objsto", "test", 10, 32, 20);
        o = (new Properties1()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.rw);
        msg("set test");
        o.put("_EN", 14);
        o.put("_FR", 15000);

        o.close();
        o = (new Properties1()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.rw);
        msg("get test");
        msg("_EN,14" + o.get("_EN", 14));
        msg("_EN,15000" + o.get("_EN", 15000));
        msg("_FR,15000" + o.get("_FR", 15000));
        msg("_FR,14" + o.get("_FR", 14));
        o.printStatistic();
        o.close();

        msg("------------------------READ_ONLY-----------------------------------------------");
        o = (new Properties1()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.r);
        o.close();


    }
}
