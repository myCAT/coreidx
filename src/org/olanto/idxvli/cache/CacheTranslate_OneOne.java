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
 * implémentation trivial sans translation.
 * 
 *
 * Cette impl�mentation est utilis�e dans les stratégies FAST qui peuvent tenir en mémoire.
 */
public class CacheTranslate_OneOne implements CacheTranslate {

    static int cacheSize;

    public CacheTranslate_OneOne(int _cacheSize) {
        cacheSize = _cacheSize;
    }

    public final int registerCacheId(int wordId) {
        return wordId;
    }

    public final int getCacheId(int wordId) {
        return wordId;
    }

    public final int getWordId(int wordId) {
        return wordId;
    }

    public final void resetCache() {
    }

    public final int capacity() {
        return cacheSize;
    }

    public final int allocate() {
        return 0;
    } // ne peut pas d�border
}
