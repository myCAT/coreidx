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

package org.olanto.idxvli.jjbg;

import java.io.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.util.BytesAndFiles.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;
import java.util.concurrent.locks.*;
import org.olanto.idxvli.util.ByteArrayVector;
import org.olanto.idxvli.util.ByteArrayVector_InMemory;
import org.olanto.idxvli.util.ByteArrayVector_OnDisk;
import org.olanto.idxvli.util.IntVector;
import org.olanto.idxvli.util.IntVector_DirectIO;
import org.olanto.idxvli.util.IntVector_InMemory;

/**
 * Une classe stocker des vecteurs de bytes sur disque.
 *
 * <p>idées: Olanto Foundation Geneva & Joel Borcard
 * <p>
 * 
 *
 *
 * v2.2
 * - elimination du READWRITE mod
 * -�limination cas de la continuit� (pour �liminer un seek, ca marche pas!)
 * - nettoyage et adaption aux classes Messages et BytesAndFiles
 *
 * v3.0
 * introduction d'un mode direct pour les vecteurs courts
 * introduction d'une extension proportionnelle (proportional-nextsetpercent)
 *
 * v4.0
 *  modification realSize et StoredSize  //21-11-2005
 *
 *  <pre>
 *  concurrence:
 *   - // pour les lecteurs
 *   - �crivain en exclusion avec tous
 *  </pre>
 */
public class ObjectStore4 implements ObjectStorage4 {

    /**  version actuel du gestionnaire d'objet */
    //public static final String smallObjectImplementation="ByteArrayVector_InMemory";
    public static String smallObjectImplementation = "ByteArrayVector_OnDisk";
    static final String SOFT_VERSION = "ObjectStore3 2.2";
    static final String MASTER_FILE = "ObjectStore3.def";
    static final int EMPTY = -1;
    static final int FIRSTNBSET = 128; // pour l'initialisation de nbSet
    static final int MINNBSET = 1; // taille minimum de nbSet
    static final boolean proportional = true; // extention proportionnel
    static final int nextsetpercent = 5; // % d�termine la taille de l'extension si proportional=true
    static final int MAX_BLOCKID = 27; // 2^MAX_BLOCKID est le nombre max d'id � un niveau donn�
    static final int MAX_BLOCKID_COMP = 32 - MAX_BLOCKID; // d�calage compl�mentaire 32->64 si long, 2^MAX_BLOCKID_COMP>maxlevel    static final int IDINCREMENTSIZE=IDFIRSTSIZE; // incr�ment du vecteur d'id (doit �tre grand pour �viter des copies)
    static final byte[] MARK = new byte[1];  // un marqueur de longueur 1
    static final boolean MARKSPACE = false;  // r�serve les blocs des fichiers au moment de l'allocation si = true (true +++)
    // =true -> MINNBSET petit < 8 par exemple
    static final boolean SAVE = true;  // sauve r�ellement les donn�es si = true
    static boolean verbose = false;
    /* constantes d'un gestionnaire d'objet -------------------------------------- */
    /** nombre maximum de niveau d'extension */
    private int MAX_FILE;
    /** definit le ratio d'extension P/Q (chaque niveau est augment� de ce facteur)*/
    private int P;
    /** definit le ratio d'extension P/Q (chaque niveau est augment� de ce facteur)
     *  P>Q (progression exponentielle)
     *  P=Q (progression lineaire)
     */
    private int Q;
    /** definit un increment additif � chaque extention >=0*/
    private int MIN_EXT;
    /** definit la taille du niveau 0 (celui de d�part), le niveau suivant � une taille
     *  size(k)=size(k-1)*P/Q+MIN_EXT
     */
    private int SIZE_0;
    /** definit la version */
    String VERSION;
    /** definit le nom g�n�rique des fichiers */
    String GENERIC_NAME;
    /* ---------------------------------------------------------------------------*/
    /** definit le path pour l'ensemble des fichiers d�pendant de cet ObjectStore */
    String pathName;
    /** definit les tailles � chaque niveau */
    private long[] size;
    /** d�finit l'utilisation des blocs a chaque niveau
     * la valeur >= 0 d�finit la longueur r�ellement utilis�e
     * la valeur -1 d�finit que le bloc est vide
     */
    private int[][] blockAllocation;
    /** premier canditat possible dans blockAllocation qui est un bloc vide
     *  il est certain que tous les blocs inf�rieurs sont allou�s
     */
    private int[] firstEmptyBlock;
    /** nombre de blocs allou�s sur les disques pour chaque niveau */
    private int[] nbBlock;
    /** nombre de blocs allou�s sur les disques pour chaque niveau � chaque extension */
    private int[] nbSet;
    /** vecteur des id */
    private /*long*/ int[] idUser;   // sans doute un int peut suffir dans beaucoup de cas (nblevel*nbmaxbloc)
    /** fichier associ� avec chaque niveau */
    private RandomAccessFile[] rfc;
    /** pour la gestion d'une allocation fixe, avec gestion externe des id */
    private int maxSize = 0; // puissance de 2
    private int utilSize = 0; //  2^maxSize
    private int minBigSize = 0; // 32,64
    /** statistique */
    private long cntwrite = 0;
    private long cntread = 0;
    private long cntmove = 0;
    private long cntappend = 0;
    private long bytewrite = 0;
    private long byteread = 0;
    private long bytemove = 0;
    private long byteappend = 0;
    /** la structure pour les petits objets */
    private ByteArrayVector smallObject;
    private final String SMALL_EXT = "_SMALL";
    /** la structure la taille r�elle des objets */
    private IntVector realSize;
    private final String REALSIZE_EXT = "_REALSIZE";
    /* mode lecture ou mise a jour */
    private readWriteMode RW = readWriteMode.rw;

    /** cr�er une nouvelle instance de ObjectStore pour effectuer les create, open*/
    public ObjectStore4() {  // recharge un gestionnaire
    }

    /** cr�er une nouvelle instance de ObjectStore � partir des donn�es existantes*/
    private ObjectStore4(implementationMode implementation, String _pathName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        RW = _RW;
        loadMasterFile();
        openArrayFile();
        switch (implementation) {
            case FAST:
                smallObject = (new ByteArrayVector_InMemory()).open(pathName, MASTER_FILE + SMALL_EXT, RW);
                realSize = (new IntVector_InMemory().open(pathName, MASTER_FILE + REALSIZE_EXT));
                break;
            case BIG:
                smallObject = (new ByteArrayVector_OnDisk()).open(pathName, MASTER_FILE + SMALL_EXT, RW);
                realSize = (new IntVector_DirectIO().open(pathName, MASTER_FILE + REALSIZE_EXT));
                break;
            case XL:  // plus rapide pour les tailles !
                smallObject = (new ByteArrayVector_OnDisk()).open(pathName, MASTER_FILE + SMALL_EXT, RW);
                realSize = (new IntVector_InMemory().open(pathName, MASTER_FILE + REALSIZE_EXT));
                break;
        }
        //printMasterFile();
    }

    /** cr�er une nouvelle instance de ObjectStore avec des valeurs par defaut*/
    private ObjectStore4(implementationMode implementation, String _pathName, int _maxSize, int _minBigSize, String _generic_name) {
        createObjectStore(implementation, _pathName, _generic_name, _maxSize, 31, 2, 1, 0, _minBigSize);
        // createObjectStore(_pathName,_generic_name,_maxSize,15,4,1,0,8);
    }

    /**  cr�e un ObjectStorage de taille 2^maxSize � l'endroit indiqu� par le path */
    public final ObjectStorage4 create(implementationMode implementation, String path, int maxSize, int minBigSize) {
        return (new ObjectStore4(implementation, path, maxSize, minBigSize, "ext"));
    }

    public final ObjectStorage4 open(implementationMode implementation, String path, readWriteMode _RW) {
        return (new ObjectStore4(implementation, path, _RW));
    }

    public final void createObjectStore(
            implementationMode implementation,
            String _pathName,
            String _generic_name,
            int _maxSize,
            int _max_file,
            int _p,
            int _q,
            int _min_ext,
            int _size_0) {
        pathName = _pathName;
        GENERIC_NAME = _generic_name;
        maxSize = _maxSize;
        utilSize = (int) Math.pow(2, maxSize);
        MAX_FILE = _max_file;
        P = _p;
        Q = _q;
        MIN_EXT = _min_ext;
        SIZE_0 = _size_0;
        VERSION = SOFT_VERSION;
        initFirstTime();
        switch (implementation) {
            case FAST:
                smallObject = (new ByteArrayVector_InMemory()).create(pathName, MASTER_FILE + SMALL_EXT, maxSize, SIZE_0 / 2);
                realSize = (new IntVector_InMemory().create(pathName, MASTER_FILE + REALSIZE_EXT, maxSize));
                break;
            case BIG:
                smallObject = (new ByteArrayVector_OnDisk()).create(pathName, MASTER_FILE + SMALL_EXT, maxSize, SIZE_0 / 2);
                realSize = (new IntVector_DirectIO().create(pathName, MASTER_FILE + REALSIZE_EXT, maxSize));
                break;
            case XL:
                smallObject = (new ByteArrayVector_OnDisk()).create(pathName, MASTER_FILE + SMALL_EXT, maxSize, SIZE_0 / 2);
                realSize = (new IntVector_InMemory().create(pathName, MASTER_FILE + REALSIZE_EXT, maxSize));
                break;
        }
        openArrayFile();
        close();
    }

    private final void initFirstTime() { // n'utiliser que la premi�re fois, � la cr�ation
        size = new long[MAX_FILE];
        size[0] = SIZE_0;
        for (int i = 1; i < MAX_FILE; i++) {
            size[i] = size[i - 1] * (long) P / (long) Q + (long) MIN_EXT;
        }
        blockAllocation = new int[MAX_FILE][];
        firstEmptyBlock = new int[MAX_FILE];
        nbBlock = new int[MAX_FILE];
        nbSet = new int[MAX_FILE];
        initNbSet();
        idUser = new /*long*/ int[utilSize];
        for (int i = 0; i < utilSize; i++) {
            idUser[i] = EMPTY;
        } // initialise les id � vide
    }

    private final void initNbSet() { // initialise heuristiquement nbSet - peut �tre red�finit par une extension proportionnelle
        int setSize = FIRSTNBSET;
        for (int i = 0; i < MAX_FILE; i++) {
            nbSet[i] = setSize;
            setSize = Math.max(setSize / 2, MINNBSET);
        }
    }

    public final synchronized void printStatistic() {
        msg("write : " + cntwrite + " : " + bytewrite);
        msg("read  : " + cntread + " : " + byteread);
        msg("append: " + cntappend + " : " + byteappend);
        msg("move  : " + cntmove + " : " + bytemove);
        //printMasterFile();
        msg("--- nb allocated set");
        for (int i = 0; i < MAX_FILE; i++) {
            if (nbBlock[i] != 0) {
                msg("" + i + ": " + nbBlock[i]);
            }
        }
        //smallObject.printStatistic();
        //realSize.printStatistic();
    }

    public final void resetStatistic() {
        cntwrite = 0;
        cntread = 0;
        cntmove = 0;
        cntappend = 0;
        bytewrite = 0;
        byteread = 0;
        bytemove = 0;
        byteappend = 0;

    }

    public final long getSpace() { // calcule en byte l'espace occup�
        long tot = 0;
        for (int i = 0; i < MAX_FILE; i++) {
            tot += size[i] * nbBlock[i];
        }
        return tot;
    }

    private final void printMasterFile() {
        msg("--- Object Store parameter, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("GENERIC_NAME: " + GENERIC_NAME);
        msg("MAX_FILE: " + MAX_FILE);
        msg("P: " + P);
        msg("Q: " + Q);
        msg("MIN_EXT: " + MIN_EXT);
        msg("SIZE_0: " + SIZE_0);
        msg("VERSION: " + VERSION);
        msg("maxSize: " + maxSize);
        msg("utilSize: " + utilSize);
        msg("--- size allocation en byte");
        for (int i = 0; i < MAX_FILE; i++) {
            msg("" + i + ": " + size[i]);
        }
        msg("--- size allocation set ");
        for (int i = 0; i < MAX_FILE; i++) {
            msg("" + i + ": " + nbSet[i]);
        }
        msg("--- nb allocated set");
        for (int i = 0; i < MAX_FILE; i++) {
            msg("" + i + ": " + nbBlock[i]);
        }

    }

    public final void close() {
        saveMasterFile();
        smallObject.close();
        realSize.close();
        //printMasterFile();
        msg("--- Object Store is closed now ");
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            if (RW == readWriteMode.rw) {
                FileOutputStream ostream = new FileOutputStream(pathName + "/" + MASTER_FILE);
                ObjectOutputStream p = new ObjectOutputStream(ostream);
                p.writeObject(VERSION); // �crire les flags
                p.writeObject(GENERIC_NAME);
                p.writeInt(MAX_FILE);
                p.writeInt(P);
                p.writeInt(Q);
                p.writeInt(MIN_EXT);
                p.writeInt(SIZE_0);
                p.writeInt(maxSize);
                p.writeInt(utilSize);
                p.writeObject(blockAllocation);
                p.writeObject(firstEmptyBlock);
                p.writeObject(nbBlock);
                p.writeObject(nbSet);
                p.writeObject(size);
                p.writeObject(idUser);
                msg("save Master ObjectStore File for: " + pathName + "/" + MASTER_FILE + " id: " + GENERIC_NAME);
                p.flush();
                ostream.close();
            } else {
                msg("UnSave Object Store: " + pathName + "/" + MASTER_FILE);
            }
            for (int i = 0; i < MAX_FILE; i++) {
                rfc[i].close();
            }

        } catch (IOException e) {
            error("IO error in ObjectStore.saveMasterFile", e);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + MASTER_FILE);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            GENERIC_NAME = (String) p.readObject();
            MAX_FILE = p.readInt();
            P = p.readInt();
            Q = p.readInt();
            MIN_EXT = p.readInt();
            SIZE_0 = p.readInt();
            maxSize = p.readInt();
            utilSize = p.readInt();
            blockAllocation = (int[][]) p.readObject();
            firstEmptyBlock = (int[]) p.readObject();
            nbBlock = (int[]) p.readObject();
            nbSet = (int[]) p.readObject();
            size = (long[]) p.readObject();
            idUser = (/*long*/int[]) p.readObject();
            msg("load Master ObjectStore File for: " + pathName + "/" + MASTER_FILE + " id: " + GENERIC_NAME);
            istream.close();
        } catch (Exception e) {
            error("IO error file ObjectStore.loadMasterFile", e);
        }
    }

    private final void openArrayFile() {
        rfc = new RandomAccessFile[MAX_FILE];
        try {
            rfc = new RandomAccessFile[MAX_FILE];
            for (int i = 0; i < MAX_FILE; i++) {
                rfc[i] = new RandomAccessFile(pathName + "/" + GENERIC_NAME + "_" + i + ".rdn", RW.name());
            }
        } catch (Exception e) {
            error("IO error in openArrayFile()", e);
        }
    }

    /**  stocke b avec un identifiant impos�,
     * cette option est � utilis� avec le  create(String path,int maxSize) */
    private final int write(int user, byte[] b) {
        if (SAVE) {
            if (user < utilSize) {
                cntwrite++;
                bytewrite += b.length;
                if (b.length > SIZE_0 / 2) {  // cas ou l'on condid�re que l'on a un gros objet
                    /*long*/ int id = getIdForNBytes(b.length);  // cherche l'id du block
                    //msg("write 1");showVector(b);

                    int status = storeNBytes(b, id); // a tester si erreur
                    idUser[user] = id; // met � jour le id du user ...
                    return status; // on pourrai retourner une erreur ...
                } else { // cas ou l'on condid�re que l'on a un petit objet
                    smallObject.set(user, b);
                    return STATUS_OK;
                }
            } else {
                error("block storage is too small");
                return STATUS_ERROR;
            }
        } else {
            return STATUS_SIMULATION;
        }
    }

    //    /**  attribue un identifiant � un objet pour le retrouver
    //     */
    //    private int getIdUser() {
    //        // on ne r�cup�re pas les identifiants pour le moment
    //        // on peut impl�menter le systeme des first pour le faire ...
    //        if (nextIdUser>=idUser.length){ // il manque des id, on agrandit le vecteur
    //            int oldSize = idUser.length;
    //            int newSize = oldSize +IDINCREMENTSIZE;
    //            /*long*/ int[] it = new /*long*/ int[newSize];
    //            System.arraycopy(idUser, 0, it, 0, oldSize);
    //            idUser=it;
    //
    //        }
    //        nextIdUser++;
    //        return nextIdUser-1;
    //    }
    /**  stock sur le disk ces bytes � cet id  et met � jour la longueur utilis�e
     * retourne 0 si ok sinon <0
     */
    private final int storeNBytes(byte[] b, /*long*/ int id) {
        // ici on devrait tester la validit� de l'id (comment?)
        // constistance de la taille et du niveau ...
        int level = getLevelForThisId(id);
        int blockid = getBlockForThisId(id);
        //msg("storeNBytes 1");showVector(b);
        int status = writeBytes(b, (long) blockid * (long) size[level], rfc[level]);
        blockAllocation[level][blockid] = b.length; // met � jour la longueur
        if (verbose) {
            msg("write in  block no:" + blockid + ", " + b.length + " bytes at level " + level);
        }
        return status;
    }

    /**  cherche un id pour b bytes */
    private /*long*/ final int getIdForNBytes(int b) {
        int level = getLevelForNBytes(b);
        //msg("level: "+level);
        int blockid = getEmptyBlockAtThisLevel(level);
        //msg("blockid: "+blockid);
        return forgeId(level, blockid);
    }

    /**  cherche le niveau pour b bytes */
    private final int getLevelForNBytes(int b) {
        // doit �tre optimis� par une recherche dichotomique (pour 32 niveaux, cela reste ok)
        for (int i = 0; i < MAX_FILE; i++) {
            if (size[i] >= b) {
                return i;
            }
        }
        error("*** error in getLevelForNBytes() too big: " + b);
        return STATUS_ERROR;  // en erreur tout est trop petit
    }

    /**  cherche un block vide a ce niveau */
    private final int getEmptyBlockAtThisLevel(int level) {
        try {
            if (nbBlock[level] == 0) { // premi�re allocation � ce niveau
                if (verbose) {
                    msg("first allocation of " + nbSet[level] + " blocks (" + size[level] + " bytes) at level " + level);
                }
                blockAllocation[level] = new int[nbSet[level]]; // alloue le vecteur
                for (int i = 0; i < nbSet[level]; i++) {
                    blockAllocation[level][i] = EMPTY;
                } // init � vide
                if (MARKSPACE) {
                    long reserveSpace = ((long) nbSet[level] * (long) size[level]) - 1;
                    rfc[level].seek(reserveSpace);
                    rfc[level].write(MARK); // marque la derni�re place
                }
                firstEmptyBlock[level] = 0; // le premier block est vide
                nbBlock[level] = nbSet[level]; // init nbBlock;
            }
            // pas la premi�re fois
            for (int i = firstEmptyBlock[level]; i < nbBlock[level]; i++) { // cherche un block vide dans la liste
                // msg("test blk:"+i);
                if (blockAllocation[level][i] == EMPTY) {
                    blockAllocation[level][i] = 0;  // 0 byte dans ce block mais maintenant il est r�serv�
                    firstEmptyBlock[level] = i + 1;
                    return i;
                }
            }
            // plus de block vide ...
            if (proportional) {
                nbSet[level] = (blockAllocation[level].length * nextsetpercent / 100) + 1;
            } // �tend de 5%
            if (verbose) {
                msg("new allocation of " + nbSet[level] + " blocks (" + size[level] + " bytes) at level " + level);
            }
            blockAllocation[level] = incrementSize(blockAllocation[level], nbSet[level]); // �tend le vecteur
            firstEmptyBlock[level] = nbBlock[level]; // le premier block du nouveau set est vide
            nbBlock[level] += nbSet[level];         // ajoute le nombre de block allou�s
            for (int i = firstEmptyBlock[level]; i < nbBlock[level]; i++) {
                blockAllocation[level][i] = EMPTY;
            } // init � vide les nouveaux block
            if (MARKSPACE) {
                long reserveSpace = ((long) nbBlock[level] * (long) size[level]) - 1;
                rfc[level].seek(reserveSpace);
                rfc[level].write(MARK); // marque la derni�re place
            }
            firstEmptyBlock[level]++; // ajoute 1 car on va ramener ce block
            return firstEmptyBlock[level] - 1;  // on ram�ne le premier bock du nouveau set
        } catch (Exception e) {
            error("IO error in getEmptyBlockAtThisLevel()", e);
            return STATUS_ERROR;  // en erreur
        }
    }

    /* ces m�thodes peuvent �tre pass�e en mode long si 2^MAX_BLOCKID*nblevel > 32 bit
    private /*long*/ final int forgeId(int level, int blockId) {
        return ((/*long*/int) level << MAX_BLOCKID) + blockId;
    }

    private final int getLevelForThisId(/*long*/int id) {
        return (int) (id >>> MAX_BLOCKID);
    }

    private final int getBlockForThisId(/*long*/int id) {
        return (int) ((id << (MAX_BLOCKID_COMP)) >>> MAX_BLOCKID_COMP);
    }

    /**  lib�re un block
     */
    private final void releaseBlock(/*long*/int id) {
        // il serait possible de remettre � z�ro le fichier si n�cessaire  ?
        int level = getLevelForThisId(id);
        int blockid = getBlockForThisId(id);
        if (verbose) {
            msg("release block no: " + blockid + "  (" + size[level] + " bytes) at level " + level);
        }
        blockAllocation[level][blockid] = EMPTY; // marque � vide le block rendu
        if (blockid < firstEmptyBlock[level]) {
            firstEmptyBlock[level] = blockid;
        } // le premier block est le block rendu
    }
    /** op�ration sur documentName verrous ------------------------------------------
     * les sections prot�g�es ne doivent pas s'appeler entre-elle -> deadlock*/
    private final ReentrantReadWriteLock structRW = new ReentrantReadWriteLock();
    private final Lock structR = structRW.readLock();
    private final Lock structW = structRW.writeLock();

    /**  ajoute des bytes a cet objet
     *   si 0 = OK
     */
    public final int append(int[] b, int user, int to) {
        structW.lock();
        try {
            //msg("append 1");showVector(b);
            if (SAVE) {
                if (OBJ_COMPRESSION == Compression.NO) {
                    byte[] byteidx = new byte[to * 4];  // convertit les int en byte
                    intTobyte(b, to * 4, byteidx);
                    return appendAndSetSize(byteidx, user, to * 4);
                } else { // on compresse
                    byte[] comp = compressVInt(b, to);
                    return appendAndSetSize(comp, user, to * 4);
                }
            } else {
                return STATUS_SIMULATION;
            }
        } finally {
            structW.unlock();
        }
    }

    /**  ajoute des bytes a cet objet
     *   si 0 = OK
     */
    public final int append(byte[] b, int user, int realLength) {
        structW.lock();
        try {
            return appendAndSetSize(b, user, realLength);
        } finally {
            structW.unlock();
        }
    }

    /**  lib�re un id (ceux vus par les utilisateurs)
     */
    public final void releaseId(int user) {
        structW.lock();
        try {
            releaseBlock(idUser[user]);
            idUser[user] = EMPTY;
        } finally {
            structW.unlock();
        }
    }

    /**  pour le debuging, imprime l'id level/block
     */
    public final void printNiceId(int user) {
        structR.lock();
        try {
            if (idUser[user] != EMPTY) {
                msg("userid:" + user + " is at level:" + getLevelForThisId(idUser[user]) + " block:" + getBlockForThisId(idUser[user]));
            } else {
                if (smallObject != null) {
                    msg("userid:" + user + " has a small allocation");
                } else {
                    msg("userid:" + user + " is not allocated");
                }
            }
        } finally {
            structR.unlock();
        }
    }

    /**  retourne l'objet stock� completement
     *   si null = erreur
     */
    public final int[] readInt(int user) {
        structR.lock();
        try {
            /**  retourne l'objet stock� completement
             *   si null = erreur
             */
            // msg("readInt:"+user);
            // msg("compression:"+OBJ_COMPRESSION+" realSize:"+realSize(user)+" storedSize:"+storedSize(user));
            if (OBJ_COMPRESSION == Compression.NO) {
                byte[] byteidx = read(user);
                if (byteidx.length % 4 != 0) {
                    error("int[] read(int user) conversion error");
                    return null;
                }
                int[] res = new int[byteidx.length / 4];
                byteToint(res, byteidx.length, byteidx);
                return res;
            } else {
                return decompressVInt(read(user), internalRealSize(user) / 4);
            }
        } finally {
            structR.unlock();
        }
    }

    /**  retourne la taille de l'objet*/
    public final int storedSize(int user) {
        structR.lock();
        try {
            /*long*/ int id = idUser[user];
            if (id != EMPTY) {  //gros objet
                int level = getLevelForThisId(id);
                int blockid = getBlockForThisId(id);
                return blockAllocation[level][blockid];
            } else { // petit objet
                return smallObject.length(user);
            }
        } finally {
            structR.unlock();
        }
    }

    /**  retourne la taille r�el de l'objet sans compression*/
    public final int realSize(int user) {
        structR.lock();
        try {
            return internalRealSize(user);
        } finally {
            structR.unlock();
        }
    }

    /** partie priv�e ------------------------------------------------------------ */
    /**  lire les bytes de cet objet
     *   si 0 = OK
     */
    /**  retourne la taille r�el de l'objet sans compression*/
    private final int internalRealSize(int user) {
        structR.lock();
        try {
            return realSize.get(user);
        } finally {
            structR.unlock();
        }
    }

    private final byte[] read(int user) {
        /*long*/ int id = idUser[user];
        //TimerNano trPOS= new TimerNano("read",true);
        byte[] b;
        cntread++;
        if (id != EMPTY) {  //gros objet
            // msg("read like a big");
            int level = getLevelForThisId(id);
            int blockid = getBlockForThisId(id);
            int lengthToRead = blockAllocation[level][blockid];
            byteread += lengthToRead;
            b = readBytes(lengthToRead, (long) blockid * (long) size[level], rfc[level]);
            // msg("read block no:"+blockid+", "+b.length+" bytes at level "+level);
        } else { // petit objet
            //  msg("read like a small");
            b = smallObject.get(user);
            byteread += b.length;
        }
        // long readtime=trPOS.stop(true);
        // msg("   read+"+b.length+" : time "+ readtime+"[us]");
        // showVector(b);
        return b;
    }

    private final void incrementRealSize(int user, int inc) {
        int actual = realSize.get(user);
        realSize.set(user, actual + inc);
    }

    /**  ajoute des bytes a cet objet
     *   si 0 = OK
     */
    private final int appendAndSetSize(byte[] b, int user, int realLength) {
        incrementRealSize(user, realLength); // adjust realSize
        //if (user==285118)msg("285118 from appendAndSetSize: realSize"+realSize.get(user)+" storeSize:"+this.storedSize(user));
        return internalAppend(b, user);
    }

    private final int internalAppend(byte[] b, int user) {
        //msg("append 2");showVector(b);
        /*long*/ int id = idUser[user];
        if (id == EMPTY && smallObject.get(user) == null) {
            return write(user, b);
        } // cas du premier append (initialise un id)
        if (id != EMPTY) {  // big block
            //msg("append big blk");showVector(b);
            int level = getLevelForThisId(id);
            int blockid = getBlockForThisId(id);
            int newSize = blockAllocation[level][blockid] + b.length; // calcule la nouvelle taille de l'objet
            if (newSize > size[level]) { // on d�passe, il faut migrer
                // version r�cursive si next level est vraiment le prochain niveau
                if (verbose) {
                    msg("move block no:" + blockid + ", " + blockAllocation[level][blockid] + " bytes at level " + level);
                }
                moveToNextLevel(user, b.length);  // d�place le bloc d�finit par cet id au niveau suivant, on ne teste pas l'erreur possible
                return internalAppend(b, user);         // essaye de faire l'append maintenant
            }
            // il y a de la place on concat�ne
            cntappend++;
            byteappend += b.length;
            int status = writeBytes(b, (long) blockid * (long) size[level] + (long) blockAllocation[level][blockid], rfc[level]);
            blockAllocation[level][blockid] += b.length; // met � jour la longueur
            if (verbose) {
                msg("append to block no:" + blockid + ", " + b.length + " bytes at level " + level);
            }
            return status;
        } else {
            // small block
            if (verbose) {
                msg("append small blk");
            }//showVector(b);
            byte[] first = read(user);  // relit les bytes d�ja stock�s
            int newSize = first.length + b.length; // calcule la nouvelle taille de l'objet
            byte[] res = new byte[newSize];
            System.arraycopy(first, 0, res, 0, first.length); // copie first
            System.arraycopy(b, 0, res, first.length, b.length); // copie first
            //msg("new vector");showVector(res);
            smallObject.clear(user); // �limine l'ancien vecteur
            int status = write(user, res); // peut le migrer ou le laisser dans smallObject
            return status;
        }
    }

    /**  d�place le bloc d�finit par cet id au niveau suivant n�cessaire,
     *  on peut donc sauter plusieurs niveaux
     * si o=ok sinon <0
     */
    private final int moveToNextLevel(int user, int increment) {
        cntmove++;
        bytemove += increment;
        byte[] blocktomove = read(user);
        releaseBlock(idUser[user]);     // lib�re le block actuel
        /*long*/ int id = getIdForNBytes(blocktomove.length + increment);  // cherche l'id du block demand� (avec l'incr�ment)
        int status = storeNBytes(blocktomove, id); // recopie le block a tester si erreur
        idUser[user] = id; // met � jour le id du user ...
        return status;
    }
}
