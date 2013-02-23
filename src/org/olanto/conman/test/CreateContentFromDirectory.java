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

package org.olanto.conman.test;

import org.olanto.conman.*;
import org.olanto.util.Timer;

/**
 *
 * Test du conteneur
 * 
 */
public class CreateContentFromDirectory {

    private static ContentStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new ContentStructure("NEW", new ConfigurationContentManager());

        //id.getFromDirectory("c:/jdk1.5/docs/guide");
        id.getFromDirectory("C:/Documents and Settings/jacques.guyot/Bureau/Quote");


//       for (int i=0;i<3;i++){
//            msg(id.getStringContent(i));
//        }



        id.Statistic.global();
        id.close();
        t1.stop();

    }
}
