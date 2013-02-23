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

import org.olanto.idxvli.util.*;
import org.olanto.idxvli.*;
import java.util.List;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 * gestionnaire de documents.
 * 
 * gestionnaire de documents. cette version est allÃ©gÃ©e pour ne pas tenir compte des dates, des properties, des ...
 * seul les noms des documents sont conservï¿½s et donc le mode IdxMode.INCREMENTAL;
 */
public class Documents1_ligth implements DocumentManager {

    private StringRepository documentName;
    /** structure contenant les documents non-indexables*/
    private SetOfWords dontIndexThisDocuments;
    private IdxMode updatingMode = IdxMode.INCREMENTAL;
//    private LanguageMode keepLanguage;
//    private CollectionMode keepCollection;

    /** crï¿½er une nouvelle instance de repository pour effectuer les create, open*/
    public Documents1_ligth() {
    }

    /**  crï¿½e un gestionnaire de documents (la taille et la longueur) ï¿½ l'endroit indiquï¿½ par le path */
    public final DocumentManager create(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            String _path, String _idxName, int _maxSize, int _lengthString) {
        return (new Documents1_ligth(_ManagerImplementation, _keepLanguage, _keepCollection,
                _path, _idxName, _maxSize, _lengthString));
    }

    /**  ouvre un gestionnaire de documents  ï¿½ l'endroit indiquï¿½ par le _path */
    public final DocumentManager open(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            IdxMode _updatingMode, String _path, String _idxName) {
        return (new Documents1_ligth(_ManagerImplementation, _keepLanguage, _keepCollection,
                _updatingMode, _path, _idxName));
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        msg("mode close:" + updatingMode.name());
        documentName.close();
        //  }
        msg("--- DocumentManager is closed now ");
    }

    /** crï¿½er une nouvelle instance de DocumentManager ï¿½ partir des donnï¿½es existantes*/
    private Documents1_ligth(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            IdxMode _updatingMode, String _pathName, String _idxName) {  // recharge un gestionnaire
        if (updatingMode == IdxMode.DIFFERENTIAL) {
            error("Documents1_ligth must be in INCREMENTAL mode");
        }
        updatingMode = _updatingMode;
        switch (_ManagerImplementation) {
            case FAST:
                documentName = (new StringTable_HomeHash_InMemory()).open(_pathName, _idxName + "_name");
                break;
            case BIG:
                documentName = (new StringTable_HomeHash_OnDisk_DIO()).open(_pathName, _idxName + "_name");
                break;
            case XL:
                documentName = (new StringTable_OnDisk_WithCache_MapIO_XL()).open(_pathName, _idxName + "_name");
                break;
            case XXL:
                documentName = (new StringTable_OnDisk_WithCache_XXL()).open(_pathName, _idxName + "_name");
                break;
        }
        msg("load excluding list of documents:" + IDX_DONTINDEXTHISDOC);
        dontIndexThisDocuments = new SetOfWords(IDX_DONTINDEXTHISDOC);

    }

    /** crï¿½er une nouvelle instance de Document Manager*/
    private Documents1_ligth(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            String _pathName, String _idxName, int _maxSize, int _lengthString) {
        switch (_ManagerImplementation) {
            case FAST:
                documentName = (new StringTable_HomeHash_InMemory()).create(_pathName, _idxName + "_name", _maxSize + 1, -1); // on double la taille pour les collisions
                break;
            case BIG:
                documentName = (new StringTable_HomeHash_OnDisk_DIO()).create(_pathName, _idxName + "_name", _maxSize + 1, _lengthString);
                break;
            case XL:
                documentName = (new StringTable_OnDisk_WithCache_MapIO_XL()).create(_pathName, _idxName + "_name", _maxSize, _lengthString);  // sauf pour celui-ci on accepte plus de collision
                break;
            case XXL:
                documentName = (new StringTable_OnDisk_WithCache_XXL()).create(_pathName, _idxName + "_name", _maxSize + 1, _lengthString);
                // doit ï¿½tre remplacï¿½ par une implementation sur disque
                break;
        }
    }

    /**  ajoute un document au gestionnaire retourne le numï¿½ro du docuemnt*/
    public final int put(String d) {
        //msg("add this:"+d);
        int id = documentName.put(d);
        return id;
    }

    /**  cherche le numï¿½ro du document, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public final int get(String d) {
        return documentName.get(d);
    }

    /**  cherche le document associï¿½ ï¿½ un numï¿½ro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        return documentName.get(i);
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg("------------------------------------------------------------");
        msg("- DOCS TABLE STAT                                         -");
        msg("------------------------------------------------------------");
        msg("mode:" + updatingMode.name());
        msg("REFERENCE STRUCTURE:");
        documentName.printStatistic();
        msg("");
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        return documentName.getCount();
    }

    /**  enregistre la date pour le document i */
    public final void setDate(int i, long date) {
        // rien faire documentDate.set(i,date);
    }

    /**  demande  la date pour le document i */
    public final long getDate(int i) {
        return -1; // rien faire
    }

    /**  enregistre la taille pour le document i */
    public final void setSize(int i, int size) {
        // rien faire documentSize.set(i,size);
    }

    /**  demande  la taille pour le document i */
    public final int getSize(int i) {
        return -1; // rien faire
    }

    /**
     * enregistre la propiï¿½tï¿½ pour le document i
     * @param i numï¿½ro du document
     * @param properties propirï¿½tï¿½
     */
    public void setPropertie(int i, String properties) {
        // rien faire documentProperties.put(properties,i);
    }

    /**
     * ï¿½limine la propiï¿½tï¿½ pour le document i
     * @param i numï¿½ro du document
     * @param properties propirï¿½tï¿½
     */
    public void clearPropertie(int i, String properties) {
        // rien faire documentProperties.clear(properties,i);
    }

    /**
     * Retourne la taille pour le document i
     * @param i numï¿½ro du document
     * @param properties propriï¿½tï¿½
     * @return la valeur de cette propiï¿½tï¿½
     */
    public boolean getPropertie(int i, String properties) {
        return false;// rien faire documentProperties.get(properties,i);
    }

    /**  verifie si le document est ï¿½ indexer (nom diffï¿½rent, date diffï¿½rente */
    public final boolean IndexThisDocument(String fname, long date) {
//        msg("test:"+fname);
        if (dontIndexThisDocuments.check(fname)) {
            return false; // ne pas indexer
        }
        int id = get(fname);  // cherche si le document est dï¿½jï¿½ indexï¿½
        if (id == StringRepository.EMPTY) {
            return true; // un nouveau document
        }
        return false; // cas le document est identique,
    }

    /**  rend invalide le document i */
    public final void invalid(int i) {
// rien faire
    }

    /**  test si le document i  est valide */
    public final boolean isValid(int i) {
        return true;// rien faire !documentInvalid.get(i);
    }

    /**  le nombre de document valides*/
    public final int countValid() {
        return documentName.getCount();
    }

    /**  cherche toutes les documents possï¿½dant une propriï¿½tï¿½s*/
    public final SetOfBits satisfyThisProperty(String properties) {
        return null; // rien faire    
    }

    /**
     *  rï¿½cupï¿½re le dictionnaire de propriï¿½tï¿½s
     * @return liste des propriï¿½tï¿½s actives
     */
    public List<String> getDictionnary() {
        return null;
    }

    /**
     *  rï¿½cupï¿½re le dictionnaire de propriï¿½tï¿½s ayant un certain prï¿½fix (COLECT., LANG.)
     * @param prefix prï¿½fixe des propriï¿½tï¿½s
     * @return liste des propriï¿½tï¿½s actives
     */
    public List<String> getDictionnary(String prefix) {
        return null;
    }

    /**
     * propage l'invaliditï¿½ des documents dans les propriï¿½tï¿½s
     */
    public void propagateInvalididy() {
        // pour ï¿½tre conforme ï¿½ l'interface
        // pas de protection spï¿½ciale pour la concurrence
    }
}
