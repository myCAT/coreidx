/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
