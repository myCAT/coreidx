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

/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package org.olanto.idxvli.word.test;

import java.rmi.*;
import static org.olanto.util.Messages.*;
import org.olanto.idxvli.word.*;

/**
 *
 * 
 *
 */
public class DictionnaryServer {

    public static void main(String[] args) {


        try {
            System.out.println("initialisation de l'indexeur ...");

            DictionnaryService_BASIC idxobj = new DictionnaryService_BASIC();

            System.out.println("Enregistrement du serveur");

            String name = "rmi://jg4/TERMDICT";
            System.out.println("name:" + name);
            Naming.rebind(name, idxobj);
            System.out.println("Serveur lancï¿½");

        } catch (Exception e) {
            error("Serveur Idx", e);
        }


    }
}
