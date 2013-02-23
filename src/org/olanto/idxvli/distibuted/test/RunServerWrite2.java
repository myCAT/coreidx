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

import org.olanto.idxvli.IdxInit;
import java.rmi.*;
import static org.olanto.util.Messages.*;
import org.olanto.idxvli.server.*;

/** init des indexeurs distribués
 *  * 
 */

/*
 * �Djava.security.policy=policy.all 
 */
public class RunServerWrite2 {

    public static void main(String[] args) {

        int i = 2;

        IdxInit conf = new ConfigurationNative2();
        try {

            System.out.println("initialisation de l'indexeur " + i + " ...");

            IndexService_BASIC idxobj = new IndexService_BASIC();
            idxobj.getAndInit(conf, "INCREMENTAL", false);

            System.out.println("Enregistrement du serveur " + i);

            //      String name="rmi://"+java.net.InetAddress.getLocalHost()+"/IDX";
            String name = "rmi://localhost/VLI" + i;
            System.out.println("name:" + name);
            Naming.rebind(name, idxobj);
            System.out.println("Serveur lanc� " + i);


        } catch (Exception e) {
            error("Serveur Idx " + i, e);
        }

    }
}
