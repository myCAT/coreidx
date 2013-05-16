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

import java.io.FileNotFoundException;
import org.olanto.util.Timer;

/**
 *
 * @author simple
 */
public class GetHitsStatsTest {

    public static void main(String[] args) throws FileNotFoundException {
        SearchHits sHits = new SearchHits();
        Timer t = new Timer("GetHighlight_Occurrences");
        sHits.getRefWordsPos("C:/MYCAT/corpus/txt/EN/Glossaries¦External¦Product classification¦Documents¦Customs Tariffs¦Philippines¦Philippines2003.pdf.txt", "intellectuelle exclusive du Bureau");
        t.stop();
        Timer t1 = new Timer("GetHighlight_Occurrences");
        sHits.getRefWordsPos("C:/MYCAT/corpus/txt/EN/TEST¦Philippines2003.pdf.txt", "Philippines");
        t1.stop();
        Timer t2 = new Timer("GetHighlight_Occurrences");
        sHits.getRefWordsPos("C:/MYCAT/corpus/txt/EN/Glossaries¦External¦Product classification¦Documents¦Customs Tariffs¦Philippines¦Philippines2003.pdf.txt", "exécution de la Convention internationale");
        t2.stop();
        Timer t3 = new Timer("GetHighlight_Occurrences");
        sHits.getRefWordsPos("C:/MYCAT/corpus/txt/EN/ECHA¦EUR_Lex_32006R1907R(01).htm.txt", "report");
        t3.stop();
        Timer t4 = new Timer("GetHighlight_Occurrences");
        sHits.getRefWordsPos("C:/MYCAT/corpus/txt/EN/ECHA¦EUR_Lex_32006R1907R(01).htm.txt", " permission to refer to the full study report");
        t4.stop();
    }
}
