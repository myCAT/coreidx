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

package org.olanto.conman.server;

import java.rmi.*;
import org.olanto.conman.*;
import org.olanto.idxvli.util.*;
import org.olanto.idxvli.doc.*;

/**
 *  Facade pour les services des contents manager en modeRMI (ces services peuvent
 * être augmentés en publiant des méthodes de ContentStructure)
 * 
 */
public interface ContentService extends Remote {

    /**
     * Pour demander des informations sur le serveur (pour v�rifier la connexion)
     * @return nom du serveur ...
     * @throws java.rmi.RemoteException
     */
    public String getInformation() throws RemoteException;

    /**
     * Pour demander la construction de nouvelles structures pour le conteneur 
     * (dans le cas d'une r�initialisation compl�te)
     * @param client configuration de la structure
     * @throws java.rmi.RemoteException
     */
    public void startANewOne(ContentInit client) throws RemoteException;

    /**
     * Pour demander l'ouverture d'un conteneur existant
     * @param client configuration de la structure
     * @param mode (QUERY,INCREMENTAL,DIFFERENTIAL)
     * @throws java.rmi.RemoteException
     */
    public void getAndInit(ContentInit client, String mode) throws RemoteException;

    /**
     * Pour obtenir les statisques (nb doc, %compression, ...) du conteneur
     * @return r�sultat des stat
     * @throws java.rmi.RemoteException
     */
    public String getStatistic() throws RemoteException;

    /**
     * pour stopper le serveur
     * @throws java.rmi.RemoteException
     */
    public void quit() throws RemoteException;

    /**
     * pour ajouter un nom de document sans son contene (voir crawler)
     * @param url identifiant du document
     * @throws java.rmi.RemoteException
     */
    public void addURL(String url) throws RemoteException;

    /**
     * pour ajouter un document dans le conteneur
     * @param docName identifiant du document
     * @param content contenu du document (textuel)
     * @param lang langue du document
     * @param collection attribut du document
     * @throws java.rmi.RemoteException
     */
    public void addContent(String docName, String content, String lang, String collection) throws RemoteException;

    /**
     *  pour ajouter/remplacer le contenu d'un document d�j� connu
     * @param docID id interne du document
     * @param content contenu du document (textuel)
     * @param type (html,java,txt, ...)
     * @throws java.rmi.RemoteException
     */
    public void saveContent(int docID, String content, String type) throws RemoteException;

    /**
     * pour ajouter/remplacer le contenu d'un document d�j� connu
     * @param docID id interne du document
     * @param content contenu du document (binary)
     * @param type (pdf,doc,ppt, ...)
     * @throws java.rmi.RemoteException
     */
    public void saveContent(int docID, byte[] content, String type) throws RemoteException;

    /**
     * pour ajouter un ensemble de documents depuis un r�pertoire
     * @param path chemin du r�pertoire
     * @throws java.rmi.RemoteException
     */
    public void getFromDirectory(String path) throws RemoteException;

    /**
     * pour r�cup�rer le nom du document associ� � un id
     * @param doc id du document
     * @return nom du document
     * @throws java.rmi.RemoteException
     */
    public String getDocName(int doc) throws RemoteException;

    /**
     * pour savoir si le document est valide (pas supprim�)
     * @param doc id du document
     * @return false=pas valide true=valide
     * @throws java.rmi.RemoteException
     */
    public boolean docIsValid(int doc) throws RemoteException;

    /**
     * pour r�cup�rer le contenu du document associ� � un id
     * @param docId id
     * @return contenu (textuel)
     * @throws java.rmi.RemoteException
     */
    public String getDoc(int docId) throws RemoteException;

    /**
     * pour r�cup�rer le contenu du document associ� � un nom de document
     * @param docName nom de document
     * @return contenu (textuel)
     * @throws java.rmi.RemoteException
     */
    public String getDoc(String docName) throws RemoteException;

    /**
     * pour r�cup�rer le contenu du document associ� � un id
     * @param docId id
     * @return contenu (binary)
     * @throws java.rmi.RemoteException
     */
    public byte[] getBin(int docId) throws RemoteException;

    /**
     * pour r�cup�rer le contenu du document associ� � un nom de document
     * @param docName nom de document
     * @return contenu (binary)
     * @throws java.rmi.RemoteException
     */
    public byte[] getBin(String docName) throws RemoteException;

    /**
     * pour extraire une section du contenu d'un document associ� � un id
     * @param docId id
     * @param from d�but de la section
     * @param to fin de la section
     * @return extraction (textuelle)
     * @throws java.rmi.RemoteException
     */
    public String getPartOfDoc(int docId, int from, int to) throws RemoteException;

    /**
     * pour r�cup�rer l'id document associ� � un nom de document
     * @param docName nom de document
     * @return id
     * @throws java.rmi.RemoteException
     */
    public int getDocId(String docName) throws RemoteException;

    /**
     * pour r�cup�rer le nombre de documents du conteneur
     * @return id
     * @throws java.rmi.RemoteException
     */
    public int getSize() throws RemoteException;

    /**
     * pour tester si un document est complet (valide avec un contenu et pas trait�)
     * @param doc id
     * @return false=pas traitable true=traitable
     * @throws java.rmi.RemoteException
     */
    public boolean isIndexable(int doc) throws RemoteException;

    /**
     * pour indiquer qu'un document est trait�
     * @param doc id
     * @throws java.rmi.RemoteException
     */
    public void setIndexed(int doc) throws RemoteException;

    /**
     * pour r�cup�rer l'ensemble des documents satisfaisant 
     * une propri�t� (lang, collection, state, etc)
     * @param properties nom de la propi�t�
     * @return vector de bits
     * @throws java.rmi.RemoteException
     */
    public SetOfBits satisfyThisProperty(String properties) throws RemoteException;

    /**
     * pour associer une propri�t� � un document
     * @param docID id
     * @param properties nom de la propi�t�
     * @throws java.rmi.RemoteException
     */
    public void setDocumentPropertie(int docID, String properties) throws RemoteException;

    /**
     * pour enlever une propri�t� � un document
     * @param docID id
     * @param properties nom de la propi�t�
     * @throws java.rmi.RemoteException
     */
    public void clearDocumentPropertie(int docID, String properties) throws RemoteException;

    /**
     *  r�cup�re le dictionnaire de propri�t�s actives du conteneur
     * @return liste des propri�t�s actives
     */
    public PropertiesList getDictionnary() throws RemoteException;

    /**
     *  pour r�cup�rer le dictionnaire de propri�t�s pour un groupe de propri�t� (COLECT., LANG.)
     * @param prefix pr�fixe du groupe des propri�t�s
     * @return liste des propri�t�s actives
     */
    public PropertiesList getDictionnary(String prefix) throws RemoteException;

    /**
     * pour ajouter un contenu structur� dans le conteneur (voir mode CLEAN)
     * @param docName nom du document
     * @param refName r�f�rence externe du document (url, path)
     * @param title titre du document
     * @param cleantxt texte "nettoy�" du document
     * @throws java.rmi.RemoteException
     */
    public void setRefDoc(String docName, String refName, String title, String cleantxt) throws RemoteException;

    /**
     * pour r�cup�rer la r�f�rence externe d'un contenu structur�
     * @param docName id
     * @return la r�f�rence externe
     * @throws java.rmi.RemoteException
     */
    public String getRefName(String docName) throws RemoteException;

    /**
     * pour r�cup�rer le titre d'un contenu structur�
     * @param docName id
     * @return le titre
     * @throws java.rmi.RemoteException
     */
    public String getTitle(String docName) throws RemoteException;

    /**
     * pour r�cup�rer le titre d'un contenu structur�
     * @param docName id
     * @return le titre
     * @throws java.rmi.RemoteException
     */
    public String getCleanText(String docName) throws RemoteException;

    /**
     * pour r�cup�rer le texte d'un contenu structur�
     * @param docName id
     * @return le texte
     * @throws java.rmi.RemoteException
     */
    public String getCleanText(String docName, int from, int to) throws RemoteException;
}
