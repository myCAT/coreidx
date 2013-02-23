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

import org.olanto.idxvli.*;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.util.Messages.*;

/** Une classe pour stocker la s�quence des termes des documents sous forme d'un vecteur d'entier.
 * <pre>
 *  Lors de l'indexation, il faut utiliser, un mode qui active la cr�ation
 *  des s�quence de terme -> docMore = true;
 *
 *  l'indexeur construit alors automatiquement les vecteurs de s�quence de termes.
 *
 *  apr�s coup, on peut recharger la s�quence avec getSeqOfDoc dans IdxStructure
 *
 * 
 *
 *
 */
public class DocSeq {

    // pour stocker la s�quence des termes
    /** longueur maximum d'un document en termes index�s */
    static int lastseq = 0;
    static int[] seqOfWords = new int[DOC_MAXOCCLENGTH];

    DocSeq() {
    }

    /** initialise la cr�ation de s�quences (uniquement pour l'indexeur)
     */
    public static void init() {
        seqOfWords = new int[DOC_MAXOCCLENGTH];  // pour la cr�ation des s�quences
    }

    /** initialise la s�quence pour un nouveau document (uniquement pour l'indexeur)
     */
    public static void reset() {
        lastseq = 0; // pr�pare un nouveau document
    }

    /**
     * ajoute un mot � la s�quence (uniquement pour l'indexeur)
     * @param w mot � ajouter
     */
    public static void addWord(int w) {
        seqOfWords[lastseq] = w;
        lastseq++;
    }

    /** retourne  la s�quence compact�e (uniquement pour l'indexeur.
     * @return la s�quence compact�e
     */
    public static int[] compact() {
        int[] res = new int[lastseq];
        System.arraycopy(seqOfWords, 0, res, 0, lastseq);
        return res;
    }

    /** convertir une s�quence en liste de mots
     * @param seq vecteur de termes
     * @param glue indexeur de r�f�rence
     * @return liste de mots
     */
    public static String[] toTextList(int[] seq, IdxStructure glue) {
        String[] res = new String[seq.length];
        for (int i = 0; i < seq.length; i++) {
            res[i] = glue.getStringforW(seq[i]);
        }
        return res;
    }

    /** convertir une s�quence en chaine de caract�res
     * @param seq vecteur de termes
     * @param glue indexeur de r�f�rence
     * @return traduction en caract�res
     */
    public static String toText(int[] seq, IdxStructure glue) {
        String res = "";
        for (int i = 0; i < seq.length; i++) {
            res += glue.getStringforW(seq[i]) + " ";
        }
        return res;
    }

    /** extrait le texte correspondant � une sous s�quence du document
     * @param seq sequence de r�f�rence du document
     * @param glue indexeur de r�f�rence
     * @param from d�but de la s�quence � extraire
     * @param to fin de la s�quence � extraire
     * @return texte extrait du document
     */
    public static String extract(int[] seq, IdxStructure glue, int from, int to) {
        String res = "";
        for (int i = from; i < to; i++) {
            res += glue.getStringforW(seq[i]) + " ";
        }
        return res;
    }

    /** extrait les id des mots d'une sous s�quence du document
     * @param seq sequence de r�f�rence du document
     * @param from d�but de la s�quence � extraire
     * @param to fin de la s�quence � extraire
     * @return id extrait du document
     */
    public static int[] extract(int[] seq, int from, int to) {
        try {
            int[] res = new int[to - from];
            for (int i = from; i < to; i++) {
                res[i - from] = seq[i];
            }
            return res;
        } catch (Exception e) {
            error("extract position from:" + from + " to:" + to);
            return new int[0];
        }
    }
}
