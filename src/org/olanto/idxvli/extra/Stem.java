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

package org.olanto.idxvli.extra;

import java.lang.reflect.Method;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.util.Messages.*;
import java.util.*;
import net.sf.snowball.*;

/**
 * Cette classe wrap les stemmer de snowball.
 *
 * 
 *
 *
 * Cette classe wrap les stemmer de snowball.
 *
 *Snowball - License (package net.sf.snowball)
 *
 * All the software given out on this Snowball site is covered by the BSD License (see http://www.opensource.org/licenses/bsd-license.html ), with Copyright (c) 2001, Dr Martin Porter, and (for the Java developments) Copyright (c) 2002, Richard Boulton.
 *
 * Essentially, all this means is that you can do what you like with the code, except claim another Copyright for it, or claim that it is issued under a different license. The software is also issued without warranties, which means that if anyone suffers through its use, they cannot come back and sue you. You also have to alert anyone to whom you give the Snowball software to the fact that it is covered by the BSD license.
 *
 * We have not bothered to insert the licensing arrangement into the text of the Snowball software
 *
 */
public class Stem {

    private String lang = "french";
    private Class stemClass;
    private SnowballProgram stemmer;
    private Method stemMethod;
    private Object[] emptyArgs = new Object[0];
    private HashMap<String, String> InMemory;
    private int countInMemory = 0;

    public Stem(String _lang) {
        lang = _lang;
        String stemName = "net.sf.snowball.ext." + lang + "Stemmer";
        System.out.println("Stemmer.init:" + _lang);
        countInMemory = 0;
        InMemory = new HashMap<String, String>();
        try {
            stemClass = Class.forName(stemName);
            stemmer = (SnowballProgram) stemClass.newInstance();
            stemMethod = stemClass.getMethod("stem", new Class[0]);
        } catch (Exception e) {
            System.err.println("Error during Stemmer.init (check language):" + stemName);
            e.printStackTrace();
        }

    }

    public String stemmingOfW(String src) {
        try {
            String stemInMemory = InMemory.get(src);
            if (stemInMemory != null) { // dans le cache
                return stemInMemory;
            } else { // pas dans le cache
                stemmer.setCurrent(src);
                stemMethod.invoke(stemmer, emptyArgs);
                String res = stemmer.getCurrent();
                if (countInMemory > STEM_CACHE_COUNT) { // dï¿½passement de capacitï¿½
                    countInMemory = 0;
                    InMemory = new HashMap<String, String>();
                    msg("reset stemming cache:" + lang);
                }
                InMemory.put(src, res);
                countInMemory++;
                return res;
            }
        } catch (Exception e) {
            System.err.println("Error during Stemmer.stemmingOfWt (check language)");
            e.printStackTrace();
        }
        return src;
    }
}
