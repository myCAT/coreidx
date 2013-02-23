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
 * gestionnaire d'un cache d'index en lecture uniquement.
 * 
 *
 * Comportement d'un cache d'index en lecture uniquement.
 * Les indices des termes sont ceux utilisÃ©s par le dictionnaire 
 * (donc par forcemment ceux du cache de termes).
 * <p>
 * Dans cette version le GC doit ï¿½tre organisï¿½ ï¿½ l'extï¿½rieur, aucune translation n'est prï¿½vue.
 * donc cacheSize = maxWord
 *
 * pour l'essentiel, cette classe crï¿½e une facade de CacheIdx sur les documents et sur les positions.
 */
public class CacheRead_Basic implements CacheRead {

    private CacheIdx_ExtGC doc;
    private CacheIdx_ExtGC pos;

    public CacheRead_Basic(int maxWord) {
        //  System.out.println("CacheRead_ExtGC:"+maxWord);
        doc = new CacheIdx_ExtGC(maxWord);
        pos = new CacheIdx_ExtGC(maxWord);
    }

    public final int[] getReferenceOnDoc(int i) {
        return doc.getReferenceOn(i);
    }

    public final int[] getCopyOfDoc(int i) {
        return doc.getCopyOf(i);
    }

    public final int vDoc(int i, int j) {
        return doc.v(i, j);
    }

    public final int[] getvDoc(int i, int from, int length) {
        return doc.getv(i, from, length);
    }

    public final boolean isNotLoadedDoc(int i) {
        return doc.isNotLoaded(i);
    }

    public final boolean isLoadedDoc(int i) {
        return doc.isLoaded(i);
    }

    public final int lengthDoc(int i) {
        return doc.length(i);
    }

    public final void registerVectorDoc(int wn, int[] reg) {
        doc.registerVector(wn, reg);
    }

    public final void releaseVectorDoc(int n) {
        doc.releaseVector(n);
    }

    public final int[] getReferenceOnPos(int i) {
        return pos.getReferenceOn(i);
    }

    public final int[] getCopyOfPos(int i) {
        return pos.getCopyOf(i);
    }

    public final int vPos(int i, int j) {
        return pos.v(i, j);
    }

    public final int[] getvPos(int i, int from, int length) {
        return pos.getv(i, from, length);
    }

    public final boolean isNotLoadedPos(int i) {
        return pos.isNotLoaded(i);
    }

    public final boolean isLoadedPos(int i) {
        return pos.isLoaded(i);
    }

    public final int lengthPos(int i) {
        return pos.length(i);
    }

    public final void registerVectorPos(int wn, int[] reg) {
        pos.registerVector(wn, reg);
    }

    public final void releaseVectorPos(int n) {
        pos.releaseVector(n);
    }

    public final long getCurrentSize() {
        return doc.getCurrentSize() + pos.getCurrentSize();
    }
}
