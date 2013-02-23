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

/**
 * 
 * 
 *
 *
 */
public class TestStringRepository_Stress_Get {

    public static void main(String[] args) {


        try {

            System.out.println("connect to serveur");

            Remote r = Naming.lookup("rmi://jg4/TERMDICT");

            System.out.println("access to serveur");

            if (r instanceof DictionnaryService) {
                DictionnaryService is = ((DictionnaryService) r);
                String s = is.getInformation();
                System.out.println("cha�ne renvoy�e = " + s);
                Timer t0 = new Timer("global get");
                Timer t1 = new Timer("get 0");
                int max = 1000000000;
                int delta = 10000;
                double maxintable = 100000;
                for (int i = max; i > 0; i--) {
                    if (i % delta == 0) {
                        t1.stop();
                        t1 = new Timer("get:" + (i / delta + 1));
                    }
                    int rdn = (int) (Math.random() * maxintable);
                    is.get(String.valueOf(i));
                }
                t0.stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
