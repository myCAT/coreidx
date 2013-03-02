/**
 * ********
 * Copyright © 2010-2012 Olanto Foundation Geneva
 *
 * This file is part of myCAT.
 *
 * myCAT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.idxvli.ql;

import org.olanto.util.TimerNano;
import java.io.*;
import java.util.*;
import org.olanto.idxvli.*;
import org.olanto.idxvli.server.*;
import org.olanto.idxvli.extra.*;
import org.olanto.conman.server.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.idxvli.util.BytesAndFiles.*;

/**
 * exécuteur de requête pour le browse.
 */
public class Browse_Basic implements BrowseManager {

    private static long totalTime = 0;
    private static int get = 0;

    public final QLResultNice get(IdxStructure id, String request, String langS, int start, int size, String[] collections, String order, boolean onlyOnFileName) {

        QLResultNice nice = evalQLNice(id, request, langS, start, size, collections, order, onlyOnFileName);
        totalTime += nice.duration;
        get++;
        printStatistic();
        return nice;
    }

    /**
     * imprime des statistiques
     */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**
     * retourne des statistiques
     */
    public final String getStatistic() {
        return "BROWSE cache statistics -> "
                + " get: " + get + " totalTime for browse: " + totalTime / 1000 + " [s] meanTime: " + totalTime / get + " [ms]";
    }

    public QLResultNice evalQLNice(IdxStructure id, String request, String langS, int start, int size, String[] collections, String order, boolean onlyOnFileName) {
        System.out.println("Browse query :"+request);
        QLResultNice res = null;
        TimerNano time = new TimerNano(request, true);
        if (DOCNAME_BROWSE_INSENSITIVE){request="(?i)"+request;}  // set insensitive pattern      
        String[] docname = id.docNameExpander.getExpand(request, langS, collections, DOCNAME_MAX_BROWSE, onlyOnFileName);  // difficile de ne pas ramener beaucoup pour être sur de bien trier

        if (docname == null) {// forme une réponse vide
            res = new QLResultNice(request, null, null, null, new int[0], null, null, null, time.stop(true) / 1000, null);
            return res;
        } else { // il a des résultats
            int[] doc = new int[docname.length];
            for (int i = 0; i < doc.length; i++) {
                doc[i] = id.getIntForDocument(docname[i]);
            }
            res = new QLResultNice(request, null, null, null, doc, docname, null, null, time.stop(true) / 1000, null);
            res.orderBy(id, order);
        }

        return res;
    }
}
