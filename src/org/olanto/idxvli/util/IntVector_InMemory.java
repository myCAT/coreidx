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
 *  Comportements d'un vecteur de Int charg� en m�moire.
 *
 * 
 *
 */
public class IntVector_InMemory implements IntVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "IntVector_InMemory 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String pathName;
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    private String fileName;
    private int[] v;
    private int size = 0;

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public IntVector_InMemory() {
    }

    /**  cr�e un vecteur de taille 2^_maxSize � l'endroit indiqu� par le path */
    public final IntVector create(String _pathName, String _fileName, int _maxSize) {
        return (new IntVector_InMemory(_pathName, _fileName, _maxSize));
    }

    /**  ouvre un vecteur  � l'endroit indiqu� par le _path */
    public final IntVector open(String _pathName, String _fileName) {
        return (new IntVector_InMemory(_pathName, _fileName));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /** cr�er une nouvelle instance de WordTable � partir des donn�es existantes*/
    private IntVector_InMemory(String _pathName, String _fileName) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        loadMasterFile();
        //printMasterFile();
    }

    /** cr�er une nouvelle instance de WordTable*/
    private IntVector_InMemory(String _pathName, String _fileName, int _maxSize) {
        createIntVector_InMemory(_pathName, _fileName, _maxSize);
    }

    private final void createIntVector_InMemory(String _pathName, String _fileName, int _maxSize) {
        pathName = _pathName;
        fileName = _fileName;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la premi�re fois, � la cr�ation
        v = new int[size];
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // �crire les flags
            p.writeInt(size);
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
            v = (int[]) p.readObject();
            System.out.println("load Int Vector: " + pathName + "/" + fileName);
            istream.close();
        } catch (Exception e) {
            error("IO error file IntVector_InMemory.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Int Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
    }

    /** mets � jour la position pos avec la valeur val */
    public final void set(int pos, int val) {
        v[pos] = val;
    }

    /**  cherche la valeur � la position pos  */
    public final int get(int pos) {
        return v[pos];
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
        return "IntVector_InMemory: " + pathName + "/" + fileName + "statistics -> "
                + "\n  size: " + size;
    }
}
