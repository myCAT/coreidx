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

package org.olanto.idxvli.distibuted.test;

import org.olanto.idxvli.*;
import org.olanto.util.Timer;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import static org.olanto.util.Messages.*;

/** export les mots indexés
 * 
 */
public class ExportAllVocabulary {

    // classe charg�e de stresser les query
    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY");
        // cr�ation de la racine de l'indexation
        id.createComponent(new ConfigurationNative1());

        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();

        id.Statistic.global();

        String filename = "C:/bigvocabulary.txt";
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename));
            int last = id.lastUpdatedWord;

            for (int i = 0; i < last; i++) {
                out.write(id.getStringforW(i) + "\n");
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            error("io", ex);
        }
        t1.stop();
    }
}
