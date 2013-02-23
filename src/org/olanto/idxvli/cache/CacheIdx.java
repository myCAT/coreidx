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
 * Comportement d'un cache d'index.
 * 
 *
 *
 * Comportement d'un cache d'index.
 * Les indices des termes sont ceux utilisÃ©s par l'implÃ©mentation du cache
 * (donc par forcemment ceux du dictionnaire de termes)
 */
public interface CacheIdx {

    /**
     * retourne une rÃ©fÃ©rence sur le cache du terme i. On travaille donc directement dans le cache!
     * @param i terme
     * @return veteur original
     */
    public int[] getReferenceOn(int i);

    /**
     * retourne une copie du cache du terme i
     * @param i terme
     * @return veteur
     */
    public int[] getCopyOf(int i);

    /**
     * retourne la valeur du cache du terme i Ã  la position j
     * @param i terme
     * @param j position
     * @return valeur
     */
    public int v(int i, int j);

    /**
     * retourne le vecteur des valeurs du cache du terme i de from sur la longueur donnÃ©e
     * @param i terme
     * @param from position
     * @param length longeur
     * @return vecteur des valeurs
     */
    public int[] getv(int i, int from, int length);

    /**
     * assigne une valeur au cache du terme i Ã  la position j
     * @param i terme
     * @param j position
     * @param value valeur
     */
    public void setv(int i, int j, int value);

    /**
     * incrÃ©mente la valeur du cache du terme i Ã  la position j
     * @param i terme
     * @param j position
     */
    public void incv(int i, int j);

    /**
     * retourne la taille total du cache
     * @return la taille total du cache
     */
    public long getCurrentSize();

    /**
     * dÃ©termine si le terme i est pas dans le cache
     * @param i terme
     * @return true=le terme i est pas dans le cache
     */
    public boolean isNotLoaded(int i);

    /**
     * dÃ©termine si le terme i est  dans le cache
     * @param i terme
     * @return true=le terme i est  dans le cache
     */
    public boolean isLoaded(int i);

    /**
     * retourne la taille du vecteur i du cache
     * @param i terme
     * @return  taille du vecteur i
     */
    public int length(int i);

    /**
     * inscrit un nouveau terme dans le cache avec une taille initiale
     * @param i terme
     * @param initialSize taille initiale
     */
    public void newVector(int i, int initialSize);

    /**
     * inscrit un nouveau terme dans le cache avec un vecteur initial
     * @param i terme
     * @param reg vecteur initial
     */
    public void registerVector(int i, int[] reg);

    /**
     * libÃ¨re le terme i dans le cache
     * @param i terme
     */
    public void releaseVector(int i);

    /**
     * assigne une nouvelle taille au cache du terme i
     * @param i terme
     * @param newSize nouvelle taille
     */
    public void resize(int i, int newSize);
}
