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

package org.olanto.profil;

import org.olanto.idxvli.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;

/**
 * Une classe pour initialiser les constantes.
 * <p>
 * 
 *<p>
 *
 *
 * Une classe pour initialiser les constantes. Cette classe doit ï¿½tre implï¿½mentï¿½e pour chaque application
 */
public class Configuration implements IdxInit {

    /** crï¿½e l'attache de cette classe.
     */
    public Configuration() {
    }

    /** initialisation permanante des constantes. 
     * Ces constantes choisies dï¿½finitivement pour toute la durï¿½e de la vie de l'index.
     */
    public void InitPermanent() {
        DOC_MAXBIT = 20;
        WORD_MAXBIT = 20;
        DOC_MAX = (int) Math.pow(2, DOC_MAXBIT);  // recalcule
        WORD_MAX = (int) Math.pow(2, WORD_MAXBIT); // recalcule

        IdxConstant.WORD_IMPLEMENTATION = implementationMode.FAST;
        IdxConstant.DOC_IMPLEMENTATION = implementationMode.FAST;
        IdxConstant.OBJ_IMPLEMENTATION = implementationMode.FAST;

        IDX_MORE_INFO = false;
        IDX_SAVE_POSITION = false;
        MODE_RANKING = RankingMode.IDFxTDF;
        MAX_RESPONSE = 200;

        IDX_DONTINDEXTHIS = "C:/JG/VLI_RW/data/dontindexthiswords.txt";
        DOC_SIZE_NAME = 32;

        IdxConstant.DOC_ENCODING = "UTF-8";
        IDX_MFLF_ENCODING = "UTF-8";
        WORD_MINLENGTH = 3;
        WORD_DEFINITION = new TokenIndexing();


        WORD_USE_STEMMER = false;
        WORD_STEMMING_LANG = "french"; // only for initialisation

        /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2 = 0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
        /** nbre d'object storage actif */
        OBJ_NB = (int) Math.pow(2, OBJ_PW2);  ///0=>1,1=>2,2=>4,
        OBJ_COMPRESSION = Compression.NO;
    }

    /** initialisation des constantes de configuration (modifiable). 
     * Ces constantes choisies dï¿½finitivement pour toute la durï¿½e de la vie du processus.
     */
    public void InitConfiguration() {

        // les directoire
        String root = "c:/JG/VLI_RW/data/objsto";
        String root0 = "c:/JG/VLI_RW/data/objsto/idx0";
        IdxConstant.COMMON_ROOT = root;
        IdxConstant.DOC_ROOT = root;
        IdxConstant.WORD_ROOT = root;
        SetObjectStoreRoot(root0, 0);

        // paramï¿½tre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING = implementationMode.FAST;
        CACHE_IMPLEMENTATION_READ = implementationMode.FAST;
        KEEP_IN_CACHE = 80;
        INDEXING_CACHE_SIZE = 64 * MEGA;
        QUERY_CACHE_SIZE = 128 * MEGA;

        IDX_MARKER = false;
        ORTOGRAFIC = false;
    }
}
