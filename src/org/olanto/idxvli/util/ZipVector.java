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
 *  Comportements d'un vecteur de String zipés.
 *
 * 
 *
 *
 */
public interface ZipVector {

    /**  crée un vecteur 2^_maxSize par defaut à  l'endroit indiqué par le path, (maximum=2^31), objet=byte[] qui représente le zip d'un fichier */
    public ZipVector create(String _path, String _file, int _maxSize);

    /**  ouvre un vecteur Ã  l'endroit indiqué par le path */
    public ZipVector open(String _path, String _file, readWriteMode _RW);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /** mets à  jour la position pos avec txt */
    public void set(int pos, String txt);

    /** élimine le vecteur a la position pos */
    public void clear(int pos);

    /**  cherche la valeur à  la position pos  */
    public String get(int pos);

    /**  retourne la taille du vecteur */
    public int length();

    /**  retourne la taille du vecteur à  la position pos*/
    public int lengthZip(int pos);
 
    /**  retourne la taille du vecteur décompressé à  la position pos*/
    public int lengthString(int pos);

    /**  retourne la taille total des vecteurs stockés*/
    public long totalLengthZip();
   /**  retourne la taille total des vecteurs stockés*/
    public long totalLengthString();

    /**  imprime des statistiques */
    public void printStatistic();
}
