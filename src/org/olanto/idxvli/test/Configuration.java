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

package org.olanto.idxvli.test;

import org.olanto.idxvli.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;

/**
 * Une classe pour initialiser les constantes.
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
        CACHE_IMPLEMENTATION_INDEXING = implementationMode.FAST;
        CACHE_IMPLEMENTATION_READ = implementationMode.FAST;

    }

    /** initialisation des constantes de configuration (modifiable). 
     * Ces constantes choisies dï¿½finitivement pour toute la durï¿½e de la vie du processus.
     */
    public void InitConfiguration() {

        // les directoire
        String root = "c:/JG/gigaversion/data/objsto";
        String root0 = "c:/JG/gigaversion/data/objsto0";
        String root1 = "c:/JG/gigaversion/data/objsto1";
        String root2 = "c:/JG/gigaversion/data/objsto2";
        String root3 = "c:/JG/gigaversion/data/objsto3";
        IdxConstant.COMMON_ROOT = root;
        IdxConstant.DOC_ROOT = root;
        IdxConstant.WORD_ROOT = root;
        SetObjectStoreRoot(root0, 0);
        SetObjectStoreRoot(root1, 1);
        SetObjectStoreRoot(root2, 2);
        SetObjectStoreRoot(root3, 3);

        // paramï¿½tre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING = implementationMode.FAST;
        CACHE_IMPLEMENTATION_READ = implementationMode.FAST;
        KEEP_IN_CACHE = 80;
        INDEXING_CACHE_SIZE = 64 * MEGA;
        QUERY_CACHE_SIZE = 64 * MEGA;

    }
}
