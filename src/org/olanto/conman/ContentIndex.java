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

package org.olanto.conman;

import org.olanto.idxvli.*;
import static org.olanto.util.Messages.*;

/**
 * Une classe pour indexer les contents manager.
 * 
 *<p>
 */
public class ContentIndex {

    ContentStructure cm;
    IdxStructure id;

    public ContentIndex(ContentStructure cm, IdxStructure id) {
        this.cm = cm;
        this.id = id;
    }

    /** Affiche dans la console des statistiques sur l'indexeur.
     */
    public void indexAll() {

        for (int i = 0; i < cm.lastdoc; i++) {
            if (cm.isIndexable(i)) {
                String name = cm.getFileNameForDocument(i);
                String content = cm.getStringContent(i);
                msg("index " + i + " :" + name + " length:" + content.length());
                id.indexThisContent(name, content);
                cm.setIndexed(i);
            }
        }

    }
}
