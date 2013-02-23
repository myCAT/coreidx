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

/**
 *  Comportements d'un vecteur de int[fixedArraySize] charg� en m�moire.
 *
 * 
 *
 */
public class IntArrayVector_InMemory implements IntArrayVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "IntArrayVector_InMemory 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String pathName;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String fileName;
    private int[][] v;
    private int size = 0;
    private int fixedArraySize;

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public IntArrayVector_InMemory() {
    }

    /**  cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path */
    public final IntArrayVector create(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        return (new IntArrayVector_InMemory(_pathName, _fileName, _maxSize, _fixedArraySize));
    }

    /**  ouvre un vecteur  � l'endroit indiqu� par le _path */
    public final IntArrayVector open(String _pathName, String _fileName) {
        return (new IntArrayVector_InMemory(_pathName, _fileName));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /** cr�er une nouvelle instance de WordTable � partir des donn�es existantes*/
    private IntArrayVector_InMemory(String _pathName, String _fileName) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        loadMasterFile();
        //printMasterFile();
    }

    /** cr�er une nouvelle instance de WordTable*/
    private IntArrayVector_InMemory(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        createIntVector_InMemory(_pathName, _fileName, _maxSize, _fixedArraySize);
    }

    private final void createIntVector_InMemory(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        pathName = _pathName;
        fileName = _fileName;
        fixedArraySize = _fixedArraySize;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la premi�re fois, � la cr�ation
        v = new int[size][];
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // �crire les flags
            p.writeInt(size);
            p.writeInt(fixedArraySize);
            p.writeObject(v);
            System.out.println("save Int Vector: " + pathName + "/" + fileName);
            p.flush();
            ostream.close();
        } catch (IOException e) {
            error("IO error in IntVector_InMemory.saveMasterFile", e);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            fixedArraySize = p.readInt();
            v = (int[][]) p.readObject();
            System.out.println("load Long Vector: " + pathName + "/" + fileName);
            istream.close();
        } catch (Exception e) {
            error("IO error file IntVector_InMemory.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Int Array Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
        msg("fixedArraySize: " + fixedArraySize);
    }

    /** mets � jour la position pos avec la valeur val */
    public final void set(int pos, int[] val) {
        if (val.length <= fixedArraySize) {
            v[pos] = val;
        } else {
            error("int array is too big length:" + val.length + " limit:" + fixedArraySize);
        }
    }

    /** mets � jour la position pos avec la valeur val */
    public void set(int pos, int i, int val) {
        if (i < fixedArraySize) {
            if (v[pos] == null) { // pas encore allou�s
                v[pos] = new int[fixedArraySize];
            }
            v[pos][i] = val;
        } else {
            error("int array is too big length:" + i + " limit:" + fixedArraySize);
        }

    }

    /**  cherche la valeur � la position pos  */
    public final int[] get(int pos) {
        return v[pos];
    }

    /**  cherche la valeur � la position pos, la i�me valeur   */
    public final int get(int pos, int i) {
        if (v[pos] == null) { // pas encore allou�s
            return 0;
        }
        return v[pos][i];
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg("IntArrayVector_InMemory" + pathName + "/" + fileName + "statistics -> ");
        msg(" size: " + size);
        msg("fixedArraySize: " + fixedArraySize);
    }

    public final void clear(int pos) {
        v[pos] = null;
    }
}
