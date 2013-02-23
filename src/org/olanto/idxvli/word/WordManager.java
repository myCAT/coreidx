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

package org.olanto.idxvli.word;

import static org.olanto.idxvli.IdxEnum.*;

/**
 * 
 * Comportements d'un gestionnaire de dictionnaire.
 * <p>
 * Un gestionnaire de documents ce type implémente:<br>
 * - la création du gestionnaire<br>
 * - l'ouverture du gestionnaire<br>
 * - la fermeture du gestionnaire<br>
 * - la recherche d'un mot<br>
 * - la recherche d'un mot<br>
 * - ...
 * 
 * 
 *
 * 
 */
public interface WordManager {

    /**
     * crée un gestionnaire  de 2^maxSize, à l'endroit indiqué par le path.
     * @param implementation cette classe possède plusieurs option d'implémentation (FAST,BIG)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @param maxSize nbre de référence maximum mémorisées = 2^maxSize
     * @param maxLengthSizeOfName taille maximum des mots = nbr de byte UTF8
     * @return un gestionnaire de mots
     */
    public WordManager create(implementationMode implementation,
            String path, String file, int maxSize, int maxLengthSizeOfName);

    /**
     * ouvre le gestionnaire à l'endroit indiqué par le path et le file. Normalement les modes implemenation, keepLanguage,
     * keepCollection doivent être les mêmes que ceux utilisés lors de la création.
     * @param implementation cette classe possède plusieurs option d'implémentation (FAST,BIG)
     * @param RW mode lecture/écriture (rw,r)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @return un gestionnaire de mots
     */
    public WordManager open(implementationMode implementation,
            readWriteMode RW, String path, String file);

    /**  ferme le gestionnaire   (et sauve les modifications*/
    public void close();

    /**
     * ajoute un mot au gestionnaire retourne le numéro du mot, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe déja
     * @param word mot
     * @return le numéro attribué au mot
     */
    public int put(String word);

    /**
     * cherche le numéro du mot, retourne EMPTY s'il n'est pas dans le dictionnaire
     * @param word mot
     * @return le numéro du mot
     */
    public int get(String word);

    /**
     * cherche le mot associé à un identifiant. Retourne NOTINTHIS s'il n'est pas dans le dictionnaire
     * @param i numéro du mot
     * @return le nom du mot
     */
    public String get(int i);

    /**
     * retourne le nbr de mots mémorisés dans le gestionnaire.
     * @return le nbr de mots mémorisés
     */
    public int getCount();

    /**  imprime des statistiques  du gestionnaire de mots*/
    public void printStatistic();
}
