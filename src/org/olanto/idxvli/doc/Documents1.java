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

package org.olanto.idxvli.doc;

import java.util.*;
import org.olanto.idxvli.util.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.idxvli.doc.KeepProperties.*;
import java.util.concurrent.locks.*;

/**
 * gestionnaire de documents.
 * 
 *
 * gestionnaire de documents.
 *
 * *  <pre>
 *  concurrence:
 *   - // pour les lecteurs
 *   - ï¿½crivain en exclusion avec tous
 *  doit ï¿½tre le point d'accï¿½s pour toutes les structures utilisï¿½es !
 *  </pre>
 *
 */
public class Documents1 implements DocumentManager {

    private StringRepository documentName;
    private IntVector documentSize;
    private LongVector documentDate;
    private BitVector documentInvalid;
    private BitVector documentIndexed;
    private PropertiesManager documentProperties;
    private IdxMode updatingMode = IdxMode.INCREMENTAL;
    private LanguageMode keepLanguage;
    private CollectionMode keepCollection;

    /** crï¿½er une nouvelle instance de repository pour effectuer les create, open*/
    public Documents1() {
    }

    /**  crï¿½e un gestionnaire de documents (la taille et la longueur) ï¿½ l'endroit indiquï¿½ par le path */
    public final DocumentManager create(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            String _path, String _idxName, int _maxSize, int _lengthString) {
        return (new Documents1(_ManagerImplementation, _keepLanguage, _keepCollection,
                _path, _idxName, _maxSize, _lengthString));
    }

    /**  ouvre un gestionnaire de documents  ï¿½ l'endroit indiquï¿½ par le _path */
    public final DocumentManager open(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            IdxMode _updatingMode, String _path, String _idxName) {
        return (new Documents1(_ManagerImplementation, _keepLanguage, _keepCollection,
                _updatingMode, _path, _idxName));
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        DETLOG.info("mode close:" + updatingMode.name());
        if (updatingMode == IdxMode.DIFFERENTIAL) {
            DETLOG.info("Delete All Non Indexed Doc");
            DeleteAllNonIndexedDoc();
        }
        PutInvalidDocLikeAPropertie();
        documentName.close();
        documentSize.close();
        documentDate.close();
        documentInvalid.close();
        documentProperties.close();
        DETLOG.info("--- DocumentManager is closed now ");
    }

    /** crï¿½er une nouvelle instance de DocumentManager ï¿½ partir des donnï¿½es existantes*/
    private Documents1(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            IdxMode _updatingMode, String _pathName, String _idxName) {  // recharge un gestionnaire
        updatingMode = _updatingMode;
        keepLanguage = _keepLanguage;
        keepCollection = _keepCollection;
        switch (_ManagerImplementation) {
            case DIRECT:
                documentName = (new StringTable_Direct_InMemory()).open(_pathName, _idxName + "_name");
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
            case FAST:
                documentName = (new StringTable_HomeHash_InMemory()).open(_pathName, _idxName + "_name");
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
            case BIG:
                documentName = (new StringTable_HomeHash_OnDisk_DIO()).open(_pathName, _idxName + "_name");
                // doit ï¿½tre remplacï¿½ par une implementation sur disque
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
            case XL:
                documentName = (new StringTable_OnDisk_WithCache_MapIO_XL()).open(_pathName, _idxName + "_name");
                // doit ï¿½tre remplacï¿½ par une implementation sur disque
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
            case XXL:
                documentName = (new StringTable_OnDisk_WithCache_XXL()).open(_pathName, _idxName + "_name");
                // doit ï¿½tre remplacï¿½ par une implementation sur disque
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
        }
        if (updatingMode == IdxMode.DIFFERENTIAL) {
            DETLOG.info("open differential vector");
            documentIndexed = (new BitVector_Volatile()).create(_pathName, _idxName + "_indexed", documentInvalid.length());
        }
        if (updatingMode == IdxMode.QUERY && MODE_CONTINUE == ContinueMode.ALT) {
            DETLOG.info("open documentProperties R");
            documentProperties = (new Properties1()).open(_ManagerImplementation, _pathName, _idxName + "_properties", readWriteMode.r);
        } else {
            DETLOG.info("open documentProperties RW");
            documentProperties = (new Properties1()).open(_ManagerImplementation, _pathName, _idxName + "_properties", readWriteMode.rw);
        }
        //  }

    }

    /** crï¿½er une nouvelle instance de Document Manager*/
    private Documents1(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            String _pathName, String _idxName, int _maxSize, int _lengthString) {
        keepLanguage = _keepLanguage;
        keepCollection = _keepCollection;
        switch (_ManagerImplementation) {
            case DIRECT:
                documentName = (new StringTable_Direct_InMemory()).create(_pathName, _idxName + "_name", _maxSize, -1);
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
            case FAST:
                documentName = (new StringTable_HomeHash_InMemory()).create(_pathName, _idxName + "_name", _maxSize, -1);
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
            case BIG:
                documentName = (new StringTable_HomeHash_OnDisk_DIO()).create(_pathName, _idxName + "_name", _maxSize, _lengthString);
                // doit ï¿½tre remplacï¿½ par une implementation sur disque
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
            case XL:
                documentName = (new StringTable_OnDisk_WithCache_MapIO_XL()).create(_pathName, _idxName + "_name", _maxSize, _lengthString);
                // doit ï¿½tre remplacï¿½ par une implementation sur disque
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
            case XXL:
                documentName = (new StringTable_OnDisk_WithCache_XXL()).create(_pathName, _idxName + "_name", _maxSize, _lengthString);
                // doit ï¿½tre remplacï¿½ par une implementation sur disque
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
        }
        documentProperties = (new Properties1()).create(_ManagerImplementation, _pathName, _idxName + "_properties",
                DOC_PROPERTIES_MAXBIT, DOC_PROPERTIES_MAX_LENGHT, _maxSize);
        //  }
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        DETLOG.info(
                "\n------------------------------------------------------------"
                + "\n- DOCS TABLE STAT                                         -"
                + "\n------------------------------------------------------------"
                + "\nmode:" + (updatingMode.name())
                + "\n\nREFERENCE STRUCTURE:"
                + "\n" + documentName.getStatistic()
                + "\n\nSIZE STRUCTURE:"
                + documentSize.getStatistic()
                + "\n\nDATE STRUCTURE:"
                + documentDate.getStatistic()
                + "\n\nINVALID STATUS STRUCTURE:"
                + documentInvalid.getStatistic()
                + "\n\nPROPERTIES STRUCTURE:"
                + documentProperties.getStatistic());
    }
    /** opï¿½ration sur documentName verrous ------------------------------------------*/
    private final ReentrantReadWriteLock documentNameRW = new ReentrantReadWriteLock();
    private final Lock documentNameR = documentNameRW.readLock();
    private final Lock documentNameW = documentNameRW.writeLock();

    /**  ajoute un document au gestionnaire retourne le numï¿½ro du docuemnt*/
    public final int put(String d) {
        documentNameW.lock();
        try {
            //msg("add this:"+d);
            int id = documentName.put(d);
            if (updatingMode == IdxMode.DIFFERENTIAL) { // marque ce document car il existe dans le corpus ï¿½ indexer
                documentIndexed.set(id, true);
            }
            if (keepLanguage == LanguageMode.YES) {
                keepLanguageOfDoc(d, id, documentProperties);
            }
            if (keepCollection == CollectionMode.YES) {
                keepCollectionOfDoc(d, id, documentProperties);
            }
            return id;
        } finally {
            documentNameW.unlock();
        }
    }

    /**  cherche le numï¿½ro du document, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public final int get(String d) {
        documentNameR.lock();
        try {
            return documentName.get(d);
        } finally {
            documentNameR.unlock();
        }
    }

    /**  cherche le document associï¿½ ï¿½ un numï¿½ro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        documentNameR.lock();
        try {
            return documentName.get(i);
        } finally {
            documentNameR.unlock();
        }
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        documentNameR.lock();
        try {
            return documentName.getCount();
        } finally {
            documentNameR.unlock();
        }
    }
    /** opï¿½ration sur documentDate verrous ------------------------------------------*/
    private final ReentrantReadWriteLock documentDateRW = new ReentrantReadWriteLock();
    private final Lock documentDateR = documentDateRW.readLock();
    private final Lock documentDateW = documentDateRW.writeLock();

    /**  enregistre la date pour le document i */
    public final void setDate(int i, long date) {
        documentDateW.lock();
        try {
            documentDate.set(i, date);
        } finally {
            documentDateW.unlock();
        }
    }

    /**  demande  la date pour le document i */
    public final long getDate(int i) {
        documentDateR.lock();
        try {
            return documentDate.get(i);
        } finally {
            documentDateR.unlock();
        }
    }
    /** opï¿½ration sur documentSize verrous ------------------------------------------*/
    private final ReentrantReadWriteLock documentSizeRW = new ReentrantReadWriteLock();
    private final Lock documentSizeR = documentSizeRW.readLock();
    private final Lock documentSizeW = documentSizeRW.writeLock();

    /**  enregistre la taille pour le document i */
    public final void setSize(int i, int size) {
        documentSizeW.lock();
        try {
            documentSize.set(i, size);
        } finally {
            documentSizeW.unlock();
        }
    }

    /**  demande  la taille pour le document i */
    public final int getSize(int i) {
        documentSizeR.lock();
        try {
            return documentSize.get(i);
        } finally {
            documentSizeR.unlock();
        }
    }
    /** opï¿½ration sur documentProperties verrous ------------------------------------------*/
    private final ReentrantReadWriteLock documentPropertiesRW = new ReentrantReadWriteLock();
    private final Lock documentPropertiesR = documentPropertiesRW.readLock();
    private final Lock documentPropertiesW = documentPropertiesRW.writeLock();

    /**
     * enregistre la propiï¿½tï¿½ pour le document i
     * @param i numï¿½ro du document
     * @param properties propirï¿½tï¿½
     */
    public void setPropertie(int i, String properties) {
        documentPropertiesW.lock();
        try {
            documentProperties.put(properties, i);
        } finally {
            documentPropertiesW.unlock();
        }
    }

    /**
     * ï¿½limine la propiï¿½tï¿½ pour le document i
     * @param i numï¿½ro du document
     * @param properties propirï¿½tï¿½
     */
    public void clearPropertie(int i, String properties) {
        documentPropertiesW.lock();
        try {
            documentProperties.clear(properties, i);
        } finally {
            documentPropertiesW.unlock();
        }
    }

    /**
     * Retourne la taille pour le document i
     * @param i numï¿½ro du document
     * @param properties propriï¿½tï¿½
     * @return la valeur de cette propiï¿½tï¿½
     */
    public boolean getPropertie(int i, String properties) {
        documentPropertiesR.lock();
        try {
            return documentProperties.get(properties, i);
        } finally {
            documentPropertiesR.unlock();
        }
    }

    /**
     * propage l'invaliditï¿½ des documents dans les propriï¿½tï¿½s
     */
    public void propagateInvalididy() {
        PutInvalidDocLikeAPropertie();  // pas de protection spï¿½ciale pour la concurrence
    }

    /** le status d'invaliditï¿½ est normalisï¿½ comme une propriï¿½tï¿½ */
    private final void PutInvalidDocLikeAPropertie() {
        //msg("===================== PutInvalidDocLikeAPropertie");
        if (documentProperties != null) {
            documentProperties.put(INVALID_NAME, documentInvalid.get());
        }
    }

    /**  cherche toutes les documents possï¿½dant une propriï¿½tï¿½s*/
    public final SetOfBits satisfyThisProperty(String properties) {
        //msg("===================== satisfyThisProperty");
        if (documentProperties != null) {
            //msg("===================== get:"+properties);
            return documentProperties.get(properties);
        }
        return null;
    }

    /**
     *  rï¿½cupï¿½re le dictionnaire de propriï¿½tï¿½s
     * @return liste des propriï¿½tï¿½s actives
     */
    public List<String> getDictionnary() {
        return documentProperties.getDictionnary();
    }

    /**
     *  rï¿½cupï¿½re le dictionnaire de propriï¿½tï¿½s ayant un certain prï¿½fix (COLECT., LANG.)
     * @param prefix prï¿½fixe des propriï¿½tï¿½s
     * @return liste des propriï¿½tï¿½s actives
     */
    public List<String> getDictionnary(String prefix) {
        return documentProperties.getDictionnary(prefix);
    }

    /** opï¿½ration pour indexer ------------------------------------------*/
    /**  verifie si le document est ï¿½ indexer (nom diffï¿½rent, date diffï¿½rente,
     * si il rï¿½pond vrai, alors il faut indexer le document (car il a peut-ï¿½tre invalidï¿½ un document dï¿½ja existant)
    , pas protï¿½gï¿½ le processus appelant doit rendre exclusif les appels
     */
    public final boolean IndexThisDocument(String fname, long date) {
        int id = get(fname);  // cherche si le document est dï¿½jï¿½ indexï¿½
        if (id == StringRepository.EMPTY) {
            return true;
        } // un nouveau document
        long fdate = getDate(id);
        if (fdate != date) { // une mise ï¿½ jour
            msg("IndexThisDocument maj:" + fname + " fdate " + fdate + " newdate " + date);
            invalid(id);
            return true;
        }
        if (updatingMode == IdxMode.DIFFERENTIAL) { // marque ce document car il existe dans le corpus ï¿½ indexer
            documentIndexed.set(id, true);
        }

        return false; // cas le document est identique,
    }

    /**  rend invalide le document i 
    , pas protï¿½gï¿½ le processus appelant doit rendre exclusif les appels
     */
    public final void invalid(int i) {
        documentDate.set(i, INVALID_DATE); // marque la date avec une valeur impossible
        String fname = get(i); // rï¿½cupï¿½re l'ancien nom'
        documentName.modify(i, INVALID_NAME + fname);  // ajoute une prï¿½fixe impossible
        documentInvalid.set(i, true);
    }

    /**  test si le document i  est valide 
    , pas protï¿½gï¿½ le processus appelant doit rendre exclusif les appels
     */
    public final boolean isValid(int i) {
        return !documentInvalid.get(i);
    }

    /**  le nombre de document valides
    , pas protï¿½gï¿½ le processus appelant doit rendre exclusif les appels
     */
    public final int countValid() {
        int size = documentName.getCount();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (isValid(i)) {
                count++;
            }
        }
        return count;
    }

    /** rend invalide tous les documents qui ne sont pas indexï¿½ 
    , pas protï¿½gï¿½ le processus appelant doit rendre exclusif les appels
     */
    private final void DeleteAllNonIndexedDoc() {
        int size = documentName.getCount();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (!documentIndexed.get(i) && isValid(i)) {
                invalid(i);
                count++;
            }
        }
        DETLOG.info("Nbr of Non-Indexed documents:" + count);
    }
}
