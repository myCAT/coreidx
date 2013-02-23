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

package org.olanto.idxvli;

/**
 * Une classe pour définir la notion de terme.
 * 
 * Une classe pour définir la notion de terme. Cette classe doit être implémentée pour chaque application
 */
public interface TokenDefinition {

    /**
     * Cherche le symbole suivant.
     * @param a le parseur courant
     */
    public void next(DoParse a);

    /** normalise le mot.
     * @param id l'indexeur de référence
     * @param w le mot à normaliser
     * @return un mot normalisé
     */
    public String normaliseWord(IdxStructure id, String w);
}
