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

package org.olanto.idxvli.util.test;

import org.olanto.util.TimerNano;
import static org.olanto.idxvli.util.SetOperation.*;

/**
 * 
 * 
 *
 *
 */
public class TestSetOperation {

    public static void main(String[] args) {

        int sl = 1000000;
        int[] vl = new int[sl];

        for (int i = 0; i < sl; i++) {
            vl[i] = 100 * i;
        }

        int ss = 101;
        int[] vs = new int[ss];

        for (int i = 0; i < ss; i++) {
            vs[i] = 1 * i;
        }

        TimerNano t = new TimerNano("1", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 10 * i;
        }
        t = new TimerNano("10", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);


        for (int i = 0; i < ss; i++) {
            vs[i] = 100 * i;
        }
        t = new TimerNano("100", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 1000 * i;
        }
        t = new TimerNano("1000", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 10000 * i;
        }
        t = new TimerNano("10000", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 100000 * i;
        }
        t = new TimerNano("100000", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 1000000 * i;
        }
        t = new TimerNano("1000000", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

    }
}
