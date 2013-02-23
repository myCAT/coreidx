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

package org.olanto.propertie;

import org.olanto.dtk.*;

/**
 * Permet de checher les propriétés associés à des objets.
 * <p>
 * Par exemple, les propriétés de langue ou de collection associées à des documents
 *
 * 
 *
 * */
public class Marker implements MarkerManager {

    static DetectLang detectLang;
    static CollectionRules collections;

    public Marker(String rootlangfile, String collectClassFile) {
        initLanguageDetector(rootlangfile);
        collections = new CollectionRules(collectClassFile);
    }

    private void initLanguageDetector(String fileroot) {

        detectLang = new DetectLang(4);

        detectLang.trainLang("FRE", fileroot + "French.txt");
        detectLang.trainLang("ENG", fileroot + "English.txt");
        detectLang.trainLang("GER", fileroot + "German.txt");
        detectLang.trainLang("SPA", fileroot + "Spanish.txt");
//        msg("initialization of Language assign for : FRE; ENG; GER; SPA");


    }

    /**
     *  retourne la langue d'un document
     * @param txt �chantillon du document (1000 caract�res par exemple)
     * @return code de la langue (FR,EN,...)
     */
    public String getLanguage(String txt) {
        return detectLang.langOfText(txt);
    }

    /**
     *  retourne la collection d'un document
     * @param name nom du document (url, ...)
     * @return code de la collection (INFORMATIQUE,MEDECINE,...)
     */
    public String[] getCollectionFromName(String name) {
        return collections.eval(name);

    }
}
