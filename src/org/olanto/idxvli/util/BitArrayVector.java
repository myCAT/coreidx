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
 * Comportements d'un tableau de bit[2^maxSize][fixedArraySize].
 * crée une structure de bit qui sera vue comme un tableau.
 * 
 *
 * 
 * Comportements d'un tableau de bit[2^maxSize][fixedArraySize].
 */
public interface BitArrayVector {

    /**
     * crée un un tableau de bit[2^maxSize][fixedArraySize] à l'endroit indiqué par le path et file
     * @param _implementation cette classe possède plusieurs implémentations (FAST,BIG)
     * @param _path dossier contenant les fichiers
     * @param _file nom racine des fichiers
     * @param _maxSize nbre de vecteurs du tableaux = 2^_maxSize
     * @param _fixedArraySize nbre taille fixe des vecteurs
     * @return un tableau de bit[2^maxSize][fixedArraySize]
     */
    public BitArrayVector create(implementationMode _implementation, String _path, String _file, int _maxSize, int _fixedArraySize);

    /**
     * ouvre un vecteur à l'endroit indiqué par le path
     * @param _ManagerImplementation 
     * @param _path 
     * @param _file 
     * @param _RW 
     * @return un gestionnaire de vecteurs
     */
    public BitArrayVector open(implementationMode _ManagerImplementation, String _path, String _file, readWriteMode _RW);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /**
     * mets à jour la position i avec la valeur val du vecteur pos
     * @param pos 
     * @param i 
     * @param val 
     */
    public void set(int pos, int i, boolean val);

    /**
     * mets à jour la position i avec le vecteur complet
     * @param pos 
     * @param v 
     */
    public void set(int pos, SetOfBits v);

    /**
     * cherche la valeur à la position pos, la ième valeur
     * @param pos 
     * @param i 
     * @return valeur
     */
    public boolean get(int pos, int i);

    /**
     * cherche le vecteur complet à la position pos
     * @param pos 
     * @return vecteur
     */
    public SetOfBits get(int pos);

    /**
     * retourne la taille du vecteur
     * @return la taille du vecteur
     */
    public int length();

    /**
     * retourne la taille du vecteur à la position pos
     * @param pos 
     * @return la taille du vecteur à la position pos
     */
    public int length(int pos);

    /**  imprime des statistiques */
    public void printStatistic();

    /**  retourne des statistiques */
    public String getStatistic();
}
