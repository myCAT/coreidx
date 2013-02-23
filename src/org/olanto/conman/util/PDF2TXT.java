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


import java.io.*;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFText2HTML;
import org.pdfbox.util.PDFTextStripper;

/** conversion pdf vers txt
 * 
 *
 * 
 */
public class PDF2TXT {

    public static String pdf2txt(byte[] pdfbuf, String pdfFile) {
        boolean toHTML = false;
        boolean sort = true;
        String password = "";

        String encoding = "UTF-8";
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;



        Writer output = null;
        PDDocument document = null;
        try {

            ByteArrayInputStream in = new ByteArrayInputStream(pdfbuf);
            document = PDDocument.load(in);

            System.out.println(pdfFile + " - " + document.toString());

            if (document.isEncrypted()) {
                System.out.println("You do not have permission to extract text from :" + pdfFile);
                document.close();
                return null;
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            output = new OutputStreamWriter(buf, encoding);
            //System.out.println("open output:"+encoding);

            PDFTextStripper stripper = null;
            if (toHTML) {
                stripper = new PDFText2HTML();
            } else {
                stripper = new PDFTextStripper();
                //System.out.println("open stripper");
            }
            stripper.setSortByPosition(sort);
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            stripper.writeText(document, output);

            String s = new String(buf.toString(encoding));

            if (!isText(s.substring(0, Math.min(2000, s.length())))) {
                System.out.println("(rejected) file :" + pdfFile + " = " + s.substring(0, Math.min(100, s.length())));
                output.close();
                document.close();
                return null;
            }

            output.close();
            document.close();
            return s;

        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                output.close();
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static boolean isText(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                count++;
            }
        }
        if (count * 100 / (s.length() + 1) > 6) {
            return true;
        } // au moins x % de blancs 8 semble assez raisonable
        else {
            return false;
        }
    }
}
