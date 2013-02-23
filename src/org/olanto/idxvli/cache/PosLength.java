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

package org.olanto.idxvli.cache;

/**
 * Classe stockant les  bornes des positions d'un vecteur.
 * 
 *
 * Remarque cette classe permet par exemple de rapporter la position et la longueur dans un vecteur
 * pour pouvoir traiter les informations dans ce vecteur au lieu d'effectuer une copie.
 */
public class PosLength {

    /* la position */
    public int pos;
    /* la longueur */
    public int length;

    /** crÃ©e une borne
     * @param pos position
     * @param length longueur
     */
    public PosLength(int pos, int length) {
        this.pos = pos;
        this.length = length;
    }
}
