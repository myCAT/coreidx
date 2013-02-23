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
 * Une classe pour initialiser les constantes.
 * 
 *
 * Une classe pour initialiser les constantes. Cette classe doit Ãªtre implÃ©mentÃ©e pour chaque application
 */
public interface IdxInit {

    /** initialisation permanante des constantes. 
     * Ces constantes choisies dÃ©finitivement pour toute la durÃ©e de la vie de l'index.
     */
    public void InitPermanent();

    /** initialisation des constantes de configuration (modifiable). 
     * Ces constantes choisies dÃ©finitivement pour toute la durÃ©e de la vie du processus.
     */
    public void InitConfiguration();
}
