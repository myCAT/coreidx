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

package org.olanto.profil;

import org.olanto.idxvli.*;
import org.olanto.idxvli.knn.*;
import org.olanto.util.Timer;
import static org.olanto.util.Messages.*;
import org.olanto.util.TimerNano;

/**
 * *
 * <p>
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
        int ntop = 100;
//        test1("telecomunication wifi computer",10);
//        test1("The broadcast function provided by  the  MPCP  binding  is  limited  to  the "+
//                "subnetwork in which it exists. It may form part of  a  multicast  (selective "+
//                "broadcast) function within a larger (containing) subnetwork. "+
//                "Four types of Multipoint connections Broadcast, Merge, Composite,  and  Full "+
//                "Multipoint are shown in Figure  I.1  using  a  MultiPoint  Connection  Point "+
//                "(MPCP). The MPCP denotes the Root  of  the  Multipoint  connection  for  the "+
//                "Broadcast, Merge, and Composite  types,  where  the  Connection  Point  (CP) "+
//                "denotes the leaf. For the Full Multipoint connection,  the  MPCP  denotes  a "+
//                "hybrid Root/Leaf. Note that the directionality refers only  to  the  traffic "+
//                "flow, the OAM flow are for further study(seeï¿½ITU-T Rec. I.610)."
//                ,ntop);
//        
//        test1("The present invention relates to an arrangement for a hand tool (1), for example a spade, a pitchfork or a shovel. The tool comprises a shaft (2), which at one of its ends is attached to a tool component (3) and at its other end is executed with a handle (4). The invention is characterized in that the hand tool component (3) is attached to the shaft (2) in such a way that its principal direction of longitudinal extension (5) forms an angle (v1) of the order of 140ï¿½-150ï¿½ with the shaft (2) at the attachment. The length of the shaft (2) is of the order of 1-1.5 m, and is executed at a point approximately half way along its length with a bend (6), so that the part (2a) of the shaft (2) between the handle (4) and the bend (6) forms an angle (v2) with the part (2b) of the shaft (2) situated between the bend (6) and the tool attachment. The last-mentioned angle (v2) is selected so that the first-mentioned part (2a) of the shaft (2) is approximately parallel with the aforementioned principal direction of longitudinal extension of the tool component (3)"
//                ,ntop);
        msg("le meuble");
        test1("A table is a form of furniture composed of a horizontal surface supported by a base, usually four legs. It is often used to hold objects or food at a convenient or comfortable height when sitting. Generic tables are typically meant for combined use with chairs. Unlike many earlier table designs, today's tables usually do not have drawers. A table specifically intended for working is a desk. Some tables have hinged extensions of the table top called drop leaves, while others can be extended with removable sections called leaves.", ntop);
        msg("l'information");
        test1("A table is both a mode of visual communication and a means of arranging data. The use of tables is pervasive throughout all communication, research and data analysis. Tables appear in print media, handwritten notes, computer software, architectural ornamentation, traffic signs and many other places. The precise conventions and terminology for describing tables varies depending on the context. Moreover, tables differ significantly in variety, structure, flexibility, notation, representation and use.", ntop);
        msg("la base de donnï¿½es");
        test1("In relational databases, SQL databases, and flat file databases, a table is a set of data elements (values) that is organized using a model of horizontal rows and vertical columns. The columns are identified by name, and the rows are identified by the values appearing in a particular column subset which has been identified as a candidate key. Table is the lay term for relation. A table has a specified number of columns but can have any number of rows. Besides the actual data rows, tables generally have associated with them some meta-information, such as constraints on the table or on the values within particular columns.", ntop);
    }

    private static void test(String s, int nTop) {
        TimerNano t1 = new TimerNano("KNN: " + s + " " + nTop, false);
        KNN.showKNN(KNN.getKNN(s, nTop));
        t1.stop(false);
    }

    private static void test1(String s, int nTop) {
        TimerNano t1 = new TimerNano("KNN: " + s + " " + nTop, false);
        float[] sim = KNN.getSimilarity(s);
        showSIM(sim, 0.002f);
        showDIST(sim);
        KNN.showKNNWithName(KNN.getKNN(s, nTop));
        t1.stop(false);
    }

    public final static void showSIM(float[] p, float min) {
        if (p != null) {
            int l = p.length;
            int notnull = 0;
            for (int i = 0; i < l; i++) {
                if (p[i] >= min) {
                    msgnoln(p[i] + "\n");
                    notnull++;
                }
            }
            msg("Length=" + l + " notnull=" + notnull);
        } else {
            msg("Is null");
        }
    }

    public final static void showDIST(float[] p) {
        if (p != null) {
            int l = p.length;
            int not1 = 0;
            int not2 = 0;
            int not3 = 0;
            int not4 = 0;
            int not5 = 0;
            int not6 = 0;
            for (int i = 0; i < l; i++) {
                if (p[i] > 0.1) {
                    not1++;
                } else if (p[i] > 0.01) {
                    not2++;
                } else if (p[i] > 0.001) {
                    not3++;
                } else if (p[i] > 0.0001) {
                    not4++;
                } else if (p[i] > 0.00001) {
                    not5++;
                } else {
                    not6++;
                }
            }
            msg("Length=" + l
                    + "\n> 0.1 =" + not1
                    + "\n> 0.01 =" + not2
                    + "\n> 0.001 =" + not3
                    + "\n> 0.0001 =" + not4
                    + "\n> 0.00001 =" + not5
                    + "\n<= 0.00001 =" + not6);
        } else {
            msg("Is null");
        }
    }
}
