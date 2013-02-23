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

import java.rmi.*;
import org.olanto.idxvli.server.*;
import static org.olanto.util.Messages.*;

/**
 *
 * 
 *
 */
public class TestClientRead {

    public static void main(String[] args) {


        try {

            System.out.println("connect to serveur");

            Remote r = Naming.lookup("rmi://localhost/VLI");

            System.out.println("access to serveur");

            if (r instanceof IndexService) {
                IndexService is = ((IndexService) r);
                String s = is.getInformation();
                System.out.println("chaï¿½ne renvoyï¿½e = " + s);
                QLResult res = is.evalQL("russie");
                msg("time:" + res.duration);
                String[] docname = is.getDocName(res.result);
                for (int i = 0; i < res.result.length; i++) {
                    msg("  doc " + res.result[i] + " valid:" + is.docIsValid(res.result[i]) + " : " + docname[i]);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
