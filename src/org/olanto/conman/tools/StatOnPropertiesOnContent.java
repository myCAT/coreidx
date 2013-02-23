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
import org.olanto.conman.server.*;
import static org.olanto.util.Messages.*;
import static org.olanto.conman.server.GetContentService.*;
import org.olanto.idxvli.util.SetOfBits;
import org.olanto.idxvli.doc.*;

/** statistique sur les états des documents dans le conteneur
 * 
 *
 * 
 */
public class StatOnPropertiesOnContent {

    static final boolean verboseTitle = false;
    static ContentService id;

    public static void main(String[] args) {

        inventoryOf("rmi://localhost/CM_COLLECT");
        inventoryOf("rmi://localhost/CM_CLEAN");
    }

    public static void inventoryOf(String CMService) {
        msg("");
        msg("");
        msg("---------- " + CMService);
        id = getServiceCM(CMService);
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
            msg("#doc:" + id.getSize());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        msg("----- Type ---------------------------");
        countSet("TYPE.STRING");
        countSet("TYPE.PDF");
        countSet("TYPE.ERROR");
        countSet("TYPE.INDEXABLE");
        countSet("TYPE.CONVERTABLE");
        countSet("TYPE.CLEAN_TEXT");
        msg("----- collected ---------------------------");
        countSet("STATE.INDEXED");
        countSet("STATE.CONVERTED");
        countSet("STATE.ERROR");
        msg("----- Web Robot ---------------------------");
        countSet("PROTOCOLE.URL");
        countSet("STATE.NOTLOAD");
        countSet("STATE.LOAD");
        countSet("STATE.ERRORLOAD");
    }

    static void countSet(String setName) {
        try {
            int lastdoc = id.getSize();
            int count = 0;
            SetOfBits sob = id.satisfyThisProperty(setName);
            if (sob == null) {
                msg("no property for " + setName + " :" + count);
                return;
            }

            for (int i = 0; i < lastdoc; i++) {
                if (sob.get(i)) {
                    count++;
                }
            }
            msg("count for " + setName + " :" + count);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
