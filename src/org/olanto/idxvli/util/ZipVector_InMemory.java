/**
 * ********
 * Copyright © 2010-2012 Olanto Foundation Geneva
 *
 * This file is part of myCAT.
 *
 * myCAT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.idxvli.util;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.olanto.idxvli.IdxEnum.readWriteMode;
import static org.olanto.util.Messages.*;

/**
 * Comportements d'un vecteur de byte[fixedArraySize] charg� enti�rement en
 * m�moire. <p>
 *
 *
 */
public class ZipVector_InMemory implements ZipVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final boolean OBJ_COMPRESSION = true; //sinon pas de compression
    static final String CONTENT_ENCODING = "UTF-8"; //toujours des String
    static final String SOFT_VERSION = "ByteArrayVector_InMemory 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /**
     * definit la version
     */
    private String VERSION;
    /**
     * definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore
     */
    private String pathName;
    /**
     * definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore
     */
    private String fileName;
    private byte[][] v;
    private int[] zipSize;
    private int size = 0;
    private readWriteMode RW = readWriteMode.rw;

    /**
     * cr�er une nouvelle instance de repository pour effectuer les create, open
     */
    public ZipVector_InMemory() {
    }

    /**
     * cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path
     */
    @Override
    public final ZipVector create(String _pathName, String _fileName, int _maxSize) {
        return (new ZipVector_InMemory(_pathName, _fileName, _maxSize));
    }

    /**
     * ouvre un vecteur � l'endroit indiqu� par le _path
     */
    @Override
    public final ZipVector open(String _pathName, String _fileName, readWriteMode _RW) {
        return (new ZipVector_InMemory(_pathName, _fileName, _RW));
    }

    /**
     * ferme un vecteur (et sauve les modifications
     */
    @Override
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /**
     * créer une nouvelle instance de WordTable à partir des données existantes
     */
    private ZipVector_InMemory(String _pathName, String _fileName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        RW = _RW;
        loadMasterFile();
        //printMasterFile();
    }

    /**
     * cr�er une nouvelle instance de WordTable
     */
    private ZipVector_InMemory(String _pathName, String _fileName, int _maxSize) {
        createZipVector_InMemory(_pathName, _fileName, _maxSize);
    }

    private void createZipVector_InMemory(String _pathName, String _fileName, int _maxSize) {
        pathName = _pathName;
        fileName = _fileName;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private void initFirstTime() { // n'utiliser que la première fois, à la création
        v = new byte[size][];
        zipSize = new int[size];
    }

    private void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        if (RW == readWriteMode.rw) {
            try {
                FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
                ObjectOutputStream p = new ObjectOutputStream(ostream);
                p.writeObject(VERSION); // écrire les flags
                p.writeInt(size);
                p.writeObject(v);
                p.writeObject(zipSize);
                System.out.println("save Zip Vector: " + pathName + "/" + fileName);
                p.flush();
                ostream.close();
            } catch (IOException e) {
                error("IO error in ZipVector_InMemory.saveMasterFile", e);
            }
        } else {
            msg("UnSave Zip Vector: " + pathName + "/" + fileName);
        }
    }

    private void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            v = (byte[][]) p.readObject();
            zipSize = (int[]) p.readObject();
            System.out.println("load Zip Vector: " + pathName + "/" + fileName);
            printMasterFile();
            istream.close();
        } catch (Exception e) {
            error("IO error file ZipVector_InMemory.loadMasterFile", e);
        }
    }

    private void printMasterFile() {
        msg("--- <Zip Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
    }

    /**
     * mets à jour la position pos avec la valeur val
     */
    @Override
    public final void set(int pos, String txt) {
        try {
            byte[] b = txt.getBytes(CONTENT_ENCODING);
            v[pos] = compress(b);
            zipSize[pos] = b.length;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ZipVector_InMemory.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("ZipManager.set: pos= " + pos + ", v[pos].length=" + v[pos].length + ", zipSize[pos]=" + zipSize[pos]);
    }
   /** mets à  jour la position pos avec le fichier filename */
    @Override
    public void set(int pos, String filename, String encoding){
        set(pos, BytesAndFiles.file2String(filename,encoding));
    }

    /**
     * cherche la valeur à la position pos
     */
    @Override
    public final String get(int pos) {
        if (zipSize[pos] == 0) {
            return null;
        }
        String res;
        try {
            res = new String(decompress(zipSize[pos], v[pos]), CONTENT_ENCODING);
            return res;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ZipVector_InMemory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * retourne la taille du vecteur
     */
    @Override
    public final int length() {
        return size;
    }

    /**
     * retourne la taille du vecteur à la position pos
     */
    @Override
    public final int lengthZip(int pos) {
        if (v[pos] != null) {
            return v[pos].length;
        }
        return 0;
    }

    /**
     * retourne la taille du vecteur décompressé à  la position pos
     */
    @Override
    public int lengthString(int pos) {
        return zipSize[pos];
    }

    ;


    private int countNotEmpty() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (zipSize[i] != 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * retourne la taille total des vecteurs stockés
     */
    @Override
    public long totalLengthZip() {
        long count = 0;
        for (int i = 0; i < size; i++) {
            if (v[i] != null) {
                count += v[i].length;
            }
        }
        return count;
    }

    ;
   /**  retourne la taille total des vecteurs stockés*/
    @Override
    public long totalLengthString() {
        long count = 0;
        for (int i = 0; i < size; i++) {
            count += zipSize[i];
        }
        return count;
    }

    ;

    
    /**
     * imprime des statistiques
     */
    @Override
    public final void printStatistic() {
        msg("Byte Array Vector statistics -> " + pathName + "/" + fileName);
        msg(" size: " + size);
        msg(" not Empty: " + countNotEmpty());
        msg("totalLengthZip: " + totalLengthZip());
        msg("totalLengthString: " + totalLengthString());
    }

    @Override
    public final void clear(int pos) {
        v[pos] = null;
        zipSize[pos] = 0;
    }

    public static byte[] decompress(int realSize, byte[] bb) {
        if (OBJ_COMPRESSION == true) {
            return BytesAndFiles.decompress(realSize, bb);
        } else {
            return bb;
        }
    }

    public static byte[] compress(byte[] bb) {
        if (OBJ_COMPRESSION == true) {
            return BytesAndFiles.compress(bb);
        } else {
            return bb;
        }
    }
}
