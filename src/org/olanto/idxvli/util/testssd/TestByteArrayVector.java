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

package org.olanto.idxvli.util.testssd;

import org.olanto.idxvli.util.ByteArrayVector;
import org.olanto.util.Timer;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 * 
 * 
 *
 *
 */
public class TestByteArrayVector {

    static ByteArrayVector o;

    public static void main(String[] args) {
        String volume = "F:/ajeter";
        int size = 124;
        int nb = 27;
        int init = 1000000;
        byte[] val = new byte[size];
        val[0] = 13;
        val[1] = 23;
        val[2] = 33;
        Timer t0 = new Timer("create struct");
        o = (new ByteArrayVector_OnDisk()).create(volume, "test", nb, size);
        t0.stop();
        t0 = new Timer("open struct");
        o = (new ByteArrayVector_OnDisk()).open(volume, "test", readWriteMode.rw);
        t0.stop();
        showVector(val);

        t0 = new Timer("init struct");
        for (int i = 0; i < init; i++) {
            o.set(i, val);
        }
        t0.stop();
        showVector(o.get(12));
        o.close();
        System.out.println("open again ...");
        o = (new ByteArrayVector_OnDisk()).open(volume, "test", readWriteMode.r);
        System.out.println("" + o.get(12)[2]);
        System.out.println("" + o.get(13)[1]);
        System.out.println("" + o.get(200));
        o.printStatistic();

        // performance test

        o.close();


    }
}
