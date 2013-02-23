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

package org.olanto.server.test;

import java.io.*;
import org.olanto.idxvli.*;

/**Test de l'indexeur, mode query 
 * 
  *
 */
public class GetWord {

    private static IdxStructure id;

    public static void main(String[] args) {
        id = new IdxStructure("QUERY", new ConfigurationNative());
        id.Statistic.global();
        //id.Statistic.topIndexByLength(0);
        exportEntry("c:/AAA/BLAISE/exportTerm.cat");
    }

    static void exportEntry(String filename) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename), "ISO-8859-1");
            //        FileWriter out= new FileWriter(filename,"UTF-8");
            for (int i = 0; i < id.lastUpdatedWord; i++) {
                String entry = id.getStringforW(i);
                int occofW = id.getOccCorpusOfW(i);
                if (occofW > 0) {
                    out.write(entry + "\t" + occofW + "\t" + "\n");
                }
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("IO error ExportEntry");
        }
    }
}
