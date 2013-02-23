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

package org.olanto.conman.test;

import org.olanto.conman.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.conman.ContentConstant.*;

/**
 * Une classe pour initialiser les constantes.
 * 
 */
public class ConfigurationContentManager implements ContentInit {

    /** crï¿½e l'attache de cette classe.
     */
    public ConfigurationContentManager() {
    }

    /** initialisation permanante des constantes.
     * Ces constantes choisies dï¿½finitivement pour toute la durï¿½e de la vie de l'index.
     */
    public void InitPermanent() {

        COMLOG_FILE = "C:/JG/VLI_RW/data/javaora/common.log";

        DETLOG_FILE = "C:/JG/VLI_RW/data/javaora/detail.log";


        DOC_MAXBIT = 18;
        DOC_MAX = (int) Math.pow(2, DOC_MAXBIT);  // recalcule

        DOC_IMPLEMENTATION = implementationMode.FAST;
        OBJ_IMPLEMENTATION = implementationMode.FAST;

        /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2 = 0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
        OBJ_NB = (int) Math.pow(2, OBJ_PW2);  ///0=>1,1=>2,2=>4,


        /** taille maximum des noms de documents */
        DOC_SIZE_NAME = 256;
    }

    /** initialisation des constantes de configuration (modifiable).
     * Ces constantes choisies dï¿½finitivement pour toute la durï¿½e de la vie du processus.
     */
    public void InitConfiguration() {

        // les directoire
        String root = "c:/JG/VLI_RW/data/javaora";
        String root0 = "c:/JG/VLI_RW/data/javaora/conman0";
//        String root1="c:/JG/gigaversion/data/conman1";
//        String root2="c:/JG/gigaversion/data/conman2";
//        String root3="c:/JG/gigaversion/data/conman3";
        COMMON_ROOT = root;
        DOC_ROOT = root;
        SetObjectStoreRoot(root0, 0);
//        SetObjectStoreRoot(root1,1);
//        SetObjectStoreRoot(root2,2);
//        SetObjectStoreRoot(root3,3);

        // paramï¿½tre de fonctionnement
    }
}
