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

package org.olanto.idxvli.util;

import java.util.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxConstant.*;

/**
 * gestionaire de mots avec un niveau de cache.
 * <p>
 * 
 *
 */
public class StringTable_OnDisk_WithCache_MapIO implements StringRepository {

    private StringRepository onDisk;
    private int count = 0;
    private int get = 0;
    private int getInCache = 0;
    private int countRefresh = 0;
    private int maxInCache = WORD_CACHE_COUNT;
    private Hashtable<String, Integer> InMemory;

    public StringTable_OnDisk_WithCache_MapIO() {
    }

    /**  crï¿½e une word table de la taille 2^_maxSize par dï¿½faut ï¿½ l'endroit indiquï¿½ par le path, (maximum=2^31),
     * avec des string de longueur max _lengthString*/
    public final StringRepository create(String _path, String _name, int _maxSize, int _lengthString) {
        return (new StringTable_OnDisk_WithCache_MapIO(_path, _name, _maxSize, _lengthString));
    }

    private StringTable_OnDisk_WithCache_MapIO(String _path, String _name, int _maxSize, int _lengthString) {
        initCache();
        onDisk = new StringTable_HomeHash_OnDisk_DIO_Clue().create(_path, _name, _maxSize, _lengthString);
    }

    /**  ouvre un gestionnaire de mots  ï¿½ l'endroit indiquï¿½ par le path */
    public final StringRepository open(String _path, String _name) {
        return (new StringTable_OnDisk_WithCache_MapIO(_path, _name));
    }

    private StringTable_OnDisk_WithCache_MapIO(String _path, String _name) {
        initCache();
        onDisk = (new StringTable_HomeHash_OnDisk_DIO_Clue()).open(_path, _name);
    }

    private final void initCache() {
        count = 0;
        InMemory = new Hashtable<String, Integer>(2 * maxInCache);
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        onDisk.close();
    }

    /**  ajoute un terme au gestionnaire retourne le numï¿½ro du terme, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe dï¿½ja
     */
    public final int put(String w) {
        return onDisk.put(w);
    }

    /**  cherche le numï¿½ro du terme, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    synchronized public final int get(String w) { // rafraichir tout le cache
        //msg("get:"+w);
        get++;
        if (count > maxInCache) {
            initCache();
            countRefresh++;
        }
        Integer n = InMemory.get(w);
        if (n != null) {  // dans le cache
            getInCache++;
            return n;
        } else {
            int fromDisk = onDisk.get(w);
            if (fromDisk == EMPTY) {
                return EMPTY;
            } // cas on a pas trouvï¿½
            InMemory.put(w, fromDisk);
            count++;
            return fromDisk;
        }

    }

    /**  cherche le terme associï¿½ ï¿½ un numï¿½ro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        return onDisk.get(i);
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        return onDisk.getCount();
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "String Table with cache statistics -> "
                + "\n  get: " + get + " getInCache: " + getInCache + " countRefresh: " + countRefresh + " maxInCache: " + maxInCache
                + "  use this object:"
                + onDisk.getStatistic();
    }

    public final void modify(int i, String newValue) {
        error("not implemented");
    }
}
