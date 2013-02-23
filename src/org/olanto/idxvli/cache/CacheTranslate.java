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
 * Gestionnaire des translations entre les WordId et les CacheID.
 * 
 */
public interface CacheTranslate {

    /**
     * enregistre un nouveau WordId, retourne le CacheId
     * @param wordId word
     * @return CacheId
     */
    public int registerCacheId(int wordId);

    /**
     * retourne le CacheId associÃ© Ã  un WordID
     * @param wordId word
     * @return CacheId
     */
    public int getCacheId(int wordId);

    /**
     * retourne le retourne WordID associÃ© Ã  un CacheId
     * @param CacheId interne au cache
     * @return WordID
     */
    public int getWordId(int CacheId);

    /** efface toutes les associations */
    public void resetCache();

    /**
     * retourne la capacitÃ© du gestionnaire
     * @return la capacitÃ© du gestionnaire
     */
    public int capacity();

    /**
     * retourne le nombre d'association enregistrÃ©es
     * @return nombre d'association
     */
    public int allocate();
}
