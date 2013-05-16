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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.olanto.util.Timer;

/**
 *
 * @author simple
 */
public class GetHitsStatsTestOnFolder {

    static SearchHits sHits = new SearchHits();
    static int count=0;
 
    public static void main(String[] args) throws FileNotFoundException {
      //   String path="C:/MYCAT/corpus/txt/EN";
      //   String path="C:/MYCAT/corpus_cern/txt/EN";
         String path="C:/MYCAT/corpus_OMC/txt/EN";
         test(path,"reportxxxx");
         test(path,"reportxxxx");
         test(path,"report");
         test(path,"wto report");
         test(path,"flat tariff");
      
    }
    public static void test(String path, String exp) {
count=0;
        Timer t = new Timer("GetHighlight_Occurrences on folder"+path+", exp:"+exp);
        ScanFolder(path,exp);
        t.stop();
        System.out.println("countFile:" + count);
    }

    
    
    private static final void ScanFolder(String path,String exp) {
        File f = new File(path);
        if (f.isFile()) {
            if (path.endsWith(".htm") || path.endsWith(".txt") || path.endsWith(".TXT") || path.endsWith(".html") || path.endsWith(".xml") || path.endsWith(".java") || path.endsWith(".sql") || path.endsWith(".lzy")) {
              
//                System.out.println("file:" + path);
                try {
                    count++;
                    sHits.getRefWordsPos(path, exp);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GetHitsStatsTestOnFolder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.out.println("indexdir:" + path);
            String[] lf = f.list();
            int ilf = lf.length;
            for (int i = 0; i < ilf; i++) {
                ScanFolder(path + "/" + lf[i],exp);
            }
        }
    }
}
