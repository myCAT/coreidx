/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package org.olanto.onto;

import java.util.*;

/**
 *
 * 
 *
 */
public class ManyOntologyManager {

    static HashMap<String, LexicManager> multiOntology;

    public ManyOntologyManager() {

        multiOntology = new HashMap<String, LexicManager>();
    }

    public void add(String suffix, String rootOntologyFile, String src, String stem) {
        //LexicManager ontology= (new LexicBasic()).create(rootOntologyFile+src+".txt",src,stem);
        LexicManager ontology = (new LexicBetter()).create(rootOntologyFile + src + ".txt", src, stem);
        multiOntology.put(suffix, ontology);
    }

    public LexicManager get(String suffix) {
        return multiOntology.get(suffix);
    }
}
