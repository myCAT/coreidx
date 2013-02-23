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

import static org.olanto.idxvli.IdxEnum.*;

/**
 *
 *
 *  Comportements d'un vecteur de byte[fixedArraySize].
 *
 * 
 *
 *
 */
public interface ByteArrayVector {

    /**  crÃ©e un vecteur 2^_maxSize par dÃ©faut Ã  l'endroit indiquÃ© par le path, (maximum=2^31), objet=byte[fixedArraySize] */
    public ByteArrayVector create(String _path, String _file, int _maxSize, int _fixedArraySize);

    /**  ouvre un vecteur Ã  l'endroit indiquÃ© par le path */
    public ByteArrayVector open(String _path, String _file, readWriteMode _RW);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /** mets Ã  jour la position pos avec la valeur val */
    public void set(int pos, byte[] val);

    /** Ã©limine le vecteur a la position pos */
    public void clear(int pos);

    /**  cherche la valeur Ã  la position pos  */
    public byte[] get(int pos);

    /**  cherche la valeur Ã  la position pos, la iÃ¨me valeur   */
    public byte get(int pos, int i);

    /**  retourne la taille du vecteur */
    public int length();

    /**  retourne la taille du vecteur Ã  la position pos*/
    public int length(int pos);

    /**  retourne la taille maximum des vecteurs stockÃ©*/
    public int maxUsedlength();

    /**  imprime des statistiques */
    public void printStatistic();
}
