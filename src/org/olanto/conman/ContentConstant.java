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

package org.olanto.conman;

/**
 * Une classe pour déclarer des constants.
 * 
 *
 */
public class ContentConstant extends org.olanto.idxvli.IdxConstant {

    public static final String SEPARATOR = "<::::....---->";  // une chaine improbable (sauf dans ce programme!)
    /*************************************************************************************/
    /** path de la racine des fichiers communs */
    public static String COMMON_ROOT = "C:/JG/VLI_RW/data/conman";
    /** racine des nom des fichiers  */
    public static String currentf = "rootctm";
    /* DOCUMENT *************************************************************************************/
    /** taille maximum d'un document*/
    public static final int DOC_MAXOCCLENGTH = 2000000;
    /** encodage des contenus */
    public static String CONTENT_ENCODING = "UTF-8";  //;"ISO-8859-1"
    /*************************************************************************************/
}
