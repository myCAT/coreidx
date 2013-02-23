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
import java.rmi.*;
import org.olanto.conman.server.*;
import static org.olanto.util.Messages.*;
import static org.olanto.conman.server.GetContentService.*;
import static org.olanto.conman.util.UtilitiesOnHTML.*;
import org.olanto.idxvli.util.SetOfBits;
import java.util.regex.*;


/** convertion de html vers le txt + title
 * 
 *
 * 
 */
public class CollectHTMLAndClean {

    static final boolean verboseTitle = false;

    public static void main(String[] args) {
        ContentService cmCollect = getServiceCM("rmi://localhost/CM_COLLECT");
        ContentService cmClean = getServiceCM("rmi://localhost/CM_CLEAN");
        String title = null;
        int notitle = 0;
        try {
            int lastdoc = cmCollect.getSize();
            msg("#doc:" + lastdoc);
            SetOfBits indexable = cmCollect.satisfyThisProperty("TYPE.INDEXABLE");
            for (int i = 0; i < lastdoc; i++) {
                if (i % 1000 == 0) {
                    msg("process:" + i);
                }
                if (indexable.get(i)) {  // on doit donc pouvoir trouver un titre si html
                    String txt = cmCollect.getDoc(i);
                    if (txt.length() != 0) {
                        if (txt.startsWith("%PDF")) {  // PDF
                            msg("skip unconverted PDF:" + cmCollect.getDocName(i));
                        } else if (txt.substring(0, Math.min(500, txt.length())).contains("charset=windows-1251")) {  // RUSSE
                            msg("skip unconverted 1251:" + cmCollect.getDocName(i));
                        } else {  // on peut indexer
                            if (verboseTitle) {
                                msg("process doc:" + i + " length:" + txt.length());
                            }
                            title = getTitle(txt);
                            if (title == null) {
                                notitle++;
                                msg("no title for:" + cmCollect.getDocName(i));
                                title = "no title";
                            }
                            title = clean(title);
                            String refname = cmCollect.getDocName(i);
                            String cleantxt = html2txt(cmCollect.getDoc(i));
                            if (verboseTitle) {
                                msg("assign title :" + title + " for doc" + cmCollect.getDocName(i));
                            }
                            cmClean.setRefDoc("" + i, refname, title, cleantxt);
                        }
                    }
                }
            }
            msg("notitle:" + notitle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
