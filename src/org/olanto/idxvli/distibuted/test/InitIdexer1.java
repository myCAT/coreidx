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

import static org.olanto.util.Messages.*;
import org.olanto.idxvli.server.*;
/** init des indexeurs distribués
 *  * 
 */

/*
 * �Djava.security.policy=policy.all 
 */
public class InitIdexer1 {

    public static void main(String[] args) {

         try {
            System.out.println("initialisation de l'indexeur 1...");

            IndexService_BASIC idxobj = new IndexService_BASIC();
            idxobj.startANewOne(new ConfigurationNative1());
        } catch (Exception e) {
            error("Serveur Idx 1", e);
        }


    }

 
}
