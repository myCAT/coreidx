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

package org.olanto.conman.tools;

import java.rmi.*;
import org.olanto.idxvli.server.*;
import static org.olanto.util.Messages.*;
import static org.olanto.conman.server.GetContentService.*;
import org.olanto.idxvli.doc.*;

/** statistique sur les états des documents dans l'indexeur
 * 
 *
 * 
 */
public class StatOnPropertiesOnIndex {

    static final boolean verboseTitle = false;
    static IndexService id;

    public static void main(String[] args) {

        inventoryOf("rmi://localhost/VLI");
    }

    public static void inventoryOf(String VLIService) {
        msg("");
        msg("");
        msg("---------- " + VLIService);
        id = getServiceVLI(VLIService);
        try {
            msg("---- all properties:");
            PropertiesList prop = id.getDictionnary();
            showVector(prop.result);
            msg("---- all properties TYPE:");
            prop = id.getDictionnary("TYPE.");
            showVector(prop.result);
            msg("---- all properties LANG:");
            prop = id.getDictionnary("LANG.");
            showVector(prop.result);
            msg("---- all properties COLLECTION:");
            prop = id.getDictionnary("COLLECTION.");
            showVector(prop.result);
            msg("---- all properties PROFIL:");
            prop = id.getDictionnary("PROFIL.");
            showVector(prop.result);
            msg("statistic:" + id.getStatistic());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
