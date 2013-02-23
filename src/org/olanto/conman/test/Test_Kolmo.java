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

package org.olanto.conman.test;

import org.olanto.conman.*;
import org.olanto.util.Timer;
import static org.olanto.util.Messages.*;

/** Test de de la distance de kolmogorov
 * 
 *
 * 
 */
public class Test_Kolmo {

    private static ContentStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new ContentStructure("QUERY", new ConfigurationContentManager());
        boolean bzip2 = false;

        for (int i = 0; i < id.lastdoc; i++) {
            msg("id=" + i + " - " + id.getFileNameForDocument(i));
        }


        msg("doc 0 = " + id.getFileNameForDocument(0));
        msg("doc 1 = " + id.getFileNameForDocument(1));

        msg("dist 0 0 =" + id.distOfKolmogorov(0, 0, bzip2));
        msg("dist 0 1 =" + id.distOfKolmogorov(0, 1, bzip2));
        msg("dist 1 0 =" + id.distOfKolmogorov(1, 0, bzip2));
        msg("dist 1 1 =" + id.distOfKolmogorov(1, 1, bzip2));

        t1 = new Timer("global time");
        int k = 500;
        for (int i = 0; i < id.lastdoc; i++) {
            msg("dist\t" + k + "\t" + i + "\t" + id.distOfKolmogorov(k, i, bzip2) + "\t" + id.getFileNameForDocument(i));
        }
        t1.stop();

        id.Statistic.global();






    }
}
