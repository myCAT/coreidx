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
import java.io.*;
import org.olanto.idxvli.ql.*;
import org.olanto.idxvli.server.QLResultAndRank;
import static org.olanto.util.Messages.*;
import org.olanto.util.TimerNano;

/** tests des indexeurs distribués
 *  * 
 */
public class TESTQL_CLASSIC {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("INCREMENTAL", new ConfigurationNative1());
        id.Statistic.global();
        //id.checkIntegrityOfW("nana",true);
        //testref();


        test("napoléon");
//        test("hashtable");
//        test("hashtable AND interface");
//        test("NEAR(garbage,collection)");
//        test("NEXT(create,view)");




        t1.stop();
        // id.close();
        System.exit(1);
    }

    public static void test(String s) {
        TimerNano t1 = new TimerNano("parse:" + s, false);

        QLManager ql = new QL_Basic();
        QLResultAndRank qres = ql.getMore(s, id);
        int[] res = qres.result;
        String[] docName = qres.docName;
        t1.stop(false);
        if (res == null) {
            msg("result is null");
        } else {
            msg("result is ndoc:" + res.length);
            for (int i = 0; i < res.length; i++) {
                msg("  doc " + res[i] + " : " + docName[i]);
            }
        }
        msg("\n-----------------------------------------------------------------------------\n");
        QLResultAndRank[] nodes = new QLResultAndRank[3];
        nodes[0] = qres;
        nodes[1] = qres;
        QResDistributed globalRes = new QResDistributed(nodes, 100);
        if (globalRes == null) {
            msg("global result is null");
        } else {
            msg("global result is ndoc:" + globalRes.topdoc.length);
            for (int i = 0; i < globalRes.topdoc.length; i++) {
                msg(" doc " + globalRes.topdoc[i] + " from node " + globalRes.topsource[i] + " : " + globalRes.topname[i]);
            }
        }

        //showVector(res);


    }

    private static void testref() {
        String s = readFromFile("C:/JG/gigaversion/data/REFTEST.TXT");

        {
            TimerNano t1 = new TimerNano("ref 1-----------:", false);
            id.Query.parseQuery("MULTI", s, "6");
            t1.stop(false);
        }

//        {TimerNano t1=new TimerNano("ref 2-----------:",false);
//        id.Query.parseQuery("MULTI",s,"6");
//        t1.stop(false);
//        }

        //System.out.println(id.Query.parseQuery("MULTI",s,"6"));
    }

    private static String readFromFile(String fname) {
        StringBuffer b = new StringBuffer("");
        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));
            String w = in.readLine();
            while (w != null) {
                //System.out.println(w);
                b.append(w);
                w = in.readLine();
            }
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
        return b.toString();
    }
}
