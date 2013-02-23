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

/**
 *
 * 
 *
 *
 */
public class TestTimerNano {

    public static void main(String[] args) {
        TimerNano t1;
        for (int i = 0; i < 1000; i++) {
            t1 = new TimerNano("empty", true);
            t1.stop(false);

            t1 = new TimerNano("empty2", true);
            t1.stop(false);
        }
    }
}
