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
 * comportement d'un cache pour la mise à jour.
 * 
 *
 * permet de gérer les informations sur le remplissage partielle du cache
 */
public interface CacheWrite extends CacheIdx {

    /**
     * retourne la taille réellement utilisée dans le vecteur pour le terme i dans le cache
     * @param i terme
     * @return la taille réellement utilisée
     */
    public int getCountOf(int i);

    /**
     * assigne la taille réellement utilisée dans le vecteur pour le terme i dans le cache
     * @param i terme
     * @param value la taille réellement utilisée
     */
    public void setCountOf(int i, int value);

    /**
     * incrémente la taille réellement utilisée dans le vecteur pour le terme i dans le cache
     * @param i terme
     */
    public void incCountOf(int i);

    /**réinitialise toutes les taille réellement utilisée dans le cache =0
     */
    public void resetAll();
}
