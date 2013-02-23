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

import org.olanto.idxvli.doc.PropertiesList;
import java.rmi.*;
import org.olanto.idxvli.util.SetOfBits;
import static org.olanto.util.Messages.*;

/** pour mettre à jour les propriétés des documents de mycat
 *
 * - modification JG : correction des caractères de séparation
 */
public class SetCollectionProperties {

    static String rootTxt;
    static IndexService_MyCat is;
    static final int MAX_LEVEL = 4;

    public static void updateCollectionProperties(IndexService_MyCat ispar) {

        is = ispar;

        try {

            rootTxt = is.getROOT_CORPUS_TXT();
            int lastdoc = is.getSize(); // taille du corpus
            PropertiesList prop = is.getDictionnary("COLLECTION.");
            String[] oldCollection = prop.result;
            msg("#collection before update: " + oldCollection.length);
            for (int i = 0; i < oldCollection.length; i++) {
                is.clearThisProperty(oldCollection[i]);
            }
            for (int i = 0; i < lastdoc; i++) {
                String name = is.getDocName(i);
                int from = name.lastIndexOf("/");
                int to = name.lastIndexOf("¦");   // vérifier le marqueur !!!!!!!!!!!!!!!!!!!!!!!
                if (from != -1 && to != -1) {
                    String collection = "COLLECTION." + name.substring(from + 1, to + 1);
                    int start = 0;
                    for (int j = 0; j < MAX_LEVEL; j++) {
                        int next = collection.indexOf("¦", start);
                        if (next != -1) {
                            //msg(collection.substring(0,next) + " is the collection for: " + name);
                            start = next + 1;
                          is.setDocumentPropertie(i, collection.substring(0, next));
                        } else {
                            break;
                        }
                    }
                } else {
                    //msg("no collection for: " + name);
                }
                if ((i + 1) % 100 == 0) {
                    System.out.print(".");
                }
                if ((i + 1) % 10000 == 0) {
                    System.out.println();
                }
            }
            inventoryOf();
            msg("collection properties are updated ...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void inventoryOf() {
        msg("");
        msg("");
        msg("---------- ");
        try {
            msg("---- all properties COLLECTION:");
            PropertiesList prop = is.getDictionnary("COLLECTION.");
            showVector(prop.result);
            msg("#doc:" + is.getSize());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    /** afficher le contenu du vecteur
     * @param p vecteur
     */
    public final static void showVector(String[] p) {
        if (p != null) {
            int l = p.length;
            for (int i = 0; i < l; i++) {
                countSet(p[i]);
            }
        } else {
            msg("Is null");
        }
    }

    static void countSet(String setName) {
        try {
            int lastdoc = is.getSize();
            int count = 0;
            SetOfBits sob = is.satisfyThisProperty(setName);
            if (sob == null) {
                msg("no property for " + setName + " :" + count);
                return;
            }

            for (int i = 0; i < lastdoc; i++) {
                if (sob.get(i)) {
                    count++;
                }
            }
            msg(setName + " :" + count);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
