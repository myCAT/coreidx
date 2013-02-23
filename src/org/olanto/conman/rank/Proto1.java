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

package org.olanto.conman.rank;

import static org.olanto.util.Messages.*;
import org.olanto.util.TimerNano;

/** prototype de ranking par les liens.
 * 
 * 
 *
 * 
 */
public class Proto1 {

    static final int LEVEL = 20;
    static final int MAX = (int) Math.pow(2, LEVEL);
    static int[][] invlink = new int[MAX - 1][];
    static int[] cntlink = new int[MAX - 1];
    static float[] wnode = new float[MAX - 1];
    static int last = 0;
    static int lastzero = 0;

    public static void main(String[] args) {


        TimerNano t1 = new TimerNano("global", false);
        init();

        for (int k = 0; k < 16 * LEVEL; k++) {
            relax();
            msg(k + "," + wnode[0] + "," + wnode[1] + "," + wnode[3] + "," + wnode[5] + "," + wnode[7] + "," + wnode[9] + "," + wnode[11] + "," + wnode[13] + "," + wnode[15] + "," + wnode[17]);
        }
        t1.stop(false);

    }

    static void relax() {
        for (int k = 0; k < last; k++) {
            int i = (int) (Math.random() * last);

            float sum = 0;
            for (int j = 0; j < invlink[i].length; j++) {
                sum += wnode[invlink[i][j]] / (float) cntlink[invlink[i][j]];
            }
            wnode[i] = sum;
        }
    }

    static void init() {
        last = 1;
        invlink[0] = new int[MAX / 2];
        for (int i = 1; i < MAX - 1; i++) {
            invlink[i] = new int[1];
        }
        bi(0, 0);
        msg("last:" + last + " lastzero:" + lastzero);
//        showVector(invlink[0]);
//        showVector(invlink[1]);
//        showVector(invlink[2]);
//        showVector(invlink[3]);
//        showVector(invlink[4]);
//        showVector(invlink[5]);
//        showVector(cntlink);
        for (int i = 0; i < last; i++) {
            wnode[i] = MAX;
        }
    }

    static void bi(int s, int level) {
        if (level != LEVEL - 1) {
            int loc = last;
            invlink[last][0] = s;
            cntlink[s]++;
            invlink[last + 1][0] = s;
            cntlink[s]++;
            last += 2;
            bi(loc, level + 1);
            bi(loc + 1, level + 1);
        } else {
            invlink[0][lastzero] = s;
            cntlink[s]++;
            lastzero++;
        }
    }
}
