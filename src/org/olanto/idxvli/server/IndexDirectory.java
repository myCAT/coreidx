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

import java.rmi.Naming;
import java.rmi.Remote;
import static org.olanto.util.Messages.*;

/** indexation d'un dossier
 * 
 * 
  *
 */
public class IndexDirectory {

    public static void main(String[] args) {


        try {

            System.out.println("connect to serveur");
            Remote r = Naming.lookup("rmi://localhost/VLI");

            System.out.println("access to serveur");

            if (r instanceof IndexService_MyCat) {
                IndexService_MyCat ismain = ((IndexService_MyCat) r);
                updateIndex(ismain);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateIndex(IndexService_MyCat is) {
       try {
            String s = is.getInformation();
            //System.out.println("chaï¿½ne renvoyï¿½e = " + s);
            System.out.println("client send a directory to be indexed ... could take time");
            System.out.println("corpus txt = " + is.getROOT_CORPUS_TXT());
            int lastdocBeforeIdx = is.getSize();
                is.indexdir(is.getROOT_CORPUS_TXT());
//                is.flush();
                is.showFullIndex();
           if (lastdocBeforeIdx != is.getSize()) {
                msg("tot of new documents:" + (is.getSize() - lastdocBeforeIdx));
                SetLanguageProperties.updateLanguageProperties(is);
                SetCollectionProperties.updateCollectionProperties(is);
//                msg("save new index ...");
//               is.saveAndRestart();
            } else {
                msg("no change (no new document)");
            }
            msg("Index Server is ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
