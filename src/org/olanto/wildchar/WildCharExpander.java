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
package org.olanto.wildchar;

import org.olanto.util.Timer;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.olanto.util.Messages.*;

/**
 * cette classe fournit les méthodes pour retrouver les mots avec des wildchar *
 * = 0 ou n char . =1 char
 *
 */
public class WildCharExpander {

    private String target;
    public static final char ITEM_START = '\n';
    public static final char ITEM_STOP = '\r';

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    public WildCharExpander(String fileName) {
        target = readFromFile(fileName).toString();
    }

    public WildCharExpander(StringBuilder list) {
        target = list.toString();
    }

    public String[] getExpand(String regex, int maxExpand) {
//       System.out.println("---------- target size :" + target.length());
//           System.out.println("---------- look 0 for :" + regex);
        regex = regex.replaceAll("\\*", ".*");
//        System.out.println("---------- look 1 for :" + regex);

        regex = ITEM_START + regex + ITEM_STOP;
////         System.out.println("---------- look 2 for :" + regex);
        Pattern pattern = Pattern.compile(regex);
        // Get a Matcher based on the target string. 
        Matcher matcher = pattern.matcher(target);

        Vector<String> result = new Vector<String>();
        int count = 0;
        while (matcher.find() && count < maxExpand) {
            count++;
            String match = matcher.group();
           result.add(match.substring(1, match.length() - 1));
        }
        String[] res = new String[result.size()];
        result.toArray(res);
        return res;
    }

    public String[] getExpand(String regex, String langS, String[] collections, int maxExpand, boolean onlyOnFileName) {  // filtrer par les collections
        String[] matchcoll = null;
        String langMatcher = "/" + langS + "/";
        String langXX = "/XX/";
        String langEXT = "_" + langS;
        if (collections != null) {
            matchcoll = new String[collections.length];
            for (int i = 0; i < collections.length; i++) {
                matchcoll[i] = "/" + collections[i].substring(11) + "¦";
            }
        }
//        System.out.println("---------- target size :" + target.length());
//           System.out.println("---------- look 0 for :" + regex);
        regex = regex.replaceAll("\\*", ".*");
//        System.out.println("---------- look 1 for :" + regex);

        regex = ITEM_START + regex + ITEM_STOP;
////         System.out.println("---------- look 2 for :" + regex);
        Pattern pattern = Pattern.compile(regex);
        // Get a Matcher based on the target string. 
        Matcher matcher = pattern.matcher(target);

        Vector<String> result = new Vector<String>();
        int count = 0;
        while (matcher.find() && count < maxExpand) {

            String match = matcher.group();
            if (inLangS(match, langMatcher,langXX,langEXT)) {
                if (collections != null) {
                    if (inCollection(match.substring(1, match.length() - 1), matchcoll)) {
                        count++;
                        result.add(match.substring(1, match.length() - 1));
                    }
                } else {
                    count++;
                    result.add(match.substring(1, match.length() - 1));
                }
            }
        }
        String[] res = new String[result.size()];
        result.toArray(res);
        return res;
    }

    private boolean inCollection(String s, String[] coll) {
        for (int i = 0; i < coll.length; i++) {
//          System.out.println("browse "+s+ " in collection:"+coll[i]);
            if (s.contains(coll[i])) {
//             System.out.println("ok for "+s);
                return true;
            }
        }
        return false;
    }

    private boolean inLangS(String s, String lang,String langXX, String langEXT) {
        if (s.contains(lang)||
                (s.contains(langXX)&&s.contains(langEXT))) { // /EN/ ou /XX/.._EN
            return true;
        }
        return false;
    }

    public void expand(String regex) {  // only for debug
        // Compile the regex. 
        System.out.println("---------- look 0 for :" + regex);
        regex = regex.replaceAll("\\*", ".*");
        System.out.println("---------- look 1 for :" + regex);

        regex = ITEM_START + regex + ITEM_STOP;
        System.out.println("---------- look 2 for :" + regex);
        Timer t1 = new Timer("---------- look for :" + regex);

        Pattern pattern = Pattern.compile(regex);
        // Get a Matcher based on the target string. 
        Matcher matcher = pattern.matcher(target);

        // Find all the matches. 
        int count = 0;
        while (matcher.find()) {
            count++;
            System.out.println(matcher.group().substring(1, matcher.group().length() - 1));
//            System.out.println("Found a match: " + matcher.group());
//            System.out.println("Start position: " + matcher.start());
//            System.out.println("End position: " + matcher.end());
        }
        System.out.println("found:" + count);
        t1.stop();
    }

    private static StringBuilder readFromFile(String fname) {
        StringBuilder b = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), "ISO-8859-1");

            BufferedReader in = new BufferedReader(isr);
            String w = ITEM_START + in.readLine();

            while (w != null) {
//                //System.out.println(w);
                b.append(w + ITEM_STOP + ITEM_START);
                w = in.readLine();
            }
        } catch (Exception e) {
            error("readFromFile", e);
        }
        return b;
    }
}
