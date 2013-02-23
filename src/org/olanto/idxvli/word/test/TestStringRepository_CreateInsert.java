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

package org.olanto.idxvli.word.test;

/* g�n�rique test pour les diff�rentes impl�menations remplacer le nom de l'impl�mentation .... */
import org.olanto.util.Timer;
import java.rmi.*;
import org.olanto.idxvli.word.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 * 
 * 
 *
 *
 */
public class TestStringRepository_CreateInsert {

    public static void main(String[] args) {


        try {

            System.out.println("connect to serveur");

            Remote r = Naming.lookup("rmi://jg4/TERMDICT");

            System.out.println("access to serveur");

            if (r instanceof DictionnaryService) {
                DictionnaryService is = ((DictionnaryService) r);
                String s = is.getInformation();
                System.out.println("cha�ne renvoy�e = " + s);
                msg("create");
                is.create(implementationMode.XL, "C:/JG/VLI_RW/data/dictio", "test", 25, 32);
                msg("open");
                is.open(implementationMode.XL, readWriteMode.rw, "C:/JG/VLI_RW/data/dictio", "test");
                Timer t0 = new Timer("global put");
                Timer t1 = new Timer("put 0");
                int max = 100000;
                for (int i = 1; i < max; i++) {
                    if (i % 1000 == 0) {
                        t1.stop();
                        t1 = new Timer("put:" + (i / 1000 + 1));
                    }
                    is.put(String.valueOf(i));
                }
                t0.stop();
                is.printStatistic();
                is.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
