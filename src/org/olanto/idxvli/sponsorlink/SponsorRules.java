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

package org.olanto.idxvli.sponsorlink;

import java.util.*;
import java.io.*;
import static org.olanto.util.Messages.*;
import java.util.regex.*;

/**pour stoker un système de règles pour les liens sponsorisés
 *
 * 
 *
 * 
 */
public class SponsorRules {

    HashMap<String, String[]> sponsors;
    String sponsorfilename;
    private static Pattern p;
    private static String SEPARATOR = ";";

    /** Creates a new instance of Rule */
    public SponsorRules(String fname) {
        sponsorfilename = fname;
        sponsors = new HashMap<String, String[]>();
        p = Pattern.compile(SEPARATOR);
        msg("init sponsor rules from file : " + fname);
        loadRules();
    }

    private void loadRules() {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(sponsorfilename), "ISO-8859-1");
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            int count = 0;
            while (w != null) {
                count++;
                msg("line " + count + " : " + w);
                if (!w.startsWith("//")) {// pas un commentaire
                    String[] items = p.split(w);
                    if (items.length >= 2) {
                        String[] links = new String[items.length - 1];
                        for (int j = 0; j < links.length; j++) {
                            links[j] = items[j + 1];
                        }
                        sponsors.put(items[0], links);  // on doit ajouter du code pour v�rifier les doublons !!!!!!
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
        String[] links = sponsors.get(s);
        if (links != null) {
            return links;
        }
        return null;
    }

    public static void main(String[] args) { // pour les tests
        SponsorRules ar = new SponsorRules("C:/JG/VLI_RW/data/urlsponsor.txt");
        showVector(ar.eval("java"));
        showVector(ar.eval("informatique"));
        showVector(ar.eval("m�decine"));
        showVector(ar.eval("kjlkjlk"));

    }
}
