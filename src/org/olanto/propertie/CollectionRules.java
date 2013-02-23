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

import java.util.*;
import java.io.*;
import static org.olanto.util.Messages.*;
import java.util.regex.*;

/**pour stoker un système de règle pour les collections
 *
 * 
 *
 *
 * 
 */
public class CollectionRules {

    ArrayList<String[]> collections;
    String collectionfilename;
    private static Pattern p;
    private static String SEPARATOR = "\\s";

    /** Creates a new instance of Rule */
    public CollectionRules(String fname) {
        collectionfilename = fname;
        collections = new ArrayList<String[]>();
        p = Pattern.compile(SEPARATOR);
        msg("init collection rules from file : " + fname);
        loadRules();
    }

    private void loadRules() {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(collectionfilename), "ISO-8859-1");
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            int count = 0;
            while (w != null) {
                count++;
                msg("line " + count + " : " + w);
                if (!w.startsWith("//")) {// pas un commentaire
                    String[] items = p.split(w);
                    if (items.length >= 2) {
                        collections.add(items);
                    } else {
                        msg("ERROR in line " + count + " : " + w);
                    }
                }
                w = in.readLine();
            }
        } catch (Exception e) {
            error("IO error in loadRules", e);
        }
    }

    public String[] eval(String s) {
        //msg(s);
        for (int i = 0; i < collections.size(); i++) {
            String[] items = collections.get(i);
            if (s.contains(items[0])) {
                //msg("rule :"+items[0]);
                String[] res = new String[items.length - 1];
                for (int j = 0; j < res.length; j++) {
                    res[j] = items[j + 1];
                }
                return res;
            }
        }
        return null;
    }

    public static void main(String[] args) { // pour les tests
        CollectionRules ar = new CollectionRules("C:/JG/VLI_RW/data/urlcollection.txt");
        showVector(ar.eval("http://cui.unige.ch/index.html"));
        showVector(ar.eval("http://cui.unige.ch/isi/index.html/#local"));
        showVector(ar.eval("http://matis.unige.ch/icon.gif"));
        showVector(ar.eval("http://localhost/"));

    }
}
