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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.idxvli.knn;

import java.util.HashMap;
import org.olanto.idxvli.IdxStructure;
import java.util.Hashtable;
import java.util.Vector;
import static org.olanto.util.Messages.*;

/**
 * Classe impl�mentant la mesure de similarit� de Lesk
 * 

 *<p>
 *
 */
class Entry {

    double wgt; // poids du terme
    Vector<Integer> pos; // positions du terme

    Entry(double wgt, int firstocc) {
        this.wgt = wgt;
        pos = new Vector<Integer>(1);
        pos.add(firstocc);
    }
}

class AlignSegment {

    double wgt; // poids du segment
    int len; // longueur du segment

    AlignSegment(double wgt) {
        this.wgt = wgt;
        len = 1;
    }

    void add(double wgt) {
        this.wgt += wgt;
        len++;
    }
}

public class LeskMeasure {

    static IdxStructure id;
    static LeskMode leskmode;
    static int[] ref;
    private double wgtRef;
    HashMap<Integer, Entry> hr;

    /**
     * @return the wgtRef
     */
    public double getWgtRef() {
        return wgtRef;
    }

    /** Mode pour la pond�ration des mots */
    public static enum LeskMode {

        /**pas de ranking */
        NO,
        /**classique inverse document frequency * term document frequency */
        IDFxTDF,}

    public LeskMeasure(int[] ref) {
        this.ref = ref;
        initRef();
    }

    public static void init(IdxStructure _id, LeskMode _leskmode) {
        id = _id;
        leskmode = _leskmode;
    }

    public double getSimilarityWith(int[] doc) {
        double sim = 0;

        for (int i = 0; i < Math.max(0, doc.length - 1); i++) {
            //System.out.println("i:"+i);
            Entry e = hr.get(doc[i]);
            if (e != null) { // il y a un matching possible
                int nbStart = e.pos.size();
                double keep = 0;
                AlignSegment keepAs = null;
                for (int k = 0; k < nbStart; k++) { // pour chaque point de d�part
                    AlignSegment as = getAlignSegment(doc, i, e, k);
                    if (as != null) {
                        if (keep < as.wgt) { // garde seulement l'alignement le plus significatif
                            keepAs = as;
                            keep = as.wgt;
                        }
                    }
                }
                if (keepAs != null) { //
                    sim += (keepAs.wgt * keepAs.wgt);
                    i += keepAs.len - 1;
                }
            }
        }
        if (sim != 0) {
            sim = sim / getWgtOfDoc(doc) / wgtRef;
        }
        return sim;
    }

    AlignSegment getAlignSegment(int[] doc, int from, Entry e, int choice) {
        //System.out.println("try from:" + from + ", choice:" + choice);
        boolean verbose = false;
        if (e.pos.get(choice) + 1 < ref.length
                && ref[e.pos.get(choice) + 1] == doc[from + 1]) { // il existe segment d'au moins 2 de longeur
            AlignSegment as = new AlignSegment(e.wgt);
            int count = 1;
            int fromRef = e.pos.get(choice);
            if (verbose) {
                System.out.println("  align from:" + from + ", choice:" + choice + ", fromRef:" + fromRef);
            }
            if (verbose) {
                System.out.print(" " + id.getStringforW(ref[fromRef]));
            }
            for (int i = from + 1; i < Math.max(0, doc.length); i++) {
                if (fromRef + count < ref.length && ref[fromRef + count] == doc[from + count]) {
                    if (verbose) {
                        System.out.print(" " + id.getStringforW(ref[fromRef + count]));
                    }
                    as.add(hr.get(ref[fromRef + count]).wgt);
                } else {
                    if (verbose) {
                        System.out.println();
                    }
                    if (verbose) {
                        System.out.println("  align length:" + as.len + ", wgt:" + as.wgt);
                    }
                    return as;
                }
                count++;
            }
            //System.out.println("  align length:" + as.len + ", wgt:" + as.wgt);
            return as;
        }
        return null;

    }

    void initRef() {
        wgtRef = getWgtOfDoc(ref);
        hr = new HashMap<Integer, Entry>(2 * ref.length);
        System.out.print("doc ref: ");
        for (int i = 0; i < ref.length; i++) {
            if (ref[i] != -1) {
                System.out.print(" " + id.getStringforW(ref[i]));
                Entry e = hr.get(ref[i]);
                if (e != null) {  // existe d�ja
                    e.pos.add(i); // ajoute une nouvelle occurence
                } else {
                    switch (leskmode) {
                        case NO:
                            hr.put(ref[i], new Entry(1, i));
                            break;
                        case IDFxTDF:
                            hr.put(ref[i], new Entry(getWgtOfWordIDFxTF(ref[i]), i));
                            break;
                    }
                }
            }
        }
        System.out.println();
    }

    public static double getWgtOfDoc(int[] doc) {
        switch (leskmode) {
            case NO:
                return doc.length;
            case IDFxTDF:
                double wgt = 0;
                for (int i = 0; i < doc.length; i++) {
                    if (doc[i] != -1) {
                        wgt += getWgtOfWordIDFxTF(doc[i]);
                    }
                }
                return wgt;
        }
        error("leskmode:" + leskmode + " not implemented");
        return 0;
    }

    public static double getWgtOfWordIDFxTF(int i) {
        return Math.log(1 + (double) id.lastRecordedDoc / (double) id.getOccOfW(i));
    }
}
