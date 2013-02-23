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

package org.olanto.idxvli.test;

import org.olanto.idxvli.*;
import org.olanto.idxvli.knn.*;
import org.olanto.util.Timer;
import org.olanto.util.TimerNano;

/**
 * *
 * 
 *
 *
 * Test de l'indexeur, mode query
 */
public class TESTKNNMode_1 {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");
    private static KNNManager KNN;

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY");
        // crï¿½ation de la racine de l'indexation
        id.createComponent(new Configuration());

        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();

        id.Statistic.global();

        Timer t2 = new Timer("init KNN");

        //for (int i=100000;i<101000;i++) msg(i+" : "+id.getOccOfW(i)); // test la fonction de longueur

        KNN = new TFxIDF_ONE();
        KNN.initialize(id, // Indexeur
                5, // Min occurence d'un mot dans le corpus (nbr de documents)
                50, // Max en o/oo d'apparition dans le corpus (par mille!)
                true, // montre les dï¿½tails
                1, // formule IDF (1,2)
                1 // formule TF (1,2,3) toujours 1
                );
        t2.stop();

        test("telecomunication wifi computer", 10);
        test("The broadcast function provided by  the  MPCP  binding  is  limited  to  the "
                + "subnetwork in which it exists. It may form part of  a  multicast  (selective "
                + "broadcast) function within a larger (containing) subnetwork. "
                + "Four types of Multipoint connections Broadcast, Merge, Composite,  and  Full "
                + "Multipoint are shown in Figure  I.1  using  a  MultiPoint  Connection  Point "
                + "(MPCP). The MPCP denotes the Root  of  the  Multipoint  connection  for  the "
                + "Broadcast, Merge, and Composite  types,  where  the  Connection  Point  (CP) "
                + "denotes the leaf. For the Full Multipoint connection,  the  MPCP  denotes  a "
                + "hybrid Root/Leaf. Note that the directionality refers only  to  the  traffic "
                + "flow, the OAM flow are for further study(seeï¿½ITU-T Rec. I.610).", 3);

        t2 = new Timer("init KNN FULL");
        KNN = new TFxIDF();
        KNN.initialize(id, // Indexeur
                5, // Min occurence d'un mot dans le corpus (nbr de documents)
                50, // Max en o/oo d'apparition dans le corpus (par mille!)
                true, // montre les dï¿½tails
                1, // formule IDF (1,2)
                1 // formule TF (1,2,3) toujours 1
                );
        t2.stop();

        test("telecomunication wifi computer", 10);
        test("The broadcast function provided by  the  MPCP  binding  is  limited  to  the "
                + "subnetwork in which it exists. It may form part of  a  multicast  (selective "
                + "broadcast) function within a larger (containing) subnetwork. "
                + "Four types of Multipoint connections Broadcast, Merge, Composite,  and  Full "
                + "Multipoint are shown in Figure  I.1  using  a  MultiPoint  Connection  Point "
                + "(MPCP). The MPCP denotes the Root  of  the  Multipoint  connection  for  the "
                + "Broadcast, Merge, and Composite  types,  where  the  Connection  Point  (CP) "
                + "denotes the leaf. For the Full Multipoint connection,  the  MPCP  denotes  a "
                + "hybrid Root/Leaf. Note that the directionality refers only  to  the  traffic "
                + "flow, the OAM flow are for further study(seeï¿½ITU-T Rec. I.610).", 3);


        t1.stop();

    }

    private static void test(String s, int nTop) {
        TimerNano t1 = new TimerNano("KNN: " + s + " " + nTop, false);
        System.out.println(KNN.searchforKNN(s, nTop));
        t1.stop(false);
    }
}
