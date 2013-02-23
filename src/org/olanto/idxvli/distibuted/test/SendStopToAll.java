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

import java.rmi.*;
import org.olanto.idxvli.server.*;
import static org.olanto.util.Messages.*;

/** tests des indexeurs distribués
 *  * 
 */
public class SendStopToAll {

    public static void main(String[] args) {

        int nbidx = 4;
        SendClose[] sc = new SendClose[nbidx + 1];

        for (int i = 1; i <= nbidx; i++) {
            sc[i] = new SendClose(i);
            sc[i].start();
        }
    }
}

class SendClose extends Thread {

    int name;

    SendClose(int name) {
        this.name = name;
    }

    public void run() {
        try {

            System.out.println("connect to serveur " + name);

            Remote r = Naming.lookup("rmi://localhost/VLI" + name);

            System.out.println("access to serveur " + name);

            if (r instanceof IndexService) {
                IndexService is = ((IndexService) r);
                String s = is.getInformation();
                System.out.println("info from " + name + " = " + s);
                System.out.println("client send a stop signal to  " + name + " ... could take time");
                is.quit();
                msg("end ... " + name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
