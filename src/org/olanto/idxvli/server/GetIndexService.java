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

package org.olanto.idxvli.server;

import org.olanto.idxvli.IdxInit;
import java.rmi.*;
import static org.olanto.util.Messages.*;

/** active un service myCat
 *
 *  * 

 */
public class GetIndexService {

    public static IndexService_MyCat getServiceIDX(String serviceName) {

        IndexService_MyCat is;

        try {
            System.out.println("try to connect server " + serviceName);
            Remote r = Naming.lookup(serviceName);
            System.out.println("access to serveur");
            if (r instanceof IndexService_MyCat) {
                is = ((IndexService_MyCat) r);
                String s = is.getInformation();
                System.out.println("Test ... return = " + s);
                msg(serviceName + " service open ...");
                return is;
            } else {
                msg(serviceName + " exist but not a right type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IndexService_MyCat runServiceIDX(IdxInit client, String serviceName) {

        IndexService_MyCat mapobj;
        try {
            System.out.println("initialisation of Server_MyCat ...");

            mapobj = new Server_MyCat();
            mapobj.getAndInit(client, "INCREMENTAL", false);

            System.out.println("registry ...");

            //      String name="rmi://"+java.net.InetAddress.getLocalHost()+"/IDX";
            //String name = "rmi://localhost/CM";
            System.out.println("name:" + serviceName);
            Naming.rebind(serviceName, mapobj);
            System.out.println("Server Index manager started");
            return mapobj;
        } catch (Exception e) {
            error("Server index manager", e);
        }
        return null;

    }
}
