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

package org.olanto.idxvli.extra;

import static org.olanto.idxvli.IdxConstant.*;

import org.olanto.idxvli.*;

/**
 * Une classe pour effectuer la classification des documents.
 * <p>
 * 
 *
 *
 */
public class DocBagInteractive {

    IdxStructure glue;
    static final int MAXOCCINDOC = DocBag.MAXOCCINDOC; // get the same max
    int[] bagOfWords = new int[WORD_MAX];  //this must be optimize for many users .... !!!!!!! (too big)
    int differentWords;

    /**
     * crÃ©e un sac pour une saisie depuis l'interface.
     * @param _glue indexeur associÃ©
     */
    public DocBagInteractive(IdxStructure _glue) {
        glue = _glue;
        differentWords = 0;
        for (int i = 0; i < WORD_MAX; i++) {
            bagOfWords[i] = NOT_FOUND;
        }
    }

    /**
     * ajoute un mot au sac (uniquement pour l'indexeur)
     * @param w mot Ã  ajouter
     */
    public void addWord(int w) {
        if (bagOfWords[w] == NOT_FOUND) {
            differentWords++;
            bagOfWords[w] = w * MAXOCCINDOC + 1;
        } else {
            if ((bagOfWords[w] % MAXOCCINDOC) < MAXOCCINDOC - 1) {
                bagOfWords[w]++;
            }
        }
    }

    /** retourne le sac de mots compactÃ© (uniquement pour l'indexeur. Les indices ne sont pris que dans l'intervalle de lecture
     * @return le sac de mots compactÃ©
     */
    public int[] compact() {
        int nextempty = 0;
        for (int i = 0; i < glue.lastUpdatedWord; i++) {  //
            if (bagOfWords[i] != NOT_FOUND) {
                bagOfWords[nextempty] = bagOfWords[i];
                nextempty++;
            }
        }
        int[] res = new int[nextempty];
        System.arraycopy(bagOfWords, 0, res, 0, nextempty);
        return res;
    }

    /** retourne le nombre de mots actuellement dans le sac
     * @return le nombre de mots actuellement dans le sac
     */
    public int getDifferentWords() {
        return differentWords;
    }
}
