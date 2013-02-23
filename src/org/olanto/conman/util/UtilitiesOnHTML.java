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

package org.olanto.conman.util;

import org.olanto.conman.util.otheropen.HTMLEntities;
import static org.olanto.util.Messages.*;

/** utilitaires pour le html (extration des titres)
 * 
 *
 * 
 */
public class UtilitiesOnHTML {

    static final String END_MARK = "$$$$$$$$";
    static final boolean verbose = false;

    public static String getTitle(String html) {
        // try from title
        String title = extract(html, "<title", "</title>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc title : " + title);
            }
            return title;
        }
        // try from title
        title = extract(html, "<TITLE", "</TITLE>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc TITLE : " + title);
            }
            return title;
        }
        // try from h1
        title = extract(html, "<h1", "</h1>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc h1 : " + title);
            }
            return title;
        }
        // try from h1
        title = extract(html, "<H1", "</H1>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc H1 : " + title);
            }
            return title;
        }
        // try from h2
        title = extract(html, "<h2", "</h2>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc h2 : " + title);
            }
            return title;
        }
        // try from h2
        title = extract(html, "<H2", "</H2>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc H2 : " + title);
            }
            return title;
        }
        // try from h3
        title = extract(html, "<h3", "</h3>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc h3 : " + title);
            }
            return title;
        }
        // try from h3
        title = extract(html, "<H3", "</H3>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc H3 : " + title);
            }
            return title;
        }
        msg("no title try to extract from txt");
        String onlytxt = html2txt(html);
        String first = onlytxt.substring(0, Math.min(64, onlytxt.length())) + "...";
        msg("deduce title:" + first);
        return first;
    }

    static public String extract(String s, String start, String end) {
        //System.out.println(" start:"+start+" end:"+end);
        int begrec = s.indexOf(start);
        int endrec = s.indexOf(end, begrec + start.length());
        //System.out.println(" begrec:"+begrec+" endrec:"+endrec);
        if (begrec != -1 & endrec != -1) {
            String rec = s.substring(begrec + start.length(), endrec);
            return rec;
        } else {
            return null;
        }

    }

    ; 
            
           public static String html2txt(String html) {
        StringBuilder res = new StringBuilder();
        int poschar = 0;
        int end = html.length();
        try {
            while (poschar < end) {
                while (html.charAt(poschar) == '<') { // skip tag
                    while ((poschar < end) && (html.charAt(poschar) != '>')) {
                        poschar++;
                    }
                    poschar++;
                    if (!(poschar < end)) {
                        return clean(res.toString());
                    } // c'est fini'
                }
                res.append(html.charAt(poschar));
                poschar++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clean(res.toString());
    }

    public static String clean(String txt) {
        txt = txt.replace('\r', ' ');
        txt = txt.replace('\n', ' ');
        txt = HTMLEntities.unhtmlentities(txt);
        while (txt.indexOf("  ") != -1) {
            txt = txt.replace("  ", " ");
        }
        txt = txt.trim();
        return txt;
    }
}
