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

import java.io.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 *  Comportements d'un vecteur de byte[fixedArraySize] charg� enti�rement en m�moire.
 * <p>
 * 
 *
 */
public class ByteArrayVector_InMemory implements ByteArrayVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "ByteArrayVector_InMemory 2.1";
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
    private int maxUsedlength = 0;
    private readWriteMode RW = readWriteMode.rw;

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public ByteArrayVector_InMemory() {
    }

    /**  cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path */
    public final ByteArrayVector create(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        return (new ByteArrayVector_InMemory(_pathName, _fileName, _maxSize, _fixedArraySize));
    }

    /**  ouvre un vecteur  � l'endroit indiqu� par le _path */
    public final ByteArrayVector open(String _pathName, String _fileName, readWriteMode _RW) {
        return (new ByteArrayVector_InMemory(_pathName, _fileName, _RW));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /** cr�er une nouvelle instance de WordTable � partir des donn�es existantes*/
    private ByteArrayVector_InMemory(String _pathName, String _fileName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        RW = _RW;
        loadMasterFile();
        //printMasterFile();
    }

    /** cr�er une nouvelle instance de WordTable*/
    private ByteArrayVector_InMemory(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        createByteArrayVector_InMemory(_pathName, _fileName, _maxSize, _fixedArraySize);
    }

    private final void createByteArrayVector_InMemory(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        pathName = _pathName;
        fileName = _fileName;
        fixedArraySize = _fixedArraySize;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la premi�re fois, � la cr�ation
        v = new byte[size][];
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        if (RW == readWriteMode.rw) {
            try {
                FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
                ObjectOutputStream p = new ObjectOutputStream(ostream);
                p.writeObject(VERSION); // �crire les flags
                p.writeInt(size);
                p.writeInt(fixedArraySize);
                p.writeInt(maxUsedlength);
                p.writeObject(v);
                System.out.println("save Byte Vector: " + pathName + "/" + fileName);
                p.flush();
                ostream.close();
            } catch (IOException e) {
                error("IO error in ByteArrayVector_InMemory.saveMasterFile", e);
            }
        } else {
            msg("UnSave Byte Vector: " + pathName + "/" + fileName);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            fixedArraySize = p.readInt();
            maxUsedlength = p.readInt();
            v = (byte[][]) p.readObject();
            System.out.println("load Byte Arrays Vector: " + pathName + "/" + fileName);
            printMasterFile();
            istream.close();
        } catch (Exception e) {
            error("IO error file ByteArrayVector_InMemory.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Byte Array Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
        msg("fixedArraySize: " + fixedArraySize);
        msg("maxUsedlength: " + maxUsedlength);
    }

    /** mets � jour la position pos avec la valeur val */
    public final void set(int pos, byte[] val) {
        // pas de test sur le mode RW, pour acc�l�rer
        if (val.length <= fixedArraySize) {
            if (val.length > maxUsedlength) {
                maxUsedlength = val.length;
            }
            //msg ("set pos:"+pos);
            v[pos] = val;
        } else {
            error("int array is too big length:" + val.length + " limit:" + fixedArraySize);
        }
    }

    /**  cherche la valeur � la position pos  */
    public final byte[] get(int pos) {
        return v[pos];
    }

    /**  cherche la valeur � la position pos, la i�me valeur   */
    public final byte get(int pos, int i) {
        return v[pos][i];
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  retourne la taille du vecteur � la position pos*/
    public final int length(int pos) {
        if (v[pos] != null) {
            return v[pos].length;
        }
        return 0;
    }

    /**  retourne la taille maximum des vecteurs stock�*/
    public final int maxUsedlength() {
        return maxUsedlength;
    }

    private final int countNotEmpty() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (v[i] != null) {
                count++;
            }
        }
        return count;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg("Byte Array Vector statistics -> " + pathName + "/" + fileName);
        msg(" size: " + size);
        msg(" not Empty: " + countNotEmpty());
        msg("fixedArraySize: " + fixedArraySize);
        msg("maxUsedlength: " + maxUsedlength);
    }

    public final void clear(int pos) {
        v[pos] = null;
    }
}
