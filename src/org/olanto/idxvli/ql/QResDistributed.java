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

import org.olanto.idxvli.server.QLResultAndRank;
import static org.olanto.util.Messages.*;

/**
 * Classe stockant les  r�sultats d'une requ�te distribu�e sur plusieurs indexeurs.
 * 
 *
 */
public class QResDistributed {

    /* les documents r�sultats tri�s*/
    public int[] topdoc;
    /* num�ro du noeud d'�ndexation*/
    public int[] topsource;
    /* les noms des documents tri�s*/
    public String[] topname;
    /* le degr� de pertinence du r�sultat tri�s*/
    public float[] toprank;
    /* le degr� de pertinence du r�sultat tri�s*/
    private QLResultAndRank[] resFromNet;
    private int maxToKeep = Integer.MAX_VALUE;
    /* les compteurs */
    private int nbNode = 0;
    private int nbNodeNotNull = 0;
    private int nbRes = 0;
    private int maxKept = 0;
    /* les variables de la fusion */
    private int[] next;
    private boolean[] active;

    /* initialise un resultat avec un ranking 0*/
    public QResDistributed(QLResultAndRank[] resFromNet, int maxToKeep) {
        this.resFromNet = resFromNet;
        this.maxToKeep = maxToKeep;
        initAndSort();
    }
    /* initialise le tri */

    public void initAndSort() {
        if (resFromNet != null) {
            nbNode = resFromNet.length;
            for (int i = 0; i < nbNode; i++) { // calcul le nombre noeud ayant donn� une r�ponse

                if (resFromNet[i] != null && resFromNet[i].result != null && resFromNet[i].result.length != 0) {
                    nbNodeNotNull++;
                }
            }
            for (int i = 0; i < nbNode; i++) { // calcul le nombre de r�ponses

                if (resFromNet[i] != null && resFromNet[i].result != null && resFromNet[i].result.length != 0) {
                    nbRes += resFromNet[i].result.length;
                }
            }
            maxKept = Math.min(nbRes, maxToKeep);
            msg(" nbNode: " + nbNode);
            msg(" nbNodeNotNull: " + nbNodeNotNull);
            msg(" nbRes: " + nbRes);
            msg(" maxKept: " + maxKept);
            next = new int[nbNode];
            active = new boolean[nbNode];
            for (int i = 0; i < nbNode; i++) { // initialise la fusion

                if (resFromNet[i] != null && resFromNet[i].result != null && resFromNet[i].result.length != 0) {
                    active[i] = true;
                    next[i] = 0;
                } else {
                    active[i] = false;
                }
            }
            topdoc = new int[maxKept];
            topsource = new int[maxKept];
            toprank = new float[maxKept];
            topname = new String[maxKept];
            for (int i = 0; i < maxKept; i++) {
                int topNode = getTopNode();
//                msg(i + " - " + topNode);
                topdoc[i] = resFromNet[topNode].result[next[topNode]];
                topsource[i] = topNode;
                toprank[i] = resFromNet[topNode].rank[next[topNode]];
                topname[i] = resFromNet[topNode].docName[next[topNode]];
                next[topNode]++;
            }
        } else {
            msg("no vector of Queries");
        }
    }

    private int getTopNode() {
        float max = -1;
        int topNode = -1;
        for (int i = 0; i < nbNode; i++) { // cherche le noeud le plus grand

            if (active[i]) {
                //               msg(i + ", " + next[i] + ", " + resFromNet[i].rank[next[i]] + ", " + max + ", " + topNode);
                if (next[i] < resFromNet[i].result.length
                        && resFromNet[i].rank[next[i]] > max) {
                    topNode = i;
                    max = resFromNet[i].rank[next[i]];
//                msg(i + ", " + next[i] + ", " + resFromNet[i].rank[next[i]] + ", " + max + ", " + topNode);
                }
            }
        }
        return topNode;
    }
}
