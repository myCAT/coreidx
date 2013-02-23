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
import org.olanto.idxvli.mapio.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 *  Comportements d'un vecteur de Long bufferis� avec des IO Map.
 *
 * 
 *
 */
public class LongVector_DirectIO implements LongVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "LongVector_DirectIO 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers d�pendant de ce gestionnaire */
    private String pathName;
    /** definit le path pour l'ensemble des fichiers d�pendant de ce gestionnaire */
    private String fileName;
    private DirectIOFile v;
    private int size = 0;
    private int slice2n = 20;

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public LongVector_DirectIO() {
    }

    /**  cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path */
    public final LongVector create(String _pathName, String _fileName, int _maxSize) {
        return (new LongVector_DirectIO(_pathName, _fileName, _maxSize));
    }

    /**  ouvre un vecteur  � l'endroit indiqu� par le _path */
    public final LongVector open(String _pathName, String _fileName) {
        return (new LongVector_DirectIO(_pathName, _fileName));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /** cr�er une nouvelle instance de WordTable � partir des donn�es existantes*/
    private LongVector_DirectIO(String _pathName, String _fileName) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        loadMasterFile();
        //printMasterFile();
    }

    /** cr�er une nouvelle instance de WordTable*/
    private LongVector_DirectIO(String _pathName, String _fileName, int _maxSize) {
        createLongVector_InMemory(_pathName, _fileName, _maxSize);
    }

    private final void createLongVector_InMemory(String _pathName, String _fileName, int _maxSize) {
        pathName = _pathName;
        fileName = _fileName;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la premi�re fois, � la cr�ation
        try {
            v = new MappedFile();
            v.open(pathName + "/" + fileName + "_dio", MappingMode.FULL, readWriteMode.rw, slice2n, size * 8);
//         v.seek(size*4-4);
//         v.writeInt(0); // marque la derni�re place
        } catch (IOException e) {
            error("IO error in LongVector_DirectIO.initFirstTime", e);
        }
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // �crire les flags
            p.writeInt(size);
            System.out.println("save Int Vector: " + pathName + "/" + fileName);
            p.flush();
            ostream.close();
            v.close();
        } catch (IOException e) {
            error("IO error in LongVector_DirectIO.saveMasterFile", e);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            System.out.println("load Int Vector: " + pathName + "/" + fileName);
            istream.close();
            v = new MappedFile();
            v.open(pathName + "/" + fileName + "_dio", MappingMode.FULL, readWriteMode.rw, slice2n, size * 8);
        } catch (Exception e) {
            error("IO error file LongVector_DirectIO.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Int Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
    }

    /** mets � jour la position pos avec la valeur val */
    public final void set(int pos, long val) {
        try {
            v.seek(pos << 3); // pos*8
            v.writeLong(val);
        } catch (Exception e) {
            error("IO error file LongVector_DirectIO.set pos:" + pos + " val:" + val, e);
        }
    }

    /**  cherche la valeur � la position pos  */
    public final long get(int pos) {
        try {
            v.seek(pos << 3); // pos*8
            return v.readLong();
        } catch (Exception e) {
            error("IO error file LongVector_DirectIO.get pos:" + pos, e);
        }
        return -1;
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
        return "LongVector_DirectIO: " + pathName + "/" + fileName + "statistics -> "
                + "\n  size: " + size;
    }
}
