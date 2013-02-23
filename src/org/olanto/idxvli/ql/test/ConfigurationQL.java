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

package org.olanto.idxvli.ql.test;

import org.olanto.idxvli.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;

/**
 * Une classe pour initialiser les constantes.
 * 
 *
 *
 * Une classe pour initialiser les constantes. Cette classe doit Ãªtre implÃ©mentÃ©e pour chaque application
 */
public class ConfigurationQL implements IdxInit {

    /** crÃ©e l'attache de cette classe.
     */
    public ConfigurationQL() {
    }

    /** initialisation permanante des constantes.
     * Ces constantes choisies dÃ©finitivement pour toute la durÃ©e de la vie de l'index.
     */
    public void InitPermanent() {

        DOC_MAXBIT = 17;
        WORD_MAXBIT = 17;
        DOC_MAX = (int) Math.pow(2, DOC_MAXBIT);  // recalcule
        WORD_MAX = (int) Math.pow(2, WORD_MAXBIT); // recalcule

        WORD_IMPLEMENTATION = implementationMode.FAST;
        DOC_IMPLEMENTATION = implementationMode.FAST;
        OBJ_IMPLEMENTATION = implementationMode.FAST;


        IDX_MFLF_ENCODING = "ISO-8859-1";  //"UTF-8";


        //WORD_DEFINITION=new TokenAlphaNumeric();

    }

    /** initialisation des constantes de configuration (modifiable).
     * Ces constantes choisies dÃ©finitivement pour toute la durÃ©e de la vie du processus.
     */
    public void InitConfiguration() {

        // les directoire
        String root = "c:/JG/gigaversion/data/objsto";
        String root0 = "c:/JG/gigaversion/data/objsto0";
        String root1 = "c:/JG/gigaversion/data/objsto1";
        String root2 = "c:/JG/gigaversion/data/objsto2";
        String root3 = "c:/JG/gigaversion/data/objsto3";
        COMMON_ROOT = root;
        DOC_ROOT = root;
        WORD_ROOT = root;
        SetObjectStoreRoot(root0, 0);
        SetObjectStoreRoot(root1, 1);
        SetObjectStoreRoot(root2, 2);
        SetObjectStoreRoot(root3, 3);

        // paramÃ¨tre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING = implementationMode.FAST;
        CACHE_IMPLEMENTATION_READ = implementationMode.FAST;
        KEEP_IN_CACHE = 80;
        INDEXING_CACHE_SIZE = 64 * MEGA;

    }
}
