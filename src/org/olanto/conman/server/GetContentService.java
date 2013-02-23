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

package org.olanto.conman.server;

import java.rmi.*;
import org.olanto.conman.*;
import org.olanto.idxvli.server.*;
import static org.olanto.util.Messages.*;

/** initialise un content service
 * 
 *
 */
public class GetContentService {

    public static ContentService getServiceCM(String serviceName) {

        ContentService is;

        try {
            System.out.println("try to connect server " + serviceName);
            Remote r = Naming.lookup(serviceName);
            System.out.println("access to serveur");
            if (r instanceof ContentService) {
                is = ((ContentService) r);
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

    public static ContentService runServiceCM(ContentInit client, String serviceName) {

        ContentService contentobj;
        try {
            System.out.println("initialisation de ContentService_BASIC ...");

            contentobj = new ContentService_BASIC();
            contentobj.getAndInit(client, "INCREMENTAL");

            System.out.println("Enregistrement du serveur");

            //      String name="rmi://"+java.net.InetAddress.getLocalHost()+"/IDX";
            String name = "rmi://localhost/CM";
            System.out.println("name:" + name);
            Naming.rebind(name, contentobj);
            System.out.println("Server content manager started");
            return contentobj;
        } catch (Exception e) {
            error("Server content manager", e);
        }
        return null;

    }

    public static IndexService getServiceVLI(String serviceName) {

        IndexService is;

        try {

            System.out.println("try to connect server " + serviceName);
            Remote r = Naming.lookup(serviceName);
            System.out.println("access to serveur");
            if (r instanceof IndexService) {
                is = ((IndexService) r);
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

    public static IndexService_MyCat getServiceMYCAT(String serviceName) {

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
}
