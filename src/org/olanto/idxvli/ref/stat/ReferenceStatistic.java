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
package org.olanto.idxvli.ref.stat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;
import org.olanto.idxvli.server.REFResultNice;
import static org.olanto.idxvli.IdxConstant.MSG;

/**
 * calcul des statitisques pour les références
 */
public class ReferenceStatistic {

    private String[] multiref;
    private String[] txt;
    private int[] txtlength;
    private int totword;
    private int totwordref;
    private HashMap<String, InverseRef> invmap = new HashMap<String, InverseRef>(1000);
    private InverseRef[] alldoc;  // la liste des documents
    private Pattern p = Pattern.compile("[" + REFResultNice.DOC_REF_SEPARATOR + "]");  // le |
    private Pattern ps = Pattern.compile("[\\s]");  // le blanc
    private NumberFormat formatter = new DecimalFormat("#0.0");

    public ReferenceStatistic(List<String> txtRefOrigin, List<String> docMultiRef, int totword) {
        this.multiref = new String[docMultiRef.size()];
        docMultiRef.toArray(this.multiref);
        this.txt = new String[txtRefOrigin.size()];
        txtRefOrigin.toArray(txt);
        txtlength = new int[txt.length];
        this.totword = totword;
        totwordref = 0;
        for (int i = 0; i < txt.length; i++) {
            String[] words = ps.split(txt[i]);
            txtlength[i] = words.length;
            totwordref += words.length;
        }
        computeStatByRef();


    }

    public void computeStatByRef() {
        for (int i = 0; i < txt.length; i++) {
            String[] refs = p.split(multiref[i]);
            for (int j = 0; j < refs.length; j++) {
                InverseRef r = invmap.get(refs[j]);
                if (r == null) {  // nouveau document 
                    r = new InverseRef(refs[j]);
                    invmap.put(refs[j], r);
                }
                r.addRef(i, (float) txtlength[i] * 100.0f / (float) totword);
            }
        }
        alldoc = new InverseRef[invmap.size()]; // constuit un vecteur de doc
        int count = 0;
        for (Iterator<String> iter = invmap.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            alldoc[count] = invmap.get(key);
            count++;
        }
        Arrays.sort(alldoc, new RefComparator());
    }

    public String getHeaderSat(String fileName, String Collections, int min) {
        StringBuilder res = new StringBuilder("");
        res.append("</p> "+MSG.get("server.qd.MSG_1")+" " + fileName);
        res.append("</p> "+MSG.get("server.qd.MSG_2")+" " + totword);
        res.append("</p> "+MSG.get("server.qd.MSG_3")+" " + alldoc.length);
        res.append("</p> "+MSG.get("server.qd.MSG_4")+" " + totwordref
                + " (" + formatter.format((float) totwordref * 100.0f / (float) totword + 0.0000001f) + "%)");
        res.append("</p> "+MSG.get("server.qd.MSG_5")+" "+min);
        if (Collections.equals(""))res.append("</p> "+MSG.get("server.qd.MSG_6"));
                else res.append("</p> "+MSG.get("server.qd.MSG_7")+" " + Collections);
        res.append("</p> "+MSG.get("server.qd.MSG_8")+" " + Calendar.getInstance().getTime());
        return res.toString();
    }

    public String getStatByRef() {
        //computeStatByRef();
        StringBuilder res = new StringBuilder("");
        res.append("</p><table BORDER=\"1\">\n");
        res.append("<caption><b>"+MSG.get("server.qd.MSG_9")+"</b></caption>\n"); // titre
        res.append("<tr>\n" //entête
                + "<th>"+MSG.get("server.qd.MSG_10")+"</br>"+MSG.get("server.qd.MSG_11")+"</th>\n"
                + "<th>"+"%"+"</th>\n"
                + "<th>"+MSG.get("server.qd.MSG_12")+"</th>\n"
                + "</tr>\n");
        for (int i = 0; i < alldoc.length; i++) {
            InverseRef current = alldoc[i];
            res.append("<tr>\n");
            res.append("<td>" + current.docref + "</td>\n");  // numéro de la ref
            if (current.pcttotword < 1) {
                res.append("<td> &lt;1 %</td>\n");  //la ref
            } else {
                res.append("<td>" + formatter.format(current.pcttotword) + " %</td>\n");  //la ref
            }
            res.append("<td>"); // bloc des références
            for (int j = 0; j < current.ref.size(); j++) {
                res.append((Integer.parseInt(current.ref.get(j))+1) + ", ");
            }
            res.append("</td>"); // fin de bloc des références
            res.append("</tr>\n");

        }

        res.append("</table>\n");
        return res.toString();
    }

    public String getStatByQuote() {
        StringBuilder res = new StringBuilder("");
        res.append("</p><table BORDER=\"1\">\n");
        res.append("<caption><b>"+MSG.get("server.qd.MSG_13")+"</b></caption>\n"); // titre
        res.append("<tr>\n" //entête
                + "<th>"+MSG.get("server.qd.MSG_14")+"</br>"+MSG.get("server.qd.MSG_15")+"</th>\n"
                + "<th>"+MSG.get("server.qd.MSG_16")+"</th>\n"
                + "<th>"+MSG.get("server.qd.MSG_17")+"</th>\n"
                + "</tr>\n");

        for (int i = 0; i < txt.length; i++) {
            String[] refs = p.split(multiref[i]);
            res.append("<tr>\n");
            res.append("<td>" + (i + 1) + "</td>\n");  // numéro de la ref
            res.append("<td>" + txt[i] + "</td>\n");  //la ref
            res.append("<td>"); // bloc des références
            for (int j = 0; j < refs.length; j++) {
                res.append(refs[j].replace("¦", "/") + "</br>\n");
            }
            res.append("</td>"); // fin de bloc des références
            res.append("</tr>\n");
        }
        res.append("</table>\n");
        return res.toString();

    }
}
