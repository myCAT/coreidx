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

package org.olanto.idxvli.ql;

import org.olanto.idxvli.*;
import org.olanto.conman.server.*;
import org.olanto.idxvli.server.*;

/**
 * Comportement d'un ex�cuteur de requ�te.
 * 
 *
 *
 * Comportement d'un ex�cuteur de requ�te.
 */
public interface QLManager {

    /**
     * retourne la liste des documents valides correspondants � la requ�te, (null) si erreur.
     * @param request requ�te
     * @param id indexeur de r�f�rence
     * @return la liste des documents valides
     */
    public int[] get(String request, IdxStructure id);

    public void initCache();
    
    public QLResultAndRank getMore(String request, IdxStructure id);

    public QLResultNice get(IdxStructure id, ContentService cs, String request, int start, int size);

    public QLResultNice get(IdxStructure id, ContentService cs, String request1, String request2, int start, int size1, int size2);

    public QLResultNice get(IdxStructure id, ContentService cs, String request, String properties, int start, int size);

    public QLResultNice get(IdxStructure id, ContentService cs, String request, String properties, String profile, int start, int size);
}
