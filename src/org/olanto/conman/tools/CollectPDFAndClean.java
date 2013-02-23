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

import org.olanto.conman.util.*;
import org.olanto.conman.server.*;
import static org.olanto.util.Messages.*;
import static org.olanto.conman.server.GetContentService.*;
import static org.olanto.conman.util.UtilitiesOnHTML.*;
import org.olanto.idxvli.util.SetOfBits;

/** convertion de pdf vers le txt + title
 * 
 *
 * 
 */
public class CollectPDFAndClean {

    static final boolean verboseTitle = true;

    public static void main(String[] args) {
        ContentService cmCollect = getServiceCM("rmi://localhost/CM_COLLECT");
        ContentService cmClean = getServiceCM("rmi://localhost/CM_CLEAN");
        try {
            int lastdoc = cmCollect.getSize();
            msg("#doc:" + lastdoc);
            SetOfBits convertable = cmCollect.satisfyThisProperty("TYPE.CONVERTABLE");
            SetOfBits pdftype = cmCollect.satisfyThisProperty("TYPE.PDF");
            for (int i = 0; i < lastdoc; i++) {
                if (i % 1000 == 0) {
                    msg("process:" + i);
                }
                if (convertable.get(i) && pdftype.get(i)) {  // convertible & pfd
                    byte[] b = cmCollect.getBin(i);
                    String refname = cmCollect.getDocName(i);
                    String txt = PDF2TXT.pdf2txt(b, refname);
                    if (txt != null && txt.length() != 0) {
                        // on peut indexer 
                        String cleantxt = html2txt(txt);
                        String title = cleantxt.substring(0, Math.min(60, cleantxt.length()));

                        if (verboseTitle) {
                            msg("assign title :" + title + " for doc" + refname);
                        }
                        cmClean.setRefDoc("" + i, refname, title, cleantxt);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
