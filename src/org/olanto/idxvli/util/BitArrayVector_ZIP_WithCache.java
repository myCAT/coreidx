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
import java.io.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;
import java.util.concurrent.locks.*;

/**
 * Comportements d'un tableau de bit[2^maxSize][fixedArraySize] Zipp� avec un cache.
 *
 * 
 *
 * <pre>
 *  concurrence
 *  get est synchronis� pour les lecteurs sur le cache
 *  les autres doivent �tre prot�g� par un �crivain (externe)
 *  </pre>
 * 
 * modification JG: ajout de la méthode getCopyFromCache
 */
public class BitArrayVector_ZIP_WithCache implements BitArrayVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "BitArrayVector_ZIP_WithCache 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String pathName;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String fileName;
    private byte[][] v;
    private int size = 0;
    private int fixedArraySize;
    ByteArrayVector vZip;
    private HashMap<Integer, SetOfBits> inMemory;
    private int countInCache = 0;
    private int countRefresh = 0;
    private int maxInCache = 16;
    private readWriteMode RW = readWriteMode.rw;
    private int countOp = 0;
    private int countOpInCache = 0;

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public BitArrayVector_ZIP_WithCache() {
    }

    /**  cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path */
    public final BitArrayVector create(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        return (new BitArrayVector_ZIP_WithCache(_ManagerImplementation, _pathName, _fileName, _maxSize, _fixedArraySize));
    }

    /**  ouvre un vecteur  � l'endroit indiqu� par le _path */
    public final BitArrayVector open(implementationMode _ManagerImplementation, String _pathName, String _fileName, readWriteMode _RW) {
        return (new BitArrayVector_ZIP_WithCache(_ManagerImplementation, _pathName, _fileName, _RW));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        if (RW == readWriteMode.rw) {
            saveCache();
        }
        saveMasterFile();
        vZip.close();
        msg("--- vector is closed now:" + fileName);
    }

    /** cr�er une nouvelle instance du gestionnaire � partir des donn�es existantes*/
    private BitArrayVector_ZIP_WithCache(implementationMode _ManagerImplementation, String _pathName, String _fileName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        RW = _RW;
        loadMasterFile();
        switch (_ManagerImplementation) {
            case DIRECT:
            case FAST:
                vZip = new ByteArrayVector_InMemory().open(_pathName, _fileName + "_zip", RW);
                break;
            case BIG:
                vZip = new ByteArrayVector_OnDisk().open(_pathName, _fileName + "_zip", RW);
                break;
            case XL:
            case XXL:
                vZip = new ByteArrayVector_OnDisk().open(_pathName, _fileName + "_zip", RW);
                break;
        }
        initCache();
        printMasterFile();
    }

    /** cr�er une nouvelle instance du gestionnaire*/
    private BitArrayVector_ZIP_WithCache(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        initCache();
        createBitArrayVector_ZIP_WithCache(_ManagerImplementation, _pathName, _fileName, _maxSize, _fixedArraySize);
    }

    private final void initCache() {
        countInCache = 0;
        maxInCache = MAX_IN_ZIP_CACHE;
        inMemory = new HashMap<Integer, SetOfBits>(2 * maxInCache);
    }

    private final void saveCache() {
        for (Integer pos : inMemory.keySet()) {
            vZip.set(pos.intValue(), (inMemory.get(pos)).getZip());
        }
    }

    private final void createBitArrayVector_ZIP_WithCache(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        pathName = _pathName;
        fileName = _fileName;
        fixedArraySize = _fixedArraySize;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        switch (_ManagerImplementation) {
            case DIRECT:
            case FAST:
                vZip = new ByteArrayVector_InMemory().create(pathName, fileName + "_zip", _maxSize, fixedArraySize);
                break;
            case BIG:
                vZip = new ByteArrayVector_OnDisk().create(pathName, fileName + "_zip", _maxSize, fixedArraySize / HOPE_COMPRESSION);
                break;
            case XL:
            case XXL:
                vZip = new ByteArrayVector_OnDisk().create(pathName, fileName + "_zip", _maxSize, fixedArraySize / HOPE_COMPRESSION);
                break;
        }
        initFirstTime();
        saveMasterFile();
        vZip.close();
    }

    private final void initFirstTime() { // n'utiliser que la premi�re fois, � la cr�ation
        SetOfBits wBA = new SetOfBits(fixedArraySize);  // cr�e un vecteur vide
        byte[] empty = wBA.getZip();
        //msg("empty.length:"+empty.length);
        for (int i = 0; i < size; i++) {
            vZip.set(i, empty);
        } //initialise tous les vecteurs � vide
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        if (RW == readWriteMode.rw) {
            try {
                FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
                ObjectOutputStream p = new ObjectOutputStream(ostream);
                p.writeObject(VERSION); // �crire les flags
                p.writeInt(size);
                p.writeInt(fixedArraySize);
                System.out.println("save Int Vector: " + pathName + "/" + fileName);
                p.flush();
                ostream.close();
            } catch (IOException e) {
                error("IO error in BitArrayVector_ZIP_WithCache.saveMasterFile", e);
            }
        } else {
            msg("UnSave Bit Array: " + pathName + "/" + fileName);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            fixedArraySize = p.readInt();
            System.out.println("load Long Vector: " + pathName + "/" + fileName);
            istream.close();
        } catch (Exception e) {
            error("IO error file BitArrayVector_ZIP_WithCache.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Bit Array Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
        msg("fixedArraySize: " + fixedArraySize);
    }
    /** opération sur cache verrous ------------------------------------------*/
    private final ReentrantReadWriteLock cacheRW = new ReentrantReadWriteLock();
    private final Lock cacheR = cacheRW.readLock();
    private final Lock cacheW = cacheRW.writeLock();

    private final SetOfBits getFromCache(int pos) {
        //msg("============================get from cache:"+pos);
        if (inMemory.get(pos) == null) {  // pas dans le cache
            //msg("============================charge dans le cache");
            if (countInCache >= maxInCache) { // cache est plein
                msg("flush the properties cache, countOpInCache:"+countOpInCache+ " countRefresh:"+countRefresh);
                if (RW == readWriteMode.rw) {
                    saveCache();
                } // sauver le cache, si on est en mode de mise � jour
                initCache(); // remet � z�ro le cache
                countRefresh++;
            }
            SetOfBits sob = new SetOfBits(vZip.get(pos), fixedArraySize);
            inMemory.put(pos, sob);  // cr�e un vecteur depuis son zip
        } else {
            countOpInCache++;
        }

        // dans le cache ici
        return inMemory.get(pos); // retourne  l'objet !!!


    }

    private final SetOfBits getCopyFromCache(int pos) {
        return new SetOfBits(getFromCache(pos));   // retourne une copie de l'objet !!!
    }

    /** mets à jour la position pos avec la valeur val, la iéme valeur  */
    public final void set(int pos, int i, boolean val) {
        //msg("============================set from cache:"+pos+","+i+","+val);
        cacheW.lock();
        try {
            countOp++;
            getFromCache(pos).set(i, val);
        } finally {
            cacheW.unlock();
        }
    }

    /** mets � jour la position pos avec le vecteur complet*/
    public final void set(int pos, SetOfBits v) {
        //msg("============================set from cache:"+pos+", set");
        cacheW.lock();
        try {
            // msg("set propertie Zip:"+pos);
            //for (int i=0;i<200;i++){if(v.get(i))System.out.print(1); else System.out.print(0);}System.out.println();
            inMemory.remove(pos);
            inMemory.put(pos, new SetOfBits(v.getIntStructure()));  // cr�e un vecteur depuis son SOB
            //for (int i=0;i<200;i++){if(get(pos,i))System.out.print(1); else System.out.print(0);}System.out.println();
        } finally {
            cacheW.unlock();
        }
    }

    /**  cherche la valeur � la position pos, la i�me valeur   */
    public final boolean get(int pos, int i) {
        cacheR.lock();
        try {
            countOp++;
            return getFromCache(pos).get(i);
        } finally {
            cacheR.unlock();
        }
    }

    /**  cherche le vecteur complet � la position pos  */
    public final SetOfBits get(int pos) {
        cacheR.lock();
        try {
            return getFromCache(pos);
        } finally {
            cacheR.unlock();
        }
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  retourne la taille du vecteur � la position pos*/
    public final int length(int pos) {
        cacheR.lock();
        try {
            return fixedArraySize;
        } finally {
            cacheR.unlock();
        }
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "Bit Array Vector with cache statistics -> " + pathName + "/" + fileName
                + "\n  size: " + size
                + "\n  fixedArraySize: " + fixedArraySize
                + "\n  Only for BIG, hopeCompression: " + HOPE_COMPRESSION + ", maxUsedLength: " + vZip.maxUsedlength()
                + "\n  actualCompression: " + (fixedArraySize / vZip.maxUsedlength())
                + "\n  ops: " + countOp + " ops InCache: " + countOpInCache + " countRefresh: " + countRefresh + " maxInCache: " + maxInCache;
    }
}
