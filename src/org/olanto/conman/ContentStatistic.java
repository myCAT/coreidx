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

import static org.olanto.util.Messages.*;
import static org.olanto.conman.ContentConstant.*;

/**
 * Une classe pour collecter des informations statistiques sur l'indexeur.
 */
public class ContentStatistic {

    ContentStructure glue;

    ContentStatistic(ContentStructure id) {
        glue = id;
    }

    /** Affiche dans la console des statistiques sur l'indexeur.
     */
    public void global() {
        msg("STATISTICS global:");
        msg("documax: " + DOC_MAX + ", docNnow: " + (glue.lastdoc) + ", used: " + ((glue.lastdoc * 100) / DOC_MAX) + "%");
        if (glue.lastdoc != 0) {
            msg("docNow: " + (glue.lastdoc) + " docValid: " + (glue.docstable.countValid() + ", valid: " + (glue.docstable.countValid() * 100) / glue.lastdoc) + "%");
        }
        contentSize();
    }

    public String getGlobal() {
        return "Kdoc: " + DOC_MAX / 1024 + "/" + (glue.lastdoc / 1024) + " " + ((glue.lastdoc * 100) / Math.max(DOC_MAX, 1)) + "%";
    }

    public void document() {
        glue.docstable.printStatistic();
    }

    public void content() {
        glue.IO.printContentStatistic();
    }

    public void contentSize() {
        msg("STATISTICS content size:");
        msg("docNow: " + glue.lastdoc);
        long real = 0;
        long stored = 0;
        for (int i = 0; i < glue.lastdoc; i++) {
            real += glue.IO.realSizeOfContent(i);
            stored += glue.IO.storeSizeOfContent(i);
        }

        msg("realSize: " + (real / MEGA)
                + " [Mb], storedSize: " + (stored / MEGA)
                + " Compression: " + (stored * 100) / (real + 1) + "%");
    }
}
