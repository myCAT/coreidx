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

import org.olanto.conman.server.*;
import static org.olanto.util.Messages.*;
import static org.olanto.conman.server.GetContentService.*;

/** permet la réindexation du conteneur CLEAN
 * 
 *
 * 
 */
public class ResetIndexed {

    public static void main(String[] args) {

        ContentService is = getServiceCM("rmi://localhost/CM_COLLECT");

        try {
            int lastdoc = is.getSize();
            for (int i = 0; i < lastdoc; i++) {
                if (i % 1000 == 0) {
                    msg("process:" + i);
                }
                is.clearDocumentPropertie(i, "STATE.INDEXED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
