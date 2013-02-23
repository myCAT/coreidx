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

package org.olanto.onto;

/**
 * 
 * Comportements d'un gestionnaire de lexiques.
 * <p>
 * Un gestionnaire de lexiques ce type implémente:<br>
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
public interface LexicManager {

    /**
     * crée le gestionnaire avec le fichier donné
     * @param file nom du fichier
     * @return un gestionnaire de lexiques
     */
    public LexicManager create(String file, String lang, String stemName);

    /**
     * ajoute un mot au gestionnaire 
     * retourne son id s'il existe déja
     * @param word mot
     * @param id id
     */
    public void put(String word, int id);

    /**
     * cherche les concepts du mot, retourne NULL s'il n'est pas dans le dictionnaire
     * @param word mot
     * @return un vecteur de concepts
     */
    public Concepts get(String word);

    /**
     * cherche les concepts du mot, retourne NULL s'il n'est pas dans le dictionnaire
     * @param word mot (sans stemming)
     * @param stem lemme de word 
     * @return un vecteur de concepts
     */
    public Concepts get(String word, String stem);

    /**
     * cherche les mots associés à un identifiant de concepts.
     * @param i numéro du concepts
     * @return la liste des termes
     */
    public Terms get(int i);

    /**
     * cherche les mots sans stemming associés à un identifiant de concepts.
     * @param i numéro du concepts
     * @return la liste des termes
     */
    public Terms getw(int i);

    /**  imprime des statistiques  du gestionnaire de mots*/
    public void printStatistic();
}
