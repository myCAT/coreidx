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

import org.olanto.idxvli.ql.QResDistributed;
import java.rmi.*;
import org.olanto.idxvli.server.*;
import org.olanto.util.Timer;
import static org.olanto.util.Messages.*;

/** tests des indexeurs distribués
 *  * 
 */
public class SubmitDistributedQuery {

    static int nbNode = 1;
    static Remote[] r;
    static IndexService[] nodes;

    public static void main(String[] args) {
        connectToNode();
        test("napoléon");
        test("hathor");
        test("harvard");
        test("Hélène");
        test("viagra");

    }

    public static void connectToNode() {
        r = new Remote[nbNode];
        nodes = new IndexService[nbNode];
        try {
            for (int i = 0; i < nbNode; i++) {
                int name = i + 1;
                System.out.println("connect to serveur : " + name);
                r[i] = Naming.lookup("rmi://localhost/VLI" + name);
                System.out.println("access to serveur");
                if (r[i] instanceof IndexService) {
                    nodes[i] = ((IndexService) r[i]);
                    String s = nodes[i].getInformation();
                    System.out.println("cha�ne renvoy�e par " + name + " = " + s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test(String s) {
        Timer t1 = new Timer("distribute:" + s, false);
        QLResultAndRank[] res = new QLResultAndRank[nbNode];
        for (int i = 0; i < nbNode; i++) {  // cette partie doit �tre revue pour �tre parall�le et asynchrone !!!!!!
            try {
                res[i] = nodes[i].evalQLMore(s);
                if (res[i] != null && res[i].result != null) {
                    msg("node " + i + " time:" + res[i].duration + " #res:" + res[i].result.length);
                } else {
                    msg("node " + i + " empty result");
                }
            } catch (RemoteException ex) {
                error("during query process ", ex);
            }
        }
        QResDistributed globalRes = new QResDistributed(res, 100);
        if (globalRes == null) {
            msg("global result is null");
        } else {
            msg("global result is ndoc:" + globalRes.topdoc.length);
            for (int i = 0; i < globalRes.topdoc.length; i++) {
                msg(" doc " + globalRes.topdoc[i] + " from node " + globalRes.topsource[i] + " : " + globalRes.topname[i]);
            }
        }
        t1.stop();


    }
}
