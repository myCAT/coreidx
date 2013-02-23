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

/**
 *
 * classe pour la manipulation et l'extraction des propri�t�s.
 *
 * 
 *
 * classe pour la manipulation et l'extraction des propri�t�s. 
 * Cette classe est modifi�e en fonction des besoins du client et de la structure des documents
 *
 */
public class KeepProperties {

    /**
     * d�termine la propri�t� de language d'un document.
     * @param fname nom du fichier
     * @param idDoc document
     * @param PM gestionnaire de propri�t�
     */
    public static void keepLanguageOfDoc(String fname, int idDoc, PropertiesManager PM) {
        //msg("keepLanguageOfDoc:"+fname);
        if (fname.indexOf("_EN.") != -1) {
            PM.put("_EN", idDoc);
            return;
        }
        if (fname.indexOf("_FR.") != -1) {
            PM.put("_FR", idDoc);
            return;
        }
        if (fname.indexOf("_FR2.") != -1) {
            PM.put("_FR2", idDoc);
            return;
        }
        if (fname.indexOf("_DE.") != -1) {
            PM.put("_DE", idDoc);
            return;
        }
        if (fname.indexOf("_DU.") != -1) {
            PM.put("_DU", idDoc);
            return;
        }
        if (fname.indexOf("_IT.") != -1) {
            PM.put("_IT", idDoc);
            return;
        }
        if (fname.indexOf("_PT.") != -1) {
            PM.put("_PT", idDoc);
            return;
        }
        if (fname.indexOf("_ES.") != -1) {
            PM.put("_ES", idDoc);
            return;
        }
        if (fname.indexOf("_SW.") != -1) {
            PM.put("_SW", idDoc);
            return;
        }
        if (fname.indexOf("_RU.") != -1) {
            PM.put("_RU", idDoc);
            return;
        }
        if (fname.indexOf("_CH.") != -1) {
            PM.put("_CH", idDoc);
            return;
        }
        if (fname.indexOf("_AR.") != -1) {
            PM.put("_AR", idDoc);
            return;
        }
        if (fname.indexOf("_ZH.") != -1) {
            PM.put("_ZH", idDoc);
            return;
        }
        PM.put("_OTHER", idDoc);
    }

    /**
     * d�termine la ou les collections d'un document.
     * @param fname nom du fichier
     * @param idDoc document
     * @param PM gestionnaire de propri�t�
     */
    public static void keepCollectionOfDoc(String fname, int idDoc, PropertiesManager PM) {
        //msg("keepLanguageOfDoc:"+fname);
        int i = fname.indexOf("/");
        while (i != -1) {
            //msg(fname.substring(0,i));
            PM.put(fname.substring(0, i), idDoc);
            i = fname.indexOf("/", i + 1);
        }
    }
}
