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
import static org.olanto.idxvli.util.BytesAndFiles.*;

/**
 * gestionaire de mots g�r� sur disque avec des IO Map avec 2 hash (stock� dans un long).
 * <p>
 * 
 *
 */
public class StringTable_HomeHash_OnDisk_Clue_XXL implements StringRepository {

    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */
    static final String SOFT_VERSION = "StringTable_HomeHash_OnDisk_DIO_Clue 2.1";
    static final int minSize = 10;  // 2^n; taille des blocs d'initialisation
    static final String ENCODE = "UTF-8";   // encodage utilis�
    /* variables d'un gestionnaire du dictionaire -------------------------------------- */
    /** definit la version */
    String VERSION;
    /** definit le nom g�n�rique des fichiers */
    String GENERIC_NAME;
    /** definit le path pour l'ensemble des fichiers d�pendant de ce Dictionnaire */
    String pathName;
    /** definit le fichier */
    String idxName;
    private int lengthString = 128;  // longueur fixe occup�e par un String
    private int maxSize = 12; //  2^n;
    private int comp32 = 32 - maxSize;
    private int utilSize = (int) Math.pow(2, maxSize) - 1;
    private long collision = 0;
    /** nbr de mots actuellement dans le dictionnaire */
    private int count = 0;
    /** fichier associ� avec les documents */
    private RandomAccessFile rdoc;
    private LongVector hdocclue; // on stock un indice pour �viter de lire le string

    public StringTable_HomeHash_OnDisk_Clue_XXL() {
    }

    /**  cr�e une word table de taille 2^_maxsize par d�faut � l'endroit indiqu� par le path */
    public final StringRepository create(String _pathName, String _idxName, int _maxSize, int _lengthString) {
        return (new StringTable_HomeHash_OnDisk_Clue_XXL(_pathName, _idxName, "ext", _maxSize, _lengthString));
    }

    /**  ouvre un gestionnaire de mots  � l'endroit indiqu� par le _path */
    public final StringRepository open(String _path, String _idxName) {
        return (new StringTable_HomeHash_OnDisk_Clue_XXL(_path, _idxName));
    }

    /** cr�er une nouvelle instance de StringTable*/
    private StringTable_HomeHash_OnDisk_Clue_XXL(String _pathName, String _idxName, String _generic_name, int _maxSize, int _lengthString) {
        createStringTable_HomeHash_OnDisk_DIO_Clue(_pathName, _idxName, _generic_name, _maxSize, _lengthString);
    }

    /** cr�er une nouvelle instance de StringTable � partir des donn�es existantes*/
    private StringTable_HomeHash_OnDisk_Clue_XXL(String _pathName, String _idxName) {  // recharge un gestionnaire
        pathName = _pathName;
        idxName = _idxName;
        loadMasterFile();
        //printMasterFile();
    }

    private final void createStringTable_HomeHash_OnDisk_DIO_Clue(
            String _pathName, String _idxName, String _generic_name,
            int _maxSize, int _lengthString) {
        pathName = _pathName;
        idxName = _idxName;
        GENERIC_NAME = _generic_name;
        VERSION = SOFT_VERSION;
        maxSize = _maxSize;
        comp32 = 32 - maxSize;
        lengthString = _lengthString;
        utilSize = (int) Math.pow(2, maxSize) - 1;
        try {
            rdoc = new RandomAccessFile(pathName + "/" + idxName + ".rnddoc", "rw");
            hdocclue = (new LongVector_On_Disk()).create(pathName, idxName + ".cluedoc", maxSize);
            hdocclue = (new LongVector_On_Disk()).open(pathName, idxName + ".cluedoc");
            initFirstTime();
            saveMasterFile();
        } catch (Exception e) {
            error("IO error in StringTable_HomeHash_OnDisk_DIO_Clue()", e);
        }

    }

    private final int hdoc(long hdoclue) {
        return (int) (hdoclue >>> 32); // partie haute
    }

    private final int clue(long hdoclue) {
        return (int) ((hdoclue << 32) >>> 32); // partie basse
    }

    private final long hdocclue(int hdoc, int clue) {
        return (long) ((long) hdoc << 32) | (((long) (clue) << 32) >>> 32); // haute + basse
    }

    private final void initFirstTime() {
        try {
            int size = (int) Math.pow(2, maxSize);
            for (int i = 0; i < size; i++) {
                hdocclue.set(i, hdocclue(-1, 0));
            }
            // marque la fin
            rdoc.seek((long) utilSize * (long) lengthString);
            rdoc.writeInt(-1);
        } catch (Exception e) {
            error("IO error in initFirstTime()", e);
        }
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + idxName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // �crire les flags
            p.writeObject(GENERIC_NAME);
            p.writeInt(maxSize);
            p.writeInt(lengthString);
            p.writeInt(comp32);
            p.writeInt(count);
            p.writeInt(utilSize);
            p.writeLong(collision);
            msg("save Master String Table File for: " + pathName + "/" + idxName + " id: " + GENERIC_NAME);
            p.flush();
            ostream.close();
            rdoc.close();
            hdocclue.close();
        } catch (IOException e) {
            error("IO error in StringTable.saveMasterFile", e);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + idxName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            GENERIC_NAME = (String) p.readObject();
            maxSize = p.readInt();
            lengthString = p.readInt();
            comp32 = p.readInt();
            count = p.readInt();
            utilSize = p.readInt();
            collision = p.readLong();
            msg("load Master String Table File for: " + pathName + "/" + idxName + " id: " + GENERIC_NAME);
            istream.close();
        } catch (Exception e) {
            error("IO error file StringTable.loadMasterFile");
        }
        try {
            rdoc = new RandomAccessFile(pathName + "/" + idxName + ".rnddoc", "rw");
            hdocclue = (new LongVector_On_Disk()).open(pathName, idxName + ".cluedoc");
        } catch (Exception e) {
            error("IO error in RND WordTable()", e);
        }

    }

    private final void printMasterFile() {
        msg("--- String Table parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("idxName: " + idxName);
        msg("GENERIC_NAME: " + GENERIC_NAME);
        msg("maxSize: " + maxSize);
        msg("lengthString: " + lengthString);
        msg("comp32: " + comp32);
        msg("count: " + count);
        msg("utilSize: " + utilSize);
        msg("collision: " + collision);
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- StringTable is closed now ");
    }

    /**  ajoute un terme au gestionnaire retourne le num�ro du terme, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe d�ja
     */
    public final int put(String w) {
        //  msg(w);
        int iget = get(w);
        if (iget != EMPTY) {
            return iget;
        }  // existe d�j�

        if (count >= utilSize) { // on doit garder un trou pour le get()
            error("*** error StringTable is full");
            return EMPTY;
        }
        int h = hash(w);
        int indirecth = hdoc(hdocclue.get(h));
        int cluew = clueHash(w);
        //msg(w+" h init:"+h);
        while (indirecth != -1) { // cherche un trou
            h = ((h + 1) << comp32) >>> comp32; //addition circulaire
            indirecth = hdoc(hdocclue.get(h));
            collision++;  // est pas tout � fait juste si plusieurs occurences similaires
        }
        // on a trouv� un trou
        //msg(w+" h final:"+h);
        hdocclue.set(h, hdocclue(count, cluew));
        writeString(w, (long) count * (long) lengthString, ENCODE, lengthString, rdoc);
        count++;
        return count - 1;
    }

    public final int get(String w) {
        int h = hash(w);
        long hc = hdocclue.get(h);
        int indirecth = hdoc(hc);
        int cluew = clueHash(w);
        int clueh = clue(hc);
        //msg(w+" h init:"+h);
        while (indirecth != -1 && clueh != cluew) { // cherche un trou ou le mot
            h = ((h + 1) << comp32) >>> comp32; //addition circulaire
            hc = hdocclue.get(h);
            indirecth = hdoc(hc);
            clueh = clue(hc);
        }
        if (indirecth == -1) {
            return EMPTY;
        } // pas trouv�
        else {
            return indirecth;
        }
    }

    public final String get(int i) {
        if (i < 0 || i > count) {
            return NOTINTHIS;
        }
        return readString((long) i * (long) lengthString, ENCODE, rdoc);
    }

    private final int hash(String s) {  // ok
        return (s.hashCode() << comp32) >>> comp32;
    }

    //        private final int clueHash(String s) {  // ok
    //        return s.hashCode();
    //    }
    private final int clueHash(String s) {  // ok
        String s1 = s;
        if (s.length() > 1) {
            s1 = s.substring(1) + s.substring(0, 1);
        }
        // msg(s+","+s1);
        return s1.hashCode();
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "String Table statistics :" + pathName + "/" + idxName + " id: " + GENERIC_NAME
                + "\n  Current version: " + SOFT_VERSION
                + "\n  utilSize: " + utilSize + " count: " + count + " collision: " + collision;
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        return count;
    }

    public final void modify(int i, String newValue) {
        writeString(newValue, (long) i * (long) lengthString, ENCODE, lengthString, rdoc);
    }
}
