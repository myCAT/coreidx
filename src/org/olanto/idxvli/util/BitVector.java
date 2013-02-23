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

package org.olanto.idxvli.util;

/**
 *  Comportements d'un vecteur de bit.
 * 
 *
 *
 *  Comportements d'un vecteur de bit.
 */
public interface BitVector {

    /**  crÃ©e un vecteur _maxSize par dÃ©faut Ã  l'endroit indiquÃ© par le path, (maximum=2^31), 
     *   _maxSize doit Ãªtre un multiple de 32
     */
    public BitVector create(String _path, String _file, int _maxSize);

    /**  ouvre un vecteur Ã  l'endroit indiquÃ© par le path */
    public BitVector open(String _path, String _file);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /** mets Ã  jour la position pos avec la valeur val */
    public void set(int pos, boolean val);

    /**  cherche la valeur Ã  la position pos  */
    public boolean get(int pos);

    /**  cherche le vecteur entier  */
    public SetOfBits get();

    /**  retourne la taille du vecteur */
    public int length();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  retourne des statistiques */
    public String getStatistic();
}
