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

package org.olanto.propertie;

/**
 *
 *  Comportements d'un gestionnaire de marquuer de propiétés des documents.
 *
 * 
 * 
 *
 *
 */
public interface MarkerManager {

    /**
     *  retourne la langue d'un document 
     * @param txt échantillon du document (1000 caractères par exemple)
     * @return code de la langue (FR,EN,...)
     */
    public String getLanguage(String txt);

    /**
     *  retourne la collection d'un document 
     * @param name nom du document (url, ...)
     * @return code de la collection (INFORMATIQUE,MEDECINE,...)
     */
    public String[] getCollectionFromName(String name);
}
