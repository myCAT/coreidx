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

package org.olanto.wildchar.test;

import org.olanto.wildchar.WildCharExpander;

/**
 * 
 *
 */
public class TestFromFile {

 static WildCharExpander  expander;
    
    public static void main(String[] args) {
   //     expander = new WildCharExpander("C:/SIMPLE/dict/ENorganisation.dic");
      expander = new WildCharExpander("C:/SIMPLE/dict/smalllist.txt");
        //target=readFromFile("C:/SIMPLE/dict/fr.dic");
        //expand("\n.*col.*\n");
        //expand("\ncol.*\n");
        test("global*");
        test("*inge*");
        test("*x*y*z");
        test("att*");
        test("a*");
        test("*");
        test(".");
        
        
    }

     public static void test(String s){
             
//         expander.expand(s);
         String[] res=expander.getExpand(s, 20000000);
         System.out.println(s+" size:"+res.length);
//   
     }
    
 
}
