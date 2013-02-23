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
import static org.olanto.idxvli.IdxConstant.*;

/**
 * Comportements d'un tableau de bit[2^maxSize][fixedArraySize] Zipp�.
 * 
 *
 */
public class BitArrayVector_ZIP implements BitArrayVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "BitArrayVector_ZIP 2.1";
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
    private ByteArrayVector vZip;
    private readWriteMode RW = readWriteMode.rw;

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public BitArrayVector_ZIP() {
    }

    /**  cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path */
    public final BitArrayVector create(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        return (new BitArrayVector_ZIP(_ManagerImplementation, _pathName, _fileName, _maxSize, _fixedArraySize));
    }

    /**  ouvre un vecteur  � l'endroit indiqu� par le _path */
    public final BitArrayVector open(implementationMode _ManagerImplementation, String _pathName, String _fileName, readWriteMode _RW) {
        return (new BitArrayVector_ZIP(_ManagerImplementation, _pathName, _fileName, _RW));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        vZip.close();
        msg("--- vector is closed now:" + fileName);
    }

    /** cr�er une nouvelle instance de WordTable � partir des donn�es existantes*/
    private BitArrayVector_ZIP(implementationMode _ManagerImplementation, String _pathName, String _fileName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        RW = _RW;
        loadMasterFile();
        switch (_ManagerImplementation) {
            case FAST:
                vZip = new ByteArrayVector_InMemory().open(_pathName, _fileName + "_zip", RW);
                break;
            case BIG:
                vZip = new ByteArrayVector_OnDisk().open(_pathName, _fileName + "_zip", RW);
                break;
        }
        //printMasterFile();
    }

    /** cr�er une nouvelle instance de WordTable*/
    private BitArrayVector_ZIP(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        createBitArrayVector_ZIP(_ManagerImplementation, _pathName, _fileName, _maxSize, _fixedArraySize);
    }

    private final void createBitArrayVector_ZIP(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        pathName = _pathName;
        fileName = _fileName;
        fixedArraySize = _fixedArraySize;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        switch (_ManagerImplementation) {
            case FAST:
                vZip = new ByteArrayVector_InMemory().create(pathName, fileName + "_zip", _maxSize, fixedArraySize);
                break;
            case BIG:
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
                msg("save Bit Vector: " + pathName + "/" + fileName);
                p.flush();
                ostream.close();
            } catch (IOException e) {
                error("IO error in BitArrayVector_ZIP.saveMasterFile", e);
            }
        } else {
            msg("UnSave Bit Vector: " + pathName + "/" + fileName);
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
            error("IO error file BitArrayVector_ZIP.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Bit Array Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
        msg("fixedArraySize: " + fixedArraySize);
    }

    /** mets � jour la position pos avec la valeur val, la i�me valeur  */
    public final void set(int pos, int i, boolean val) {
        if (RW == readWriteMode.rw) {
            SetOfBits wBA = new SetOfBits(vZip.get(pos), fixedArraySize);  // cr�e un vecteur depuis son zip
            wBA.set(i, val);
            vZip.set(pos, wBA.getZip());
        } else {
            error("BitArrayVector_ZIP.set is not allowed, mode must be in rw");
        }
    }

    /** mets � jour la position pos avec le vecteur complet*/
    public final void set(int pos, SetOfBits v) {
        vZip.set(pos, v.getZip());
    }

    /**  cherche la valeur � la position pos, la i�me valeur   */
    public final boolean get(int pos, int i) {
        SetOfBits wBA = new SetOfBits(vZip.get(pos), fixedArraySize);  // cr�e un vecteur depuis son zip
        return wBA.get(i);
    }

    /**  cherche le vecteur complet � la position pos  */
    public final SetOfBits get(int pos) {
        return new SetOfBits(vZip.get(pos), fixedArraySize);
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  retourne la taille du vecteur � la position pos*/
    public final int length(int pos) {
        return fixedArraySize;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "Bit Array Vector statistics -> " + pathName + "/" + fileName
                + "\n  size: " + size
                + "\n  fixedArraySize: " + fixedArraySize
                + "\n  Only for BIG, hopeCompression: " + HOPE_COMPRESSION + ", maxUsedLength: " + vZip.maxUsedlength()
                + "\n  actualCompression: " + (fixedArraySize / vZip.maxUsedlength());
    }
}
