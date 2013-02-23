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

import java.util.*;

/**
 * gère un espace d'adressage qui est plus grand que celui du cache.
 * 
 */
public class CacheTranslate_Subset implements CacheTranslate {

    static int cacheSize;
    static HashMap<Integer, Integer> wc;  // du word id -> cache id
    static HashMap<Integer, Integer> cw;  // du word id -> cache id
    static int nextCacheId = -1;  // next val =0;
    static long vdoc, getvdoc, vpos, getvpos;  // to debug

    /** crée un tranlateur vers un espace de taille max _cacheSize
     * @param _cacheSize taille max
     */
    public CacheTranslate_Subset(int _cacheSize) {
        //  System.out.println("CacheRead_ExtGC:"+maxSize);
        cacheSize = _cacheSize;
        wc = new HashMap<Integer, Integer>(2 * cacheSize);
        cw = new HashMap<Integer, Integer>(2 * cacheSize);
        nextCacheId = 0;
    }

    private final int nextCacheId() {
        return nextCacheId++; // on a le bon candidat
    }

    public final int registerCacheId(int wordId) {
        Integer res = wc.get(wordId);
        if (res == null) {
            int nextId = nextCacheId();
            wc.put(wordId, nextId);
            cw.put(nextId, wordId);
            return nextId;
        }
        return res.intValue();
    }

    public final int getCacheId(int wordId) {
        return wc.get(wordId);
    }

    public final int getWordId(int cacheId) {
        return cw.get(cacheId);
    }

    public final void resetCache() {
        wc = new HashMap<Integer, Integer>(2 * cacheSize);
        cw = new HashMap<Integer, Integer>(2 * cacheSize);
        nextCacheId = 0;
    }

    public final int capacity() {
        return cacheSize;
    }

    public final int allocate() {
        return nextCacheId + 1;
    } // 
}
