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

package org.olanto.idxvli.util.testssd2;

import org.olanto.idxvli.util.*;
import java.io.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.util.BytesAndFiles.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 *  Comportements d'un vecteur de byte[fixedArraySize] g�r� sur disque.
 * 

 */
public class ByteArrayVector_OnDisk implements ByteArrayVector {

    static final int minInit = 128;  // nbr de blocs pour l'initialisation
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */
    static final String SOFT_VERSION = "ByteArrayVector_InMemory 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String pathName;
    /** definit les fichiers d�pendant de cet ObjectStore */
    private String fileName;
    private byte[][] v;
    private int size = 0;
    private int fixedArraySize;
    private int fixedDiskSize;
    private int maxUsedlength = 0;
    private RandomAccessFile rdoc;
    private readWriteMode RW = readWriteMode.rw;

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public ByteArrayVector_OnDisk() {
    }

    /**  cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path */
    public final ByteArrayVector create(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        return (new ByteArrayVector_OnDisk(_pathName, _fileName, _maxSize, _fixedArraySize));
    }

    /**  ouvre un vecteur  � l'endroit indiqu� par le _path */
    public final ByteArrayVector open(String _pathName, String _fileName, readWriteMode _RW) {
        return (new ByteArrayVector_OnDisk(_pathName, _fileName, _RW));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        try {
            rdoc.close();
        } catch (Exception e) {
            error("IO error in close()", e);
        }
        msg("--- vector is closed now:" + fileName);
    }

    /** cr�er une nouvelle instance de WordTable � partir des donn�es existantes*/
    private ByteArrayVector_OnDisk(String _pathName, String _fileName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        RW = _RW;
        loadMasterFile();
        //printMasterFile();
    }

    /** cr�er une nouvelle instance de WordTable*/
    private ByteArrayVector_OnDisk(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        createByteArrayVector_OnDisk(_pathName, _fileName, _maxSize, _fixedArraySize);
    }

    private final void createByteArrayVector_OnDisk(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        pathName = _pathName;
        fileName = _fileName;
        fixedArraySize = _fixedArraySize;
        fixedDiskSize = fixedArraySize + 4;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        try {
            rdoc = new RandomAccessFile(pathName + "/" + fileName + ".rnd", RW.name());
            initFirstTime();
            saveMasterFile();
        } catch (Exception e) {
            error("IO error in createByteArrayVector_OnDisk()", e);
        }
    }

    private final void initFirstTime() { // n'utiliser que la premi�re fois, � la cr�ation
        // alloue la totalit� a l'initialisation
        byte[] b = new byte[minInit * fixedDiskSize];
        for (int i = 0; i < size / minInit; i++) {
            writeBytes(b, (long) i * (long) b.length, rdoc); // marque vide
        }
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            if (RW == readWriteMode.rw) {
                FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
                ObjectOutputStream p = new ObjectOutputStream(ostream);
                p.writeObject(VERSION); // �crire les flags
                p.writeInt(size);
                p.writeInt(fixedArraySize);
                p.writeInt(fixedDiskSize);
                p.writeInt(maxUsedlength);
                System.out.println("save Int Vector: " + pathName + "/" + fileName);
                p.flush();
                ostream.close();
            } else {
                msg("UnSave Byte Vector: " + pathName + "/" + fileName);
            }
        } catch (IOException e) {
            error("IO error in ByteArrayVector_OnDisk.saveMasterFile", e);
        }


    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            fixedArraySize = p.readInt();
            fixedDiskSize = p.readInt();
            maxUsedlength = p.readInt();
            System.out.println("load Long Vector: " + pathName + "/" + fileName);
            istream.close();
            rdoc = new RandomAccessFile(pathName + "/" + fileName + ".rnd", RW.name());
        } catch (Exception e) {
            error("IO error file IntVector_InMemory.loadMasterFile", e);
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
            long posOnDisk = (long) pos * (long) fixedDiskSize;
            writeInt(val.length, posOnDisk, rdoc);
            writeBytes(val, posOnDisk + 4, rdoc);
        } else {
            error("int array is too big length:" + val.length + " limit:" + fixedArraySize);
        }
    }

    /**  cherche la valeur � la position pos  */
    public final byte[] get(int pos) {
        long posOnDisk = (long) pos * (long) (fixedDiskSize);
        int length = readInt(posOnDisk, rdoc);
        return readBytes(32, posOnDisk + 4, rdoc);
    }

    /**  cherche la valeur � la position pos, la i�me valeur   */
    public final byte get(int pos, int i) {
        byte[] b = get(pos);
        if (b == null) {
            error("get for " + pos + " is null in " + pathName + "/" + fileName);
            return 0;
        }
        if (i >= b.length) {
            error("get for " + pos + "," + i + " is too big in" + pathName + "/" + fileName);
            return 0;
        }
        return b[i];
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  retourne la taille maximum des vecteurs stock�*/
    public final int maxUsedlength() {
        return maxUsedlength;
    }

    /**  retourne la taille du vecteur � la position pos*/
    public final int length(int pos) {
        // peut devenir une methode de l'interface
        long posOnDisk = (long) pos * (long) (fixedDiskSize);
        return readInt(posOnDisk, rdoc);
    }

    private final int countNotEmpty() {
        // ceci peut �tre couteux � lire sur le disque !!!!!!!
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (length(i) != 0) {
                count++;
            }
        }
        return count;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg("ByteArrayVector " + pathName + "/" + fileName + "statistics -> ");
        msg(" size: " + size);
        //msg(" not Empty: " + countNotEmpty()); // ceci peut �tre couteux � lire sur le disque !!!!!!!
        msg(" not Empty: not activated"); // ceci peut �tre couteux � lire sur le disque !!!!!!!
        msg("fixedArraySize: " + fixedArraySize);
        msg("maxUsedlength: " + maxUsedlength);
    }

    public final void clear(int pos) {
        byte[] b = new byte[fixedDiskSize];  // tout � 0
        writeBytes(b, (long) pos * (long) b.length, rdoc); // marque vide
    }
}
