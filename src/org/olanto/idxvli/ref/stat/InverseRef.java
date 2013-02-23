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
package org.olanto.idxvli.ref.stat;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/** pour traiter une référence inverse
 */
public class InverseRef {
    public String docref;
    public List<String> ref=new Vector<String>();
    public float pcttotword;
    
    public InverseRef(String docname){
        docref=docname;
        pcttotword=0;
    }
    public void addRef(int n, float pct){
        pcttotword+=pct;
        ref.add(""+n);
    }

}

/** pour implémenter le tri sur % mots
 */
class RefComparator implements Comparator{
    
   public int compare(Object r1, Object r2){  
    
        float r1pct = ((InverseRef)r1).pcttotword;        
        float r2pct = ((InverseRef)r2).pcttotword;
       
        if(r1pct > r2pct)
            return -1;  // pour un ordre décroissant
        else if(r1pct < r2pct)
            return 1;
        else
            return 0;    
    }
    
}
