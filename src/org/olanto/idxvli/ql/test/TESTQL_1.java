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

package org.olanto.idxvli.ql.test;

import org.olanto.idxvli.*;
import static org.olanto.util.Messages.*;

/**Test de l'indexeur, mode query
 * 
 *
 */
public class TESTQL_1 {

    private static IdxStructure id;

    public static void main(String[] args) {
        id = new IdxStructure("QUERY", new ConfigurationQL());
        id.Statistic.global();
        test("coconut AND rice");
    }

    public static void test(String s) {
        int[] res = id.executeRequest(s);
        if (res == null) {
            msg("result is null");
        } else {
            msg("result is ndoc:" + res.length);
        }
        showVector(res);
    }
}
