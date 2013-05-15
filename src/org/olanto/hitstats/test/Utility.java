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
package org.olanto.hitstats.test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Encore des utilitaires ...
 */
public class Utility {

    public Utility() {
    }

    public int getInd(int curpos, int[][] lines) {
        boolean notfound = true;
        int pos = 0;
        while ((notfound) && (pos < lines.length - 1)) {
            if ((curpos >= lines[pos][1]) && (curpos < lines[pos + 1][1])) {
                notfound = false;
            } else {
                pos++;
            }
        }
        return pos;
    }

    public ArrayList<String> getQueryWords(String query, ArrayList<String> stopWords) {
        String Query = query;
        ArrayList<String> hits = new ArrayList<String>();
        Query = Query.replace("\"", "");
        Query = Query.replace("(", "");
        Query = Query.replace(")", "");
        Query = Query.replace(",", "");
        Query = Query.replace("'", " ");
        Query = Query.replace("`", " ");
        Query = Query.replace("’", " ");
        Query = Query.replace("‘", " ");
        Query = Query.replace("#", " ");
        Query = Query.replace("“", " ");
        Query = Query.replace("”", " ");

        if (Query.startsWith("QL(")) { // complex query
            hits.add("xxx$$$xxx"); // pas de recherche
            return hits;
        }
        //Sinon requête avec des mots, donc enlever les mots clés et les mots vides
        String[] words = Query.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if ((!stopWords.contains(words[i].toLowerCase()))
                    && !(words[i].equalsIgnoreCase("NEAR"))
                    && !(words[i].equalsIgnoreCase("AND"))
                    && !(words[i].equalsIgnoreCase("OR"))
                    && !(words[i].equalsIgnoreCase("QUOTATION"))) {
                hits.add(words[i].toLowerCase());
            }
        }
        return hits;
    }

    public int getIndex(String[] source, String cookie) {
        int i = 0;
        for (int j = 0; j < source.length; j++) {
            if (source[j].equalsIgnoreCase(cookie)) {
                i = j;
                break;
            }
        }
        return i;
    }
    
    public ArrayList<String> getexactWords(String Query) {
        Query = Query.replace("\"", "");
        Query = Query.replace("(", "");
        Query = Query.replace(")", "");
        Query = Query.replace(",", "");
        Query = Query.replace("'", " ");
        Query = Query.replace("`", " ");
        Query = Query.replace("’", " ");
        Query = Query.replace("‘", " ");
        Query = Query.replace("#", " ");
        Query = Query.replace("“", " ");
        Query = Query.replace("”", " ");
        Query = Query.toLowerCase();
        ArrayList<String> hits = new ArrayList<String>();
        String[] words = Query.split("\\s+");
        hits.addAll(Arrays.asList(words));
//        Window.alert("Hits : "+hits.size());
        return hits;
    }
}
