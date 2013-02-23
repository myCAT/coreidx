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

package org.olanto.idxvli.ref;

import java.io.*;

/**
 * gestion simple d'un ensemble épars de bit.
 * 
 *
 * <p>
 */
class SparseBitSet implements Serializable {

    private static final int EOSBS = -1;
    private  int cursor;
    private  int cursor2;
    private int[] delta;

    SparseBitSet() {
    }

    SparseBitSet(int[] delta) { // order by number
        this.delta = delta;
    }

    protected final void resetcursor() {
        cursor = 0;
    }

    protected final int getNextPos() { // one by one
        if (delta == null) {
            return EOSBS;
        } else {
            if (cursor == delta.length) {
                return EOSBS;
            } else {
                int position = delta[cursor];
                cursor++;
                return position;
            }
        }
    }

    protected final void resetcursor2() {
        cursor2 = 0;
    }

    protected final int getNextPos2() { // one by one
        if (delta == null) {
            return EOSBS;
        } else {
            if (cursor2 == delta.length) {
                return EOSBS;
            } else {
                int position = delta[cursor2];
                cursor2++;
                return position;
            }
        }
    }

    protected final int length() { // only one by one
        if (delta == null) {
            return 0;
        }
        return delta.length;
    }

    protected final void addbit(int position) { // must be ordered
        if (delta == null) {
            delta = new int[1];
            delta[0] = position;
        } else {
            int l = delta.length;
            int[] it = new int[l + 1];
            System.arraycopy(delta, 0, it, 0, l);
            delta = it;
            delta[l] = position;
        }
    }

    protected final void insertbit(int position) { // 
        if (delta == null) {
            delta = new int[1];
            delta[0] = position;
        } else {
            //System.out.print("before copy: ");showVector(delta);
            int l = delta.length;
            int i = 0;
            for (i = 0; i < l; i++) {
                if (delta[i] >= position) {
                    break;
                }
            }

            if ((i == l) || (delta[i] != position)) {
                int[] it = new int[l + 1];
                //System.out.println("insert: "+position+" i: "+i+" l: "+l);
                System.arraycopy(delta, 0, it, 0, i); // copy first part
                System.arraycopy(delta, i, it, i + 1, l - i); // copy second part      
                delta = it;
                //System.out.print("after copy: ");showVector(delta);
                if (i == 0) {
                    delta[0] = position;
                } // if the first
                else {
                    if (i == l) {
                        delta[i] = position;
                    } // if  the last
                    else {
                        delta[i] = position;
                    }
                }
            } // already set
            //System.out.print("end insert: ");showVector(delta);

        }
    }

    protected final static void showVector(int[] p) {
        int l = p.length;
        for (int i = 0; i < l; i++) {
            System.out.print(p[i] + ",");
        }
        System.out.println();
    }

    protected final SparseBitSet and(SparseBitSet p) {
        SparseBitSet res = new SparseBitSet();
        int wc1 = 0, wc2 = 0, il1, il2;
        if ((p.delta == null) | (this.delta == null)) {
            return res;
        } else {
            il1 = this.delta.length;
            il2 = p.delta.length;
            while (true) { // merge sort  must be ordered !!!!!
                // System.out.println(wc1+", "+this.delta[wc1]+", "+wc2+", "+p.delta[wc2]);
                if (this.delta[wc1] == p.delta[wc2]) { // and ok
                    res.addbit(this.delta[wc1]);
                    wc1++;
                    if (wc1 >= il1) {
                        break;
                    }
                    wc2++;
                    if (wc2 >= il2) {
                        break;
                    }
                } else if (this.delta[wc1] < p.delta[wc2]) {
                    wc1++;
                    if (wc1 >= il1) {
                        break;
                    }
                } else {
                    wc2++;
                    if (wc2 >= il2) {
                        break;
                    }
                }
            } // while
        } // else
        return res;
    }

    protected final boolean notEmpty() {
        if (delta != null) {
            return true;
        }
        return false;
    }

    protected final boolean intersect(SparseBitSet p) { // true if one bit in the and
        int wc1 = 0, wc2 = 0, il1, il2;
        if ((p.delta == null) | (this.delta == null)) {
            return false;
        } else {
            il1 = this.delta.length;
            il2 = p.delta.length;
            while (true) { // merge sort  must be ordered !!!!!
                // System.out.println(wc1+", "+this.delta[wc1]+", "+wc2+", "+p.delta[wc2]);
                if (this.delta[wc1] == p.delta[wc2]) { // and ok
                    return true;
                } else if (this.delta[wc1] < p.delta[wc2]) {
                    wc1++;
                    if (wc1 >= il1) {
                        break;
                    }
                } else {
                    wc2++;
                    if (wc2 >= il2) {
                        break;
                    }
                }
            } // while
        } // else
        return false;
    }

    protected final void print() { // must be ordered
        if (delta == null) {
            System.out.println("SBS is empty");
        } else {
            System.out.print("SBS = ");
            int l = delta.length;
            for (int i = 0; i < l; i++) {
                System.out.print(delta[i] + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * @return the delta
     */
    public int[] getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(int[] delta) {
        this.delta = delta;
    }
}
