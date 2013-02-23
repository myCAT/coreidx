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

import java.rmi.RemoteException;


/**
 *  Facade pour les services de reconstruction des URL des originaux
 * 
 */
public interface OriginalService  {

    /**
     * Pour demander avoir une url à partir d'un nom de fichier indexé
     */
    public String getURL(String fname) throws RemoteException;

    /**
     * Pour demander avoir un path à partir d'un nom de fichier indexé
     */
    public String getPath(String fname) throws RemoteException;

     /**
     * Pour demander un nom de fichier à sauver dans le zip à partir d'un nom de fichier indexé
     */
    public String getSaveZipName(String fname) throws RemoteException;
}
