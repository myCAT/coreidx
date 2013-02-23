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

package org.olanto.conman;

import java.util.*;
import org.olanto.idxvli.util.*;
import org.olanto.idxvli.doc.*;
import static org.olanto.util.Messages.*;
import static org.olanto.conman.ContentConstant.*;
import org.olanto.idxvli.*;
import static org.olanto.idxvli.IdxEnum.*;
import java.util.regex.*;

/**
 * Une classe pour construire un gestionnaire de contenu.
 *
 * 
 */
public class ContentStructure {

    /** dictionnaire de documents (document->indice) (indice->document)*/
    public DocumentManager docstable;
    /** la structure comprend un module de statistique*/
    public ContentStatistic Statistic;
    /** la structure comprend un module de gestion des IO*/
    public ContentIO IO;
    /** la structure comprend un module de gestion des IO*/
    public ContentManager CM;
    /** indice du dernier document indexï¿½. attention ï¿½ cela qui est vrai dans l'ancien systï¿½me, Donc les indices partent de for (inti i=1;i < lastdoc ...)*/
    public int lastdoc = 0;
    /** flag indiquant qu'il n'y pas de document invalide (permet de sauter les filtres */
    public static boolean ZERO_INVALID_DOC = false;
    /** sliper de texte */
    Pattern p = Pattern.compile(SEPARATOR);

    /** Crï¿½e une structure d'indexation
     */
    public ContentStructure() {
    }

    /** Crï¿½e une structure d'indexation . Permet de dï¿½composer l'initalisation.
     * @param _mode (QUERY,INCREMENTAL,DIFFERENTIAL,)
     */
    public ContentStructure(String _mode) {
        MODE_IDX = IdxMode.valueOf(_mode);
    }

    /**
     * crï¿½ation d'une structure d'indexation. Permet de dï¿½composer l'initalisation.
     * @param _mode (NEW_INDEXATION)
     * @param name  (pas utilisï¿½)
     *
     */
    public ContentStructure(String _mode, String name) {
        if (!_mode.equals("NEW")) {
            error_fatal("must be in mode NEW_INDEXATION");
        }
        MODE_IDX = IdxMode.valueOf(_mode);
    }

    /** Crï¿½e une structure d'indexation et initialise la.
     * @param _mode (NEW,QUERY,INCREMENTAL,DIFFERENTIAL,)
     * @param client la configuration client
     */
    public ContentStructure(String _mode, ContentInit client) {
        MODE_IDX = IdxMode.valueOf(_mode);
        createComponent(client);
        loadContentManager();
    }

    /* service IO */
    public final void updateLastdoc() {
        lastdoc = docstable.getCount();
    }

    public final void loadContentManager() {
        IO.loadContentManager();
    }

    /** sauvegarder les donnï¿½es encore dans les caches et ferme l'un index
     */
    public final void close() {
        IO.saveContentManager();
    }

    /** rï¿½cupï¿½re le nom de la racine de la structure d'indexation
     * @return dossier racine
     */
    public final String getIdxRootName() {
        return COMMON_ROOT;
    }

    /** rï¿½cupï¿½re le nom de l'index actuellement actif
     * @return sac nom de fichier
     */
    public final String getIdxName() {
        return ContentConstant.currentf;
    }

    /** sauve le contenu
     * @param n terme
     */
    public byte[] loadContent(int n) {
        return IO.loadContent(n);
    }

    /** ajoute un contenu String.
     * @param docName identifiant
     * @param content contenu
     */
    synchronized public void addContent(String docName, String content, String lang, String collection) {
        try {
            byte[] b = content.getBytes(CONTENT_ENCODING);
            long fdate = System.currentTimeMillis();
            CM.addContent(docName, fdate, b, lang, "STRING", collection);

        } catch (Exception e) {
            error("addContent", e);
        }
    }

    /** ajoute un URL
     * @param url identifiant
     */
    synchronized public void addURL(String url) {
        CM.addURL(url);
    }

    /** sauver un contenu
     * @param docID identifiant
     * @param content contenu
     */
    synchronized public void saveContent(int docID, byte[] content) {
        IO.saveContent(docID, ContentManager.compress(content), content.length);
    }

    /** sauver un contenu
     * @param docID identifiant
     * @param content contenu
     */
    synchronized public void saveContent(int docID, String content, String type) {
        try {
            byte[] b = content.getBytes(CONTENT_ENCODING);
            IO.saveContent(docID, ContentManager.compress(b), b.length);
            docstable.setPropertie(docID, "TYPE." + type);
        } catch (Exception e) {
            error("saveContent string", e);
        }
    }

    /** sauver un contenu
     * @param docID identifiant
     * @param content contenu
     */
    synchronized public void saveContent(int docID, byte[] content, String type) {
        try {
            IO.saveContent(docID, ContentManager.compress(content), content.length);
            docstable.setPropertie(docID, "TYPE." + type);
        } catch (Exception e) {
            error("saveContent binary", e);
        }
    }

    /** rï¿½cuper un contenu String.
     * @param docName identifiant
     * @return  contenu
     */
    synchronized public String getStringContent(String docName) {
        return CM.getStringContent(docName);
    }

    /** rï¿½cuper un contenu Byte.
     * @param docName identifiant
     * @return  contenu
     */
    synchronized public byte[] getByteContent(String docName) {
        return CM.getByteContent(docName);
    }

    /** rï¿½cuper un contenu String.
     * @param docID identifiant
     * @return  contenu
     */
    synchronized public String getStringContent(int docID) {
        return CM.getStringContent(docID);
    }

    /** rï¿½cuper un contenu Byte.
     * @param docID identifiant
     * @return  contenu
     */
    synchronized public byte[] getByteContent(int docID) {
        return CM.getByteContent(docID);
    }

    /** rï¿½cupï¿½re un contenu type String sur un intervalle donnï¿½.
     * @param docID identifiant
     * @param from debut
     * @param to fin
     */
    public String getStringContent(int docID, int from, int to) {
        return CM.getStringContent(docID, from, to);
    }

    /** rï¿½cupï¿½re le contenu d'un rï¿½pertoire.
     * @param pathName rï¿½pertoire
     */
    synchronized public void getFromDirectory(String pathName) {
        CM.getFromDirectory(pathName, "NODEF", "NODEF", "ISO-8859-1");
    }

    /** rï¿½cuï¿½re le contenu d'un rï¿½pertoire.
     * @param pathName rï¿½pertoire
     * @param language langage de la collection
     * @param collection nom de la collection
     * @param txt_encoding encodage des textes
     */
    synchronized public void getFromDirectory(String pathName, String language, String collection, String txt_encoding) {
        CM.getFromDirectory(pathName, language, collection, txt_encoding);
    }

    /** longeur d'un document.
     * @param d document
     * @return nbr de termes indexï¿½s
     */
    public int getLengthOfD(int d) {
        return docstable.getSize(d);
    }

    /** distance de kolmogorov entre deux documents.
     * @param d1 document
     * @param d2 document
     * @return distance
     */
    public double distOfKolmogorov(int d1, int d2, boolean bzip2) {
        return CM.distOfKolmogorov(d1, d2, bzip2);
    }

    public double distOfKolmogorov(double kd1, double kd2, String Sd1, String Sd2, boolean bzip2) {
        return CM.distOfKolmogorov(kd1, kd2, Sd1, Sd2, bzip2);
    }

    public double kdlength(String Sd1, boolean bzip2) {
        return CM.kdlength(Sd1, bzip2);
    }

    /** nom du fichier du document i
     * @param d iï¿½me document
     * @return nom de fichier
     */
    public String getFileNameForDocument(int d) {
        return docstable.get(d);
    }

    /** numï¿½ro du document ayant le libellï¿½ s
     * @param s nom du fichier
     * @return numï¿½ro du document (-1 = pas trouvï¿½)
     */
    public int getIntForDocument(String s) {
        return docstable.get(s);
    }

    /** tout les documents satisfaisants une propriï¿½tï¿½
     * @param properties nom de la propriï¿½tï¿½
     * @return set de bits
     */
    public SetOfBits satisfyThisProperty(String properties) {
        return docstable.satisfyThisProperty(properties);
    }

    /** dï¿½finir une  propriï¿½tï¿½ pour un document
     * @param docID identifiant
     * @param properties nom de la propriï¿½tï¿½
     */
    public void setDocumentPropertie(int docID, String properties) {
        docstable.setPropertie(docID, properties);

    }

    /** ï¿½liminer une  propriï¿½tï¿½ pour un document
     * @param docID identifiant
     * @param properties nom de la propriï¿½tï¿½
     */
    public void clearDocumentPropertie(int docID, String properties) {
        docstable.clearPropertie(docID, properties);

    }

    /**
     *  rï¿½cupï¿½re le dictionnaire de propriï¿½tï¿½s
     * @return liste des propriï¿½tï¿½s actives
     */
    public List<String> getDictionnary() {
        return docstable.getDictionnary();
    }

    /**
     *  rï¿½cupï¿½re le dictionnaire de propriï¿½tï¿½s ayant un certain prï¿½fix (COLECT., LANG.)
     * @param prefix prï¿½fixe des propriï¿½tï¿½s
     * @return liste des propriï¿½tï¿½s actives
     */
    public List<String> getDictionnary(String prefix) {
        return docstable.getDictionnary(prefix);
    }

    /** test si un document est indexable.
     * @param doc identifiant
     * @return si le document est indexable
     */
    public boolean isIndexable(int doc) {
        return docstable.getPropertie(doc, "TYPE.INDEXABLE") && !docstable.getPropertie(doc, "STATE.INDEXED");
    }

    /** dï¿½termine que le document est indexï¿½.
     * @param doc identifiant
     */
    public void setIndexed(int doc) {
        docstable.setPropertie(doc, "STATE.INDEXED");
    }

    /** dï¿½termine que le document n'est pas indexï¿½.
     * @param doc identifiant
     */
    public void clearIndexed(int doc) {
        docstable.clearPropertie(doc, "STATE.INDEXED");
    }

    public synchronized void setRefDoc(String docName, String refName, String title, String cleantxt) {
        try {

            String refdoc = refName + SEPARATOR + title + SEPARATOR + cleantxt;
            byte[] content = refdoc.getBytes(CONTENT_ENCODING);
            int idcontent = docstable.put(docName); // enregistre le numï¿½ro de la rï¿½fï¿½rence
            updateLastdoc();
            docstable.setPropertie(idcontent, "TYPE.REFDOC");
            docstable.setPropertie(idcontent, "TYPE.INDEXABLE");
            docstable.setPropertie(idcontent, "TYPE.STRING");
            IO.saveContent(idcontent, ContentManager.compress(content), content.length);
        } catch (Exception e) {
            error("setRefDoc", e);
        }
    }

//**** application ***** refname,title,context is not ok for this document 169007
//**** application ***** refname,title,context is not ok for this document 169007
//**** application ***** refname,title,context is not ok for this document 169027
//**** application ***** refname,title,context is not ok for this document 169027
//**** application ***** refname,title,context is not ok for this document 169036
//**** application ***** refname,title,context is not ok for this document 169036
//**** application ***** refname,title,context is not ok for this document 169048
//**** application ***** refname,title,context is not ok for this document 169048
//**** application ***** refname,title,context is not ok for this document 169062
//**** application ***** refname,title,context is not ok for this document 169062
//**** application ***** refname,title,context is not ok for this document 169099
//**** application ***** refname,title,context is not ok for this document 169099
//**** application ***** refname,title,context is not ok for this document 169217
//**** application ***** refname,title,context is not ok for this document 169217
//**** application ***** refname,title,context is not ok for this document 169310
//**** application ***** refname,title,context is not ok for this document 169310
    public String[] getRefDoc(String docName) {
        String[] res = new String[3];
        int id = this.getIntForDocument(docName);
        if (id == -1) {
            error("no title for this document(noref)" + docName);
            res[0] = "ref error";
            res[1] = "title error";
            res[2] = "context error";
            return res;
        }
        res = p.split(CM.getStringContent(id));
        if (res.length != 3) {
            error("refname,title,context is not ok for this document " + docName + " / " + res.length);
            res = new String[3];
            res[0] = "ref error";
            res[1] = "title error";
            res[2] = "context error";
            return res;
        }
        return res;
    }

    public String getRefName(String docName) {
        return getRefDoc(docName)[0];
    }

    public String getTitle(String docName) {
        return getRefDoc(docName)[1];
    }

    public String getCleanText(String docName) {
        return getRefDoc(docName)[2];
    }

    public String getCleanText(String docName, int from, int to) {
        boolean verbose = false;
        String allcontent = getCleanText(docName);
        if (verbose) {
            msg("getCleanText allcontent:" + 0 + "-" + allcontent.length());
        }
        String res = "no clue! for " + docName;
        if (from < allcontent.length()) {
            res = allcontent.substring(Math.max(0, from), Math.min(to, allcontent.length()));
        }
        return res;
    }

    /**
     * crï¿½e un composant d'indexation avec une racine pour les fichiers d'index
     */
    public void createComponent(ContentInit client) {
        // initialise les constantes et la configuration
        client.InitPermanent();
        client.InitConfiguration();
        IdxConstant.openLogger();
        IdxConstant.show();

        ContentConstant.show();
        // create reference
        Statistic = new ContentStatistic(this);
        IO = new ContentIO(this);
        CM = new ContentManager(this);

        if (MODE_IDX == IdxMode.NEW || MODE_IDX == IdxMode.INCREMENTAL || MODE_IDX == IdxMode.DIFFERENTIAL) {
            InitIndexer();
        }
        if (MODE_IDX == IdxMode.QUERY) {
            InitQuery();
        }
    }

    void InitQuery() {
        msg("init mode:" + MODE_IDX.name());
    }

    void InitIndexer() {
        msg("docdmax:" + DOC_MAX);
        msg("init mode:" + MODE_IDX.name());

    }
}
