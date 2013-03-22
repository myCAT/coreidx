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
package org.olanto.idxvli.server;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import org.olanto.conman.server.ContentService;
import static org.olanto.conman.server.GetContentService.getServiceCM;
import static org.olanto.idxvli.IdxConstant.*;
import org.olanto.idxvli.IdxInit;
import org.olanto.idxvli.IdxStructure;
import org.olanto.idxvli.doc.PropertiesList;
import org.olanto.idxvli.knn.KNNManager;
import org.olanto.idxvli.knn.KNNResult;
import org.olanto.idxvli.ref.UploadedFile;
import org.olanto.idxvli.util.SetOfBits;
import static org.olanto.util.Messages.error;
import static org.olanto.util.Messages.msg;
import org.olanto.util.TimerNano;
import org.olanto.idxvli.ref.UtilsFiles;

/**
 * service mycat.
 *
 *
 *
 * <p>
 * *  <pre>
 *  concurrence:
 *   - // pour les lecteurs
 *   - ï¿½crivain en exclusion avec tous
 *  doit être le point d'accï¿½s pour toutes les structures utilisï¿½es !
 * </pre>
 *
 */
public class Server_MyCat extends UnicastRemoteObject implements IndexService_MyCat {

    private IdxStructure id;
    private KNNManager KNN;
    private ContentService cs;  // service associï¿½
    private String modeForRestart;
    private IdxInit clientForRestart;

    public Server_MyCat() throws RemoteException {
        super();
    }

    @Override
    public String getInformation() throws RemoteException {
        return "this service is alive ... :IndexService_BASIC";
    }
    /**
     * opï¿½ration sur documentName verrous
     * ------------------------------------------
     */
    private final ReentrantReadWriteLock serverRW = new ReentrantReadWriteLock();
    private final Lock serverR = serverRW.readLock();
    private final Lock serverW = serverRW.writeLock();

    @Override
    public synchronized String getStatistic() throws RemoteException {
        if (id != null) {
            return id.Statistic.getGlobal();
        }
        return "index is not opened";
    }

    @Override
    public void startANewOne(IdxInit client) throws RemoteException {
        serverW.lock();
        try {
            if (id != null) {
                error("already init: -> bad usage");
            } else {
                id = new IdxStructure("NEW", client);
                id.Statistic.global();
                id.close();

                msg("wait a little and start server ");

            }
        } finally {
            serverW.unlock();
        }

    }

    @Override
    public void getAndInit(IdxInit client, String mode, boolean OpenCM) throws RemoteException {
        serverW.lock();
        modeForRestart = mode;
        clientForRestart = client;
        try {
            if (id != null) {
                error("already init: getAndInit");
            } else {

                msg("current indexer is opening ...");

                id = new IdxStructure(mode, client);
                if (!mode.equals("NEW")) {
                    id.Statistic.global();
                }
                msg("current indexer is opened ...");
                if (OpenCM) {
                    cs = getServiceCM("rmi://localhost/CM_CLEAN");
                } else {
                    msg("No Content Manager in this configuration (CM_CLEAN)");
//            if (mfl!=null){
//                // init mfl
//                documents=new MFL(id);
//                documents.openManyFile(mfl, 1, IdxConstant.IDX_MFL_ENCODING);
//                msg("IndexService_BASIC:open mfl OK");
//            }
                    // init knn
//            Timer t2=new Timer("init KNN");
//            KNN=new TFxIDF_ONE();
//            KNN.initialize(id,    // Indexeur
//                    5,     // Min occurence d'un mot dans le corpus (nbr de documents)
//                    50,    // Max en o/oo d'apparition dans le corpus (par mille!)
//                    true,   // montre les dï¿½tails
//                    1,        // formule IDF (1,2)
//                    1       // formule TF (1,2,3) toujours 1
//                    );
//            t2.stop();
//            msg("IndexService_BASIC:open knn OK");
                }
            }
        } finally {
            serverW.unlock();
        }
    }

    @Override
    public void saveAndRestart() throws RemoteException {
        serverW.lock();
        try {
            if (id == null) {
                error("server is not opened");
            } else {
                msg("-- restart Index manager");
                msg("current Index manager is closing ...");
                id.Statistic.global();
                id.close();
                msg("current Index manager is closed");
                msg("current Index manager is opening ...");
                id = new IdxStructure(modeForRestart, clientForRestart);
                id.Statistic.global();
                msg("current Index manager is opened");
            }
        } finally {
            serverW.unlock();
        }
    }

    @Override
    public void quit() throws RemoteException {
        if (id == null) {
            error("already close: quit");
        } else {
            serverW.lock();
            try {

                msg("current indexer is closing ...");
                id.Statistic.global();
                id.close();
                msg("current indexer is closed");
                msg("server must be stopped  & restarted ...");
                id = null;
            } finally {
                //serverW.unlock(); ne rend pas le verrou, accepte seulement le stop
            }
        }
    }

    @Override
    public void stop() throws RemoteException {
        msg("kill the process & all other service running in the same process ...");
        try {
            System.exit(0);
        } finally {
            serverW.unlock(); // seulement pour la symétrie ...
        }
    }

    @Override
    public void flush() throws RemoteException {  // use indexdir it's better
        msg("current indexer is flusing ...");
        id.flushIndexDoc();
        msg("current indexer is flushed");
    }

    @Override
    public void showFullIndex() throws RemoteException { // use indexdir it's better
        serverW.lock();
        try {
            msg("current indexer is upgrading ...");
            id.showFullIndex();
            msg("current indexer is upgraded");
        } finally {
            serverW.unlock();
        }
    }

    // ces deux mï¿½thodes doivent ï¿½tre en exclusion mutuelle
    @Override
    public synchronized void indexdir(String path) throws RemoteException {
        msg("current indexer is indexing ...");
        id.indexdir(path);
        id.flushIndexDoc(); // décharge les caches

        msg("current indexer is waiting for more documents...");
    }

    @Override
    public void indexThisContent(String path, String content) throws RemoteException { // not used in myCat
        id.indexThisContent(path, content);
    }

    @Override
    public void indexThisContent(String path, String content, String lang) throws RemoteException { // not used in myCat
        id.indexThisContent(path, content);
        int n = id.getIntForDocument(path);
        id.docstable.setPropertie(n, "LANG." + lang);
    }

    @Override
    public void indexThisContent(String path, String content, String lang, String[] collection) throws RemoteException { // not used in myCat
        indexThisContent(path, content, lang);
        if (collection != null) {
            int n = id.getIntForDocument(path);
            for (int i = 0; i < collection.length; i++) {
                id.docstable.setPropertie(n, "COLLECTION." + collection[i]);
            }
        }
    }

    @Override
    public QLResult evalQL(String request) throws RemoteException {
        serverR.lock();
        try {
            TimerNano time = new TimerNano(request, true);
            return new QLResult(id.executeRequest(request), time.stop(true) / 1000);
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public QLResultAndRank evalQLMore(String request) throws RemoteException {
        serverR.lock();
        try {
            TimerNano time = new TimerNano(request, true);
            QLResultAndRank res = id.executeRequestMore(request);
            res.duration = time.stop(true) / 1000;
            return res;
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public QLResultNice browseNice(String request, String langS, int start, int size, String[] collections, String order, boolean onlyOnFileName) throws RemoteException {
        serverR.lock();
        try {
            return id.browseNice(request.replace("/", "¦"), langS, start, size, collections, order, onlyOnFileName);
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public QLResultNice evalQLNice(String request, int start, int size) throws RemoteException {
        serverR.lock();
        try {
            return id.evalQLNice(cs, request, start, size,false);

        } finally {
            serverR.unlock();
        }
    }

    /**
     * used for myCAT query
     */
    @Override
    public QLResultNice evalQLNice(String request, int start, int size, String order, boolean exact, boolean orderbyocc) throws RemoteException {
        serverR.lock();
        try {
            if (exact) {
              QLResultNice res = id.evalQLNice(cs, request, start, Integer.MAX_VALUE, true); // pas de limite
                res.orderBy(id, order);
                res.checkExact(id, size);
                return res;
            } else {  // fuzzy search
                QLResultNice res = id.evalQLNice(cs, request, start, size,false);
                res.orderBy(id, order);
                return res;
            }
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public QLResultNice evalQLNice(String request, String properties, int start, int size) throws RemoteException {
        return null; // not implemented
    }

    @Override
    public QLResultNice evalQLNice(String request, String properties, String profile, int start, int size) throws RemoteException {
        return null; // not implemented
    }

    @Override
    public QLResult evalQL(String lang, String request) throws RemoteException {
        return null; // not implemented
    }

    @Override
    public String[] getDocName(int[] docList) throws RemoteException {
        serverR.lock();
        try {
            String[] res = new String[docList.length];
            for (int i = 0; i < docList.length; i++) {
                res[i] = id.getFileNameForDocument(docList[i]);
            }
            return res;
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public boolean docIsValid(int doc) throws RemoteException {
        serverR.lock();
        try {
            return id.docstable.isValid(doc);
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public String getDoc(int docId) throws RemoteException {
        serverR.lock();
        try {
            msg("not implemented");
            return "no text!";
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public int getDocId(String docName) throws RemoteException {
        serverR.lock();
        try {
            return id.getIntForDocument(docName);
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public KNNResult KNNForDoc(int doc, int N) throws RemoteException {
        serverR.lock();
        try {
            return KNN.KNNForDoc(doc, N);
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public String getDOC_ENCODING() throws RemoteException {
        serverR.lock();
        try {
            return DOC_ENCODING;
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public void exportEntry(long min, long max) throws RemoteException {
        serverR.lock();
        try {
            id.exportEntry(min, max);
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public String[] ExpandTerm(String term) throws RemoteException {
        //   System.out.println("vocabulary:"+id.wordExpander.getTarget());
        if (WORD_EXPANSION) {
            return id.wordExpander.getExpand(term, WORD_MAX_EXPANSION);
        }
        return null;
    }

    @Override
    public PropertiesList getDictionnary() throws RemoteException {
        serverR.lock();
        try {
            String[] res;
            List<String> p = id.getDictionnary();
            if (p != null) {
                int l = p.size();
                res = new String[l];
                for (int i = 0; i < l; i++) {
                    res[i] = p.get(i);
                }
                Arrays.sort(res);    // tri des collections
                return new PropertiesList(res);
            } else {
                return null;
            }
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public PropertiesList getDictionnary(String prefix) throws RemoteException {
        serverR.lock();
        try {
            String[] res;
            List<String> p = id.getDictionnary(prefix);
            if (p != null) {
                int l = p.size();
                res = new String[l];
                for (int i = 0; i < l; i++) {
                    res[i] = p.get(i);
                }
                Arrays.sort(res);    // tri des collections          
                return new PropertiesList(res);
            } else {
                return null;
            }
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public String getLanguage(String txt) throws RemoteException {
        serverR.lock();
        try {
            return id.getLanguage(txt);
        } finally {
            serverR.unlock();
        }
    }

    @Override
    public String[] getCollection(String txt) throws RemoteException {
        serverR.lock();
        try {
            return id.getCollection(txt);
        } finally {
            serverR.unlock();
        }
    }

    /**
     * dï¿½termine le chemin du corpus ï¿½ convertir
     *
     * @return path
     * @throws java.rmi.RemoteException
     */
    @Override
    public String getROOT_CORPUS_SOURCE() throws RemoteException {
        return ROOT_CORPUS_SOURCE;
    }

    /**
     * dï¿½termine le chemin du corpus ï¿½ indexer
     *
     * @return path
     * @throws java.rmi.RemoteException
     */
    @Override
    public String getROOT_CORPUS_TXT() throws RemoteException {
        return ROOT_CORPUS_TXT;
    }

    /**
     * dï¿½termine la taille du corpus
     *
     * @return dernier numï¿½ro de document enregistrï¿½
     * @throws java.rmi.RemoteException
     */
    @Override
    public int getSize() throws RemoteException {
        return id.lastUpdatedDoc;
    }

    /**
     * pour chercher les noms d'un document
     *
     * @param docid identifiant
     * @return nom du document
     * @throws java.rmi.RemoteException
     */
    @Override
    public String getDocName(int docid) throws RemoteException {
        return id.getFileNameForDocument(docid);
    }

    @Override
    public SetOfBits satisfyThisProperty(String properties) throws RemoteException {
        return id.satisfyThisProperty(properties);
    }

    @Override
    public void clearThisProperty(String properties) throws RemoteException {
        id.clearThisProperty(properties);
    }

    @Override
    public void setDocumentPropertie(int docID, String properties) throws RemoteException {
        id.setDocumentPropertie(docID, properties);
    }

    @Override
    public void clearDocumentPropertie(int docID, String properties) throws RemoteException {
        id.clearDocumentPropertie(docID, properties);
    }

    @Override
    public String[] getStopWords() throws RemoteException {
        return id.getStopWords();
    }

    @Override
    public String[] getCorpusLanguages() throws RemoteException {
        Pattern p = Pattern.compile("\\s");  // les fins de mots

        String[] res = p.split(ROOT_CORPUS_LANG);
        return res;
    }

    @Override
    public String getOriginalUrl(String d) throws RemoteException {
        return ORIGINAL_DEFINITION.getURL(d);
    }

    @Override
    public String getOriginalPath(String d) throws RemoteException {
        return ORIGINAL_DEFINITION.getPath(d);
    }

    @Override
    public String getOriginalZipName(String d) throws RemoteException {
        return ORIGINAL_DEFINITION.getSaveZipName(d);
    }

    @Override
    public byte[] getByte(String d) throws RemoteException {
        File f = new File(d);
        if (f.exists()) {
            System.out.println("getByte from:" + d);
            return UtilsFiles.file2byte(f);
        }
        return null;
    }

    @Override
    public void executePreprocessing(String[] args) throws RemoteException {
        PRE_PROCESSING.main(args);
    }

    @Override
    public HashMap<String, String> getClientProperties() throws RemoteException {
        return null;
    }

    @Override
    public REFResultNice getReferences(UploadedFile upfile, int limit, String source, String target, String[] selectedCollection) throws RemoteException {
//        msg("Getting references from file: " + upfile.getFileName() + "\n" + upfile.getContentString());
        if (upfile == null) {// fix result for developpment
            return null;
        } else {
            return id.getReferences(upfile, limit, source, target, selectedCollection);
        }
    }

    @Override
    public String getHtmlReferences(UploadedFile upfile, int limit, String source, String target, String[] selectedCollection) throws RemoteException {
//        msg("Getting references from file: " + upfile.getFileName() + "\n" + upfile.getContentString());
        if (upfile == null) {// fix result for developpment
            return null;
        } else {
            return id.getReferences(upfile, limit, source, target, selectedCollection).htmlref;
        }
    }

    @Override
    public String createTemp(String FileName, String Content) throws RemoteException {
        return UtilsFiles.String2File(FileName, Content, TEMP_FOLDER);
    }

    @Override
    public byte[] getTemp(String FileName) throws RemoteException {
        return UtilsFiles.file2byte(new File(TEMP_FOLDER + "/" + FileName));
    }
}
