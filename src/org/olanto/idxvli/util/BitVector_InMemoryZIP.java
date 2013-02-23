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

import java.util.zip.*;

import java.io.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.util.BytesAndFiles.*;

/**
 *  Comportements d'un vecteur de bit charg� enti�rement en m�moire et zipp�.
 * <p>
 * 
 *<p>
 *
 */
public class BitVector_InMemoryZIP implements BitVector {
    /* constantes d'un gestionnaire  -------------------------------------- */

    static final String SOFT_VERSION = "BitVector_InMemoryZIP 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String pathName;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String fileName;
    private int[] b;
    private int size = 0;
    private static final int[] mask = {1, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6, 1 << 7, 1 << 8, 1 << 9,
        1 << 10, 1 << 11, 1 << 12, 1 << 13, 1 << 14, 1 << 15, 1 << 16, 1 << 17, 1 << 18, 1 << 19,
        1 << 20, 1 << 21, 1 << 22, 1 << 23, 1 << 24, 1 << 25, 1 << 26, 1 << 27, 1 << 28, 1 << 29,
        1 << 30, 1 << 31
    };
    private static final boolean nowrapboolean = true;  // no wrap pour le ZIP
    private static Deflater def = new Deflater(9, nowrapboolean);
    private static Inflater inf = new Inflater(nowrapboolean);

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public BitVector_InMemoryZIP() {
    }

    /**  cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path */
    public final BitVector create(String _pathName, String _fileName, int _maxSize) {
        return (new BitVector_InMemoryZIP(_pathName, _fileName, _maxSize));
    }

    /**  ouvre un vecteur  � l'endroit indiqu� par le _path */
    public final BitVector open(String _pathName, String _fileName) {
        return (new BitVector_InMemoryZIP(_pathName, _fileName));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /** cr�er une nouvelle instance de WordTable � partir des donn�es existantes*/
    private BitVector_InMemoryZIP(String _pathName, String _fileName) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        loadMasterFile();
        //printMasterFile();
    }

    /** cr�er une nouvelle instance de WordTable*/
    private BitVector_InMemoryZIP(String _pathName, String _fileName, int _maxSize) {
        createBitVector_InMemoryZIP(_pathName, _fileName, _maxSize);
    }

    private final void createBitVector_InMemoryZIP(String _pathName, String _fileName, int _maxSize) {
        pathName = _pathName;
        fileName = _fileName;
        size = _maxSize;
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la premi�re fois, � la cr�ation
        if ((size % 32) != 0) {
            error_fatal("BitVector_InMemory size must be a multiple of 32");
        }
        b = new int[size / 32];
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // �crire les flags
            p.writeInt(size);
            byte[] bb = compress(b);
            // showVector(bb);
            p.writeObject(bb);
            System.out.println("save Bit Vector: " + pathName + "/" + fileName);
            p.flush();
            ostream.close();
        } catch (IOException e) {
            error("IO error in BitVector_InMemoryz-.saveMasterFile", e);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            byte[] bb = (byte[]) p.readObject();
            // showVector(bb);
            b = decompress(bb, size / 32 + 1);
            msg("load Bit Vector: " + pathName + "/" + fileName);
            istream.close();
        } catch (Exception e) {
            error("IO error file BitVector_InMemorZIP.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Bit Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
    }

    /** mets � jour la position pos avec la valeur val */
    public final void set(int pos, boolean val) {
        if (val) {
            b[pos / 32] |= mask[pos % 32];
        } // set bit
        else {
            b[pos / 32] &= ~mask[pos % 32];
        }
    }

    /**  cherche la valeur � la position pos  */
    public final boolean get(int pos) {
        return (b[pos >> 5] & mask[pos % 32]) == mask[pos % 32]; // >>5 divise par 32
    }

    /**  cherche le vecteur entier  */
    public final SetOfBits get() {
        return new SetOfBits(b);
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "BitVector_InMemoryZIP: " + pathName + "/" + fileName + "statistics -> "
                + "\n  size: " + size;
    }
}
