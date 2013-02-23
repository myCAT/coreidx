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

package org.olanto.conman.util;

import org.olanto.conman.server.*;
import static org.olanto.util.Messages.*;
import static org.olanto.conman.server.GetContentService.*;


/** test de conversion
 * 
 *
 * 
 */
public class ExtractTextFromPDFCache {

    static final boolean verboseTitle = false;

    public static void main(String[] args) {
        ContentService cmCollect = getServiceCM("rmi://localhost/CM_COLLECT");
        //ContentService cmClean=getServiceCM("rmi://localhost/CM_CLEAN");
        String title = null;
        int notitle = 0;
        try {
//            msg(cmClean.getRefName("0"));
//            msg(cmClean.getTitle("0"));
//            msg(cmClean.getCleanText("0"));
            byte[] b = cmCollect.getBin("http://cui.unige.ch/isi/reports/fnz-ht-05.pdf");
            msg(new String(b));

            msg("-------");
            msg(PDF2TXT.pdf2txt(b, "http://cui.unige.ch/isi/reports/fnz-ht-05.pdf"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
