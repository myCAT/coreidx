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
import java.util.*;
import java.net.*;
import org.olanto.idxvli.util.SetOfBits;

/** construit la liste des host (url) contenue dans un content manager (lié avec un robot ... ).
 * 
 *
 * 
 */

/* on admet que l'on a que des URL !!!!!! */
public class GetHostList {

    static HashMap<String, Integer> url2int = new HashMap<String, Integer>();
    static HashMap<Integer, String> int2url = new HashMap<Integer, String>();

    public static void main(String[] args) {

        ContentService is = getServiceCM("rmi://localhost/CM_COLLECT");
        url2int = new HashMap<String, Integer>();
        int2url = new HashMap<Integer, String>();
        int count = 0;
        try {
            int lastdoc = is.getSize();
            SetOfBits indexable = is.satisfyThisProperty("TYPE.INDEXABLE");
            for (int i = 0; i < lastdoc; i++) {
                if (i % 1000 == 0) {
                    msg("process:" + i);
                }
                if (indexable.get(i)) {  // on doit donc pouvoir trouver un titre si html
                    String s = is.getDocName(i);
                    URL url = new URL(s);
                    String host = url.getHost();
                    //msg(host);
                    if (url2int.get(host) == null) {
                        url2int.put(host, count);
                        int2url.put(count, host);
                        count++;
                    }
                }
            }
            for (int i = 0; i < int2url.size(); i++) {
                msg(int2url.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
