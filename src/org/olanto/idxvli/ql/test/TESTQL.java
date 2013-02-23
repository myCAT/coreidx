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

package org.olanto.idxvli.ql.test;

import org.olanto.idxvli.*;
import org.olanto.util.Timer;
import java.io.*;
import org.olanto.idxvli.ql.*;
import static org.olanto.util.Messages.*;
import org.olanto.util.TimerNano;

/**Test de l'indexeur, mode query
 * 
 *
 * 
 */
public class TESTQL {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY", new ConfigurationQL());
        id.Statistic.global();


        test("prenant");
        test("italie");
        test("italie AND prenant");
        test("italie OR prenant");
        test("italie MINUS prenant");
        test("italie AND prenant AND gouvernement");
        test("prenant MINUS (italie AND prenant AND gouvernement)");

        test("undertaking");
        test("italy");
        test("italy AND undertaking");
        test("italy OR undertaking");

        test("(italie AND prenant) AND (italy AND undertaking)");
        test("(italie AND prenant) OR (italy AND undertaking)");

        test("prenant AND acte");
        test("NEXT(prenant,acte)");
        test("NEAR(prenant,gouvernement)");

        test("tarif");
        test("tarif IN[\"_EN\"]");
        test("(tarif IN[\"_EN\"])IN[\"_FR\"]");
        test("tarif IN[\"_FR\"]");
        test("tarif IN[ NOT \"_FR\"]");
        test("tarif IN[\"_EN\" OR \"_FR\"]");
        test("tarif IN[LENGTH > \"1000\"]");
        test("tarif IN[LENGTH < \"1000\"]");
        test("tarif IN[DATE < \"09-02-2004\"]");

        test("((italie AND prenant)IN[\"_FR\"] OR (italy AND undertaking)IN[\"_EN\"]) IN [LENGTH < \"1000\"]");

        // limitï¿½ filtering aux documents indexï¿½s.!!!!!!!!!!!!!!!!!!!!!!


        System.out.println("id ï¿½galement:" + id.getIntForW("ï¿½galement"));
        ////         id.indexread.lockForBasic(427);
        ////         showVector(id.indexread.getCopyOfDoc(427));
        ////         //showVector(id.indexread.getCopyOfPos(165));
        ////         id.indexread.unlock(427);
        //        test ("\"pomme\" AND \"terre\"");
        //        test ("(\"pomme\" AND \"terre\")");
        //        test ("NEXT(\"pomme\" , \"terre\")");
        //        test ("pomme AND terre");
        //        test ("pomme AND terre AND parmentier");
        //        test ("(pomme AND terre) OR (haricot AND vert)");
        //        test ("(pomme OR terre) AND (haricot OR vert)");
        //        test ("pomme AND terre IN [ \"FRENCH\" ]");
        //        test ("pomme AND terre IN [ FRENCH ]");
        //         test ("pomme AND terre IN [ FRENCH  AND DATE < \"01.01.2003\" ]");


        t1.stop();
    }

    public static void test(String s) {
        TimerNano t1 = new TimerNano("parse:" + s, false);

        QLCompiler ql = new QLCompiler(new StringReader(s), id);
        int[] res = ql.execute();

        if (res == null) {
            msg("result is null");
        } else {
            msg("result is ndoc:" + res.length);
        }
        showVector(res);

        t1.stop(false);
    }

    private static void QCMD(String command, String w1, String w2) {
        TimerNano t1 = new TimerNano(command + " " + w1 + " " + w2, false);
        //System.out.println((id.Query.parseQuery(command, w1, w2)).length());
        System.out.println((id.Query.parseQuery(command, w1, w2)));
        t1.stop(false);
    }
}
