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

import static org.olanto.util.Messages.*;
import java.util.regex.*;

/** test sur regex.
 * 
 *
 * 
 */
public class Test_REGEX {

    private static Pattern p;
    private static Matcher m;

    public static void main(String[] args) {

        String s = "ceci     est, une source de chaleur.";

        test(s, "est");
        test(s, "ce");
        test(s, "c.");
        test(s, "r.e");
        test("Ceci est cela", "[cC]e");
        test("Ceci est cela", "[cC]*[ia]");
        test(";vertu;verte;vertus;vert;vertueux;vertueuse;", ";vertu *");
        test(";vertu;verte;vertus;vert;vertueux;vertueuse;", ";vertue *");
        split(s, " ");
        split("NEAR(bnf,java)", "[\\s\\.+~*,\\)\\(]");

    }

    public static void test(String s, String r) {

        p = Pattern.compile(r);
        m = p.matcher(s);

        msg("----- pattern:" + r + " apply on:" + s);
        while (m.find()) {
            msg("found [" + m.start() + ".." + m.end() + "] " + s.substring(m.start(), m.end()));

        }
    }

    public static void split(String s, String r) {

        p = Pattern.compile(r);

        msg("----- split pattern:" + r + " apply on:" + s);
        String[] item = p.split(s);
        for (int i = 0; i < item.length; i++) {
            msg("split " + i + " : " + item[i]);
        }
    }
}
