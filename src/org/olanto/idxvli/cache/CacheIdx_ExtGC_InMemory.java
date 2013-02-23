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

package org.olanto.idxvli.cache;

/**
 *cache avec gestion externe du garbage collecting.
 * 
 *
 */
public class CacheIdx_ExtGC_InMemory implements CacheIdx {

    int maxCache; // size of  cache
    private long actualSize = 0;
    public int[][] v; // cache of idx

    /** Creates a new instance of Cache 
     * @param size nombre de termes maximum dans le cache
     */
    public CacheIdx_ExtGC_InMemory(int size) {  // default parameters
        maxCache = size;
        v = new int[maxCache][];
    }

    public final int[] getReferenceOn(int i) {
        return v[i];
    }

    public final int[] getCopyOf(int i) {
        // msg("v"+i+" ("+v[i].length+")");
        int[] copy = new int[v[i].length];
        System.arraycopy(v[i], 0, copy, 0, v[i].length);
        return copy;
    }

    public final int v(int i, int j) {
        return v[i][j];
    }

    public final int[] getv(int i, int from, int length) {
        //msg("getv for i:"+i+","+from+","+length);
        //showVector(v[i]);
        int[] copy = new int[length];
        System.arraycopy(v[i], from, copy, 0, length);
        //showVector(copy);
        return copy;
    }

    public final void setv(int i, int j, int value) {
        v[i][j] = value;
    }

    public final void incv(int i, int j) {
        v[i][j]++;
    }

    public final long getCurrentSize() {
        return 4 * actualSize;
    }

    public final boolean isNotLoaded(int i) {
        return v[i] == null;
    }

    public final boolean isLoaded(int i) {
        return v[i] != null;
    }

    public final int length(int i) {
        return v[i].length;
    }

    public final void newVector(int wn, int initialSize) {
        v[wn] = new int[initialSize]; // allocate the new vector in a free space
        actualSize += initialSize;
        //System.out.println(actualSize+" ,"+nextErase+" ,"+Array.getLength(v[nextSlot]));
    }

    public final void registerVector(int wn, int[] reg) {
        v[wn] = reg; // record this vector in the cache
        actualSize += reg.length;
    }

    public final void releaseVector(int n) { // set to null
        if (v[n] != null) {
            actualSize -= v[n].length;
            v[n] = null;
        }
    }

    public final void resize(int n, int newSize) { // resize the vector and copy first bytes
        int oldSize = v[n].length;
        if (oldSize != newSize) {
            int[] it = new int[newSize];
            if (oldSize < newSize) {
                System.arraycopy(v[n], 0, it, 0, oldSize);
            } else {
                System.arraycopy(v[n], 0, it, 0, newSize);
            }
            v[n] = it;
            actualSize += (newSize - oldSize);
        }
    }
}
