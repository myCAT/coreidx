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

package org.olanto.idxvli.mapio.test;

import java.io.IOException;
import static java.lang.Math.*;
import org.olanto.idxvli.mapio.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.util.Messages.*;
import org.olanto.util.Timer;

/**
 * <p>tests de base (�criture lecture des int ).
 * 

 * 
 *
 */
public class Test_2 {

    /**
     * @param args the command line arguments
     */
    static int fileSize = (int) pow(2, 20);

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 1; i++) {
                String fname;
                msg("\nTest DirectIOFile fileSize:" + fileSize);
                DirectIOFile file = new MappedFile();
                fname = "c:/temp/mapfile_direct_ajeter.data";
                testWrite(file, fname, MappingMode.FULL);
                System.gc(); // efface les caches
                testRead(file, fname, MappingMode.FULL);
//                test3(file, fname);
//                test4(file, fname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testWrite(DirectIOFile file, String fileName, MappingMode mapMode)
            throws IOException {
        Timer t = new Timer("----- " + file.getClass().getName());
        file.open(fileName, mapMode, readWriteMode.rw, 10, fileSize);
        file.seek(0);
        for (int i = 0; i < fileSize / 4; i++) {
            file.writeInt(i);
        }
        msg("taille max:" + file.getMaxLength());
        file.close();
        t.stop();
    }

    private static void testRead(DirectIOFile file, String fileName, MappingMode mapMode)
            throws IOException {
        Timer t = new Timer("----- " + file.getClass().getName());
        file.open(fileName, mapMode, readWriteMode.r, 10, fileSize);
        file.seek(0);
        for (int i = 0; i < fileSize / 4; i++) {
            int res = file.readInt();
            if (res != i) {
                error("re-read wait:" + i + " read:" + res);
            }
        }
        file.close();
        t.stop();
    }
}
