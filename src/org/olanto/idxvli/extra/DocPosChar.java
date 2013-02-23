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

import java.io.*;
import static org.olanto.idxvli.util.SetOperation.*;
import static org.olanto.idxvli.ql.QueryOperator.*;
import static org.olanto.idxvli.IdxConstant.*;
import org.olanto.idxvli.*;
import static org.olanto.util.Messages.*;

import org.olanto.idxvli.IdxEnum.*;

/** Une classe pour stocker la s�quence des positions des termes dans un document sous forme d'un
 * vecteur d'entier.
 *  Lors de l'indexation, il faut utiliser, un mode qui active la cr�ation
 *  des positions de terme -> docMore = true;
 *l'indexeur construit alors automatiquement les vecteurs des positions de termes.
 *apr�s coup, on peut recharger la s�quence avec getPosOfDoc dans IdxStructure
 *
 * 
 *
 *
 */
public class DocPosChar {

    // pour stocker la positions des termes
    private static int lastposchar = 0;
    private static int[] seqOfPosChar = new int[DOC_MAXOCCLENGTH];

    DocPosChar() {
    }

    /** initialise la cr�ation des positions (uniquement pour l'indexeur)
     */
    public static void init() {
        seqOfPosChar = new int[DOC_MAXOCCLENGTH];  // pour la cr�ation des positions
    }

    /** initialise des positions un nouveau document (uniquement pour l'indexeur)
     */
    public static void reset() {
        lastposchar = 0; // pr�pare un nouveau document
    }

    /**
     * ajoute une position (uniquement pour l'indexeur)
     * @param w position � ajouter
     */
    public static void addWord(int w) {
        seqOfPosChar[lastposchar] = w;
        lastposchar++;
    }

    /** retourne les positions compact�es (uniquement pour l'indexeur.
     * @return les positions compact�es
     */
    public static int[] compact() {
        int[] res = new int[lastposchar];
        System.arraycopy(seqOfPosChar, 0, res, 0, lastposchar);
        return res;
    }

    /** extrait le texte correspondant � la premi�re occurence de w dans le document
     * @param d r�f�rence du document
     * @param glue indexeur de r�f�rence
     * @param w terme dont on cherche le contexte
     * @param contextSize largeur du context
     * @return texte extrait du document
     */
    public static String extractForW(int d, IdxStructure glue, String w, int contextSize) {
        String plaintext = "not found";
        int[] doc = getDocforW(glue, w, RankingMode.NO).doc; // cherche le vecteur de document
        int iw = glue.getIntForW(w);
        if (doc != null) {
            int n = getIdxOfValue(doc, doc.length, d); // cherche la position du document
            if (n != -1) {
                int[] pos = glue.getWposition(iw, n); // load position for word iw , n th documents
                plaintext = extract(d, glue, pos[0], contextSize);
            }
        }
        return plaintext;
    }

    /** extrait l'interval correspondant � la premi�re occurence de w dans le document
     * @param d r�f�rence du document
     * @param glue indexeur de r�f�rence
     * @param w terme dont on cherche le contexte
     * @param contextSize largeur du context
     * @return texte extrait du document
     */
    public static FromTo extractIntervalForW(int d, IdxStructure glue, String w, int contextSize) {
        boolean verbose = false;
        // doit ^tre attentivement revue ... !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // pour travailler directement dans le cache et non pas sur des copies !!!!!!!!!!!!!!!!
        // cela marche bien avec le txt, ou les strings (pas UTF-8 en natif)
        int byteFactor = 1;  // =2 pour UTF-16 pour utf8 on doit bricoler ...
        int[] doc = getDocforW(glue, w, RankingMode.NO).doc; // cherche le vecteur de document
        if (verbose) {
            showVector(doc);
        }
        int iw = glue.getIntForW(w);
        if (doc != null) {
            int n = getIdxOfValue(doc, doc.length, d); // cherche la position du document
            if (verbose) {
                msg("position in vector:" + n);
            }
            if (n != -1) {
                glue.indexread.lockForFull(iw);
                int[] pos = glue.getWposition(iw, n); // load position for word iw , n th documents
                if (verbose) {
                    msg("position vector:");
                }
                if (verbose) {
                    showVector(pos);
                }
                int at = pos[0];  // la premi�re occurence
                int[] posChar = glue.getPosCharOfDoc(d);  // charge le vecteur des positions
                if (verbose) {
                    msg("posChar vector:");
                }
                if (verbose) {
                    showVector(posChar);
                }
                int from = Math.max(0, at - contextSize) * byteFactor;
                int to = Math.min(posChar.length - 2, at + contextSize) * byteFactor;   // -2 car la derni�re position est � z�ro bug ??
                from = posChar[from];  //en terme de caract�re
                to = posChar[to]; //en terme de caract�re
                glue.indexread.unlock(iw);
                return new FromTo(from, to);
            }
        }
        return null;
    }

    /** extrait le texte correspondant une position dans le document
     * @param d r�f�rence du document
     * @param glue indexeur de r�f�rence
     * @param at position � extraire
     * @param contextSize largeur du context
     * @return texte extrait du document
     */
    public static String extract(int d, IdxStructure glue, int at, int contextSize) {

        // cela marche bien avec le txt, pour le html, il faut �liminer les balises ...
        int byteFactor = 1;  // =2 pour UTF-16 pour utf8 on doit bricoler ...
        String encoding = "ISO-8859-1";
        String plaintext = "error during extraction";
        String f = glue.getFileNameForDocument(d);
        // byte Factor and encoding doivent �tre des propi�t�s du document !!!
        // actuellement on les consid�re fixe pour tout le corpus.
        int[] posChar = glue.getPosCharOfDoc(d);
        int from = Math.max(0, at - contextSize) * byteFactor;
        int to = Math.min(posChar.length - 2, at + contextSize) * byteFactor;   // -2 car la derni�re position est � z�ro bug ??
        //System.out.println("doc length:"+posChar.length+" idx from:"+from+", idx to:"+to);
        from = posChar[from];
        to = posChar[to];
        //System.out.println("pos from:"+from+", pos to:"+to);
        int lengthextract = to - from;
        // cas du txt ou autres
        try {
            RandomAccessFile refdoc = new RandomAccessFile(f, "r");
            //System.out.println("REF DOC"+f+":encoding "+encoding+":factor "+byteFactor);
            refdoc.seek(from); // position the cursor

            byte[] byteidx = new byte[lengthextract];
            refdoc.read(byteidx, 0, lengthextract);
            plaintext = new String(byteidx, encoding);
            plaintext = "<![CDATA[" + plaintext + "]]>";
            refdoc.close();

        } catch (Exception e) {
            System.err.println("IO error during open file:" + f);
            e.printStackTrace();
        }
        return plaintext;
    }
}
