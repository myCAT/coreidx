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

import org.olanto.util.Timer;
import java.io.*;
import java.util.Arrays;
import org.olanto.idxvli.*;
import org.olanto.idxvli.extra.*;
import org.olanto.conman.server.*;
import org.olanto.idxvli.ql.OrderByDate;
import org.olanto.idxvli.ql.OrderByName;
import org.olanto.idxvli.ql.OrderResult;

/**
 * Classe stockant les résultats d'une requête avec titre et snipet.
 *
 * <p>
 *
 */
public class QLResultNice implements Serializable {

    /* les documents résultats */
    public int[] result;
    /* les partie du results */
    public String[] docname;
    private long[] docdate;  // initialisé seulement en cas de tri ...
    public String[] title;
    public String[] clue;
    /* la durée d'exécution */
    public long duration;  // en ms
    /* autour de la requête */
    public String[] termsOfQuery;
    public String query;
    public String query2;
    public String properties;
    public String profile;
    public String alternative;
    /*  exact filtering */
    private long durationExactFiltering;  // en ms
    private int lastexact;
    private int countcheckfile;
    private boolean[] exact;
    private boolean exactDone;

    /**
     * crée un résultat
     *
     * @param result id des documents
     * @param duration dur�e
     */
    public QLResultNice(String query1, String query2, String properties, String profile, String[] termsOfQuery, int[] result, String[] docname, String[] title, String[] clue, long duration, String alternative) {
        this.query2 = query2;
        this.query = query1;
        this.properties = properties;
        this.profile = profile;
        this.termsOfQuery = termsOfQuery;
        this.result = result;
        this.docname = docname;
        this.title = title;
        this.clue = clue;
        this.duration = duration;
        this.alternative = alternative;
    }

    /**
     * crée un résultat
     *
     * @param result id des documents
     * @param duration dur�e
     */
    public QLResultNice(String query, String properties, String profile, String[] termsOfQuery, int[] result, String[] docname, String[] title, String[] clue, long duration, String alternative) {
        this.query = query;
        this.properties = properties;
        this.profile = profile;
        this.termsOfQuery = termsOfQuery;
        this.result = result;
        this.docname = docname;
        this.title = title;
        this.clue = clue;
        this.duration = duration;
        this.alternative = alternative;
    }

    public void hilite(int entry) {
        for (int i = 0; i < termsOfQuery.length; i++) {
            //     docname[entry]=showTerm(docname[entry],voc[i]);
            title[entry] = showTerm(title[entry], termsOfQuery[i]);
            clue[entry] = showTerm(clue[entry], termsOfQuery[i]);
        }
    }

    public static String showTerm(String s, String term) {
        if (s != null && term.length() > 2) // au moins trois caract�res
        {
            return s.replace(term, "<b>" + term + "</b>");
        }
        return s;
    }

    public void checkExact(IdxStructure id, int size) {
        if (!exactDone){ // dont do this many times !!
          exactDone=true;  
        
        boolean verbose = true;
        Timer time = new Timer(query, true);
        if (verbose) {
            System.out.println("check exact expression");
        }
        if (result.length == 0) {  // no result
            if (verbose) {
                System.out.println("check exact expression");
            }
            return;
        }
        String exactExpression = "";
        int beg = query.indexOf("QUOTATION(\"");
        int end = query.indexOf("\")");
        if (end == -1 || beg == -1) {  // no result
            System.out.println("Error: Quey is not QUOTATION(\"....\") no filtering for exact matching");
            return;
        }
        exactExpression = query.substring(beg + 11, end);
        if (verbose) {
            System.out.println("Exact expression is \"" + exactExpression + "\"");
        }
        exact = new boolean[result.length];
        for (int i = 0; i < result.length; i++) {
            countcheckfile++;
            if (id.isExactExpInDoc(exactExpression, result[i], docname[i])) {
              //  System.out.println("\"" + exactExpression + "\" is in " + docname[i]);
                exact[i] = true; // mark ok
                lastexact++;
            } else {
              //  System.out.println(exactExpression + "\" is not in " + docname[i]);
           }
            if (lastexact == size) {// enough results
                break;
            }
        }

        // compress result
       if (verbose) {
            System.out.println("countcheckfile: " + countcheckfile);
            System.out.println("countexact: " + lastexact);
        }

        int[] exactResult = new int[lastexact];
        String[] exactDocname = new String[lastexact];
        String[] exactTitle = new String[lastexact];
        String[] exactClue = new String[lastexact];
        if (lastexact != 0) {// exist exact to be copied
            int current = 0;
            for (int i = 0; i < countcheckfile; i++) {
                if (exact[i]) {
                   // System.out.println("i: " + i+", current: " + current);
                    exactResult[current] = result[i];
                    exactDocname[current] = docname[i];
                    exactTitle[current] = title[i];
                    exactClue[current] = clue[i];
                    current++;
                }
                
            }
        }
        // replace fuzzy result by exact result
        result = exactResult;
        docname = exactDocname;
        title = exactTitle;
        clue = exactClue;
        
        // finish
        
        durationExactFiltering = time.getstop();
        if (verbose) {
            System.out.println("duration of check exact expression: " + durationExactFiltering + " (ms)");
        }
        }

    }

    public void orderBy(IdxStructure id, String kind) {
        System.out.println("orderBy: " + kind);
        if (kind.equals("DATE")) { // by date
            docdate = new long[result.length];
            for (int i = 0; i < result.length; i++) { // charge les noms pour le tri
                if (docname[i] == null) { // par encore évalué
                    docname[i] = id.getFileNameForDocument(result[i]);
                }
                docdate[i] = id.getDateOfD(result[i]);
            }
            OrderResult[] allres = new OrderResult[result.length]; // constuit un vecteur de doc
            for (int i = 0; i < result.length; i++) {
                allres[i] = new OrderResult(result[i], docname[i], docdate[i]);
            }

            Arrays.sort(allres, new OrderByDate());
            for (int i = 0; i < result.length; i++) { // réécrit le résultat
                result[i] = allres[i].id;
                docname[i] = allres[i].name;
                docdate[i] = allres[i].date;
                //System.out.println("orderByDate: " + result[i] + " / " + docdate[i] + " / " + docname[i]);
            }

            return;
        }
        if (kind.equals("NAME")) {// by name
            for (int i = 0; i < result.length; i++) { // charge les noms pour le tri
                if (docname[i] == null) { // par encore évalué
                    docname[i] = id.getFileNameForDocument(result[i]);
                }
            }
            OrderResult[] allres = new OrderResult[result.length]; // constuit un vecteur de doc
            for (int i = 0; i < result.length; i++) {
                allres[i] = new OrderResult(result[i], docname[i], 0);
            }

            Arrays.sort(allres, new OrderByName());
            for (int i = 0; i < result.length; i++) { // réécrit le résultat
                result[i] = allres[i].id;
                docname[i] = allres[i].name;
                //System.out.println("orderByName: " + result[i] + " / " + docname[i]);
            }
            return;
        }
        // by ranking nothing to do
        for (int i = 0; i < result.length; i++) {
            //System.out.println("orderByRanking: " + result[i] + " / " + docname[i]);
        }
    }

    public void update(IdxStructure id, ContentService cs, String request, int start, int size, boolean fullresult) { // pour le search engine
        if(!fullresult){
        boolean contentservice = cs != null;
        Timer time = new Timer(request, true);
        if (result == null) {// rien � faire
            return;
        } else { // il a des r�sultats
            //msg("nb res:"+result.length);
            for (int i = Math.max(0, start); i < Math.min(start + size, result.length); i++) {
                if (docname[i] == null) { // par encore �valu�
                    String currentRef = id.getFileNameForDocument(result[i]);
                    if (contentservice) {
                        try {
                            docname[i] = cs.getRefName(currentRef);
                            title[i] = cs.getTitle(currentRef);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    clue[i] = "";
                    for (int j = 0; j < termsOfQuery.length; j++) {
                        if (termsOfQuery[j].length() > 2) { // marque pas les trop petits
                            // on doit aussi �liminer les termes des la requ�te AND .... � faire !!!!!!!!!!!!!!!!!!!!!
                            FromTo fromto = DocPosChar.extractIntervalForW(result[i], id, termsOfQuery[j], 4);
                            if (fromto != null) {
                                if (contentservice) {
                                    try {
                                        clue[i] += cs.getCleanText(currentRef, fromto.from, fromto.to) + "...";
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                hilite(i);
                this.duration = time.getstop(); //update time
            }
        }
        }
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(result);
        out.writeObject(docname);
        out.writeObject(title);
        out.writeObject(clue);
        out.writeLong(duration);
        out.writeObject(termsOfQuery);
        out.writeObject(query);
        out.writeObject(alternative);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        result = (int[]) in.readObject();
        docname = (String[]) in.readObject();
        title = (String[]) in.readObject();
        clue = (String[]) in.readObject();
        duration = in.readLong();
        termsOfQuery = (String[]) in.readObject();
        query = (String) in.readObject();
        alternative = (String) in.readObject();
    }
}
