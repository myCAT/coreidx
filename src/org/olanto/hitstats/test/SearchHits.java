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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.olanto.idxvli.ref.UtilsFiles;
import org.olanto.idxvli.server.IndexService_MyCat;

/**
 *
 * count hits
 */
public class SearchHits {

    private String REGEX_BEFORE_TOKEN = "([^a-zA-Z0-9]|[\\s\\p{Punct}\\r\\n\\(\\{\\[\\)\\}\\]]|^)";
    private String REGEX_AFTER_TOKEN = "([^a-zA-Z0-9\\-\\_\\/]|[\\s\\p{Punct}\\r\\n\\)\\}\\(\\{\\[\\]]|$)";
    public IndexService_MyCat is;
    public Utility utils = new Utility();
    public float reFactor = 1.7f;
    public int minRefLn = 20;
    static ArrayList<String> stopWords;

    public SearchHits() {
        stopWords = getStopWords();
    }

    public void getRefWordsPos(String fileNameIn, String expression) throws FileNotFoundException {
        boolean verbose = false;
        boolean notask = false; // to test read load
        FileInputStream in = new FileInputStream(fileNameIn);
        String content = "";
        content += UtilsFiles.file2String(in, "UTF-8").toLowerCase();
        if (!notask) {
            int queryLn = expression.length();

            ArrayList<String> query = utils.getQueryWords(expression, stopWords);
            if (verbose) {
                System.out.println("list of words to look for : " + query.toString());
            }

            String regex;
            int refLength = (int) (((reFactor * queryLn) > minRefLn) ? (reFactor * queryLn) : minRefLn);
//        System.out.println("refLength: " + refLength);

            ArrayList<String> Pos = new ArrayList<String>();
            ArrayList<Integer> startPos = new ArrayList<Integer>();
            ArrayList<Integer> lastPos = new ArrayList<Integer>();
            String first, res, last;
            Pattern p;
            Matcher m;
            startPos.clear();
            lastPos.clear();

            first = query.get(0);
            last = query.get(query.size() - 1);
            regex = REGEX_BEFORE_TOKEN + first + REGEX_AFTER_TOKEN;
            p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            m = p.matcher(content);
            if (m.find()) {
//            System.out.println("start found at : " + m.start());
                startPos.add(m.start());
                while (m.find()) {
                    startPos.add(m.start());
//                System.out.println("Start found at : " + m.start());
                }
            }
            regex = REGEX_BEFORE_TOKEN + last + REGEX_AFTER_TOKEN;
            p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            m = p.matcher(content);
            if (m.find()) {
//            System.out.println("last found at : " + m.start());
                lastPos.add(m.start() + last.length());
                while (m.find()) {
                    lastPos.add(m.start() + last.length());
//                System.out.println("last found at : " + m.start());
                }
            }
            int startp, lastp;
            for (int s = 0; s < startPos.size(); s++) {
                startp = startPos.get(s);
                for (int l = 0; l < lastPos.size(); l++) {
                    lastp = lastPos.get(l);
                    if (((lastp - startp) >= (queryLn / 2)) && ((lastp - startp) <= refLength)) {
//                   System.out.println("Found a potential gap, checking if it contains all words");
                        if (getAllWords(content.substring(startp, lastp + 1), query)) {
                            res = startp + "¦" + (lastp - startp);
                            Pos.add(res);
                        }
                    }
                }
            }
            getPositionsRef(Pos);
        }
//        for (int i = 0; i < Pos.size(); i++) {
//            System.out.println("Positions found in Line: " + Pos.get(i));
//        }
//        return getPositionsRef(Pos);
    }

    public boolean getAllWords(String content, ArrayList<String> Query) {
        String curHit;
        boolean allfound = true;
        int j = 1;
        while ((allfound) && (j < Query.size() - 1)) {
            curHit = Query.get(j);
//            System.out.println("test if: " + curHit + " is in :" + content);
            if (content.contains(curHit)) {
                j++;
            } else {
                allfound = false;
            }
        }
//        System.out.println("All found : " + allfound);
        return allfound;
    }

    private void getPositionsRef(ArrayList<String> Pos) {
        boolean verbose = false;
        int[][] posit;
        int i, k, l, r;
        String curr;
        if (!Pos.isEmpty()) {
            posit = new int[Pos.size()][2];
            for (int s = 0; s < Pos.size(); s++) {
                curr = Pos.get(s);
                i = curr.indexOf("¦");
                k = curr.length();
                l = Integer.parseInt(curr.substring(0, i));
                l = (l > 0) ? l + 1 : l;
                r = Integer.parseInt(curr.substring(i + 1, k));
                posit[s][0] = l;
                if (l == 0) {
                    posit[s][1] = r + 2;
                } else {
                    posit[s][1] = r + 1;
                }
            }
            if (verbose) {
                System.out.println("Number of Occurrences = " + posit.length);
            }
//            for (int ls = 0; ls < posit.length; ls++) {
//                System.out.println("Starts at: " + posit[ls][0] + " Length: " + posit[ls][1]);
//            }
        } else {
            posit = new int[1][1];
            posit[0][0] = -1;
        }
//        return posit;
    }

    public ArrayList<String> getStopWords() {

        ArrayList<String> stopWords = new ArrayList<String>();
        if (is == null) {
            is = org.olanto.conman.server.GetContentService.getServiceMYCAT("rmi://localhost/VLI");
        }
        try {
            String[] stpwd = is.getStopWords();
            for (int i = 0; i < stpwd.length; i++) {
                stopWords.add((stpwd[i].trim()));
            }
//            System.out.println("Stop words:" + stopWords.toString());

        } catch (RemoteException ex) {
            Logger.getLogger(SearchHits.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stopWords;

    }
}
