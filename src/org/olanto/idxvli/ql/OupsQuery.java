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

package org.olanto.idxvli.ql;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.engine.Word;
import org.olanto.util.Timer;
import org.olanto.idxvli.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.IdxConstant.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;
/*
 *
 * Cette classe utilise les composant com.swabunga.spell soumis � la licence suivante:
 *
Jazzy - a Java library for Spell Checking
Copyright (C) 2001 Mindaugas Idzelis
Full text of license can be found in LICENSE.txt
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/** cette classe d�termine des alternatives à la requête
 * Normalement cette classe devrait tenir compte de la langue ... � voir pour une nouvelle version
 * 
 */
public class OupsQuery {

//    private static String dictFile = "C:/JG/VLI_RW/dict/fr.dic";
//    private static String phonetFile = "C:/JG/VLI_RW/dict/phonet.fr";
    private static boolean verbose = false;
    private static SpellChecker spellCheck;
    private static SpellDictionary mainDictionary;
    private static SpellDictionary orgDictionary;
    private static IdxStructure id;

    public OupsQuery(IdxStructure id, String dictFile, String phonetFile, String orgFile) {
        this.id = id;
        try {
            mainDictionary = new SpellDictionaryHashMap(new File(dictFile), new File(phonetFile));
            msg("OPEN main Dictionary: dict" + dictFile + " phonetic:" + phonetFile);
            if (orgFile != null) {
                orgDictionary = new SpellDictionaryHashMap(new File(orgFile));
                msg("OPEN organisation Dictionary: dict" + orgFile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        spellCheck = new SpellChecker(mainDictionary);
        if (orgDictionary != null) {
            spellCheck.addDictionary(orgDictionary);
        }
    }

    public void test(String t) {
        Timer t1 = new Timer("check:" + t);
        if (!spellCheck.isCorrect(t)) {
            List suggest = spellCheck.getSuggestions(t, 10000);
            System.out.println("nb suggestion:" + suggest.size());
            for (Iterator suggestedWord = suggest.iterator(); suggestedWord.hasNext();) {
                System.out.print(" " + suggestedWord.next());
            }
            System.out.println();
        } else {
            System.out.println("ok:" + t);
            List suggest = spellCheck.getSuggestions(t, 10000);
            System.out.println("nb suggestion:" + suggest.size());
            for (Iterator suggestedWord = suggest.iterator(); suggestedWord.hasNext();) {
                System.out.print(" " + suggestedWord.next());
            }
            System.out.println();
        }
        t1.stop();
    }

    /** calcul une alternative pour un ensemble de mot null sinon*/
    public String guess(String[] s) {
        String alternative = "";
        boolean existAlternative = false;
        for (int i = 0; i < s.length; i++) {
            String alt = guessWord(s[i]);
            if (verbose) {
                msg("alternative for " + s[i] + " could be:" + alt);
            }
            if (alt == null) {
                alternative += " " + s[i];
            } else {
                alternative += " " + alt;
                existAlternative = true;
            }
        }
        if (existAlternative) {
            msg("query alternative: " + alternative);
            return alternative;
        } else {
            return null;
        }
    }

    /** calcul une alternative pour un mot null sinon*/
    public String guessWord(String s) {
        if (s.length() < MIN_CHAR_SUGGEST) {
            return null;
        } // trop court
        int countOfS = id.getOccOfW(s);
        boolean okOfS = spellCheck.isCorrect(s);
        List suggest = spellCheck.getSuggestions(s, 100000);
        if (verbose) {
            msg("suggest for:" + s + " count " + countOfS + " ok " + okOfS);
        }
        int nbsuggest = suggest.size();
        if (verbose) {
            msg("nb suggestions:" + nbsuggest);
        }
        if (nbsuggest != 0) {
            String[] suggestion = new String[Math.min(nbsuggest, MAX_SUGGESTION)];
            int[] countOfSuggest = new int[Math.min(nbsuggest, MAX_SUGGESTION)];
            int count = 0;
            int decrease = 1;
            // pond�re les suggestions
            for (Iterator suggestedWord = suggest.iterator(); suggestedWord.hasNext();) {/// on doit ajouter un max pour optimiser
                String sw = ((Word) suggestedWord.next()).toString();
                suggestion[count] = sw;
                countOfSuggest[count] = id.getOccOfW(sw);
                if (verbose) {
                    msg("suggest:" + count + " " + sw + " =" + countOfSuggest[count]);
                }
                if (countOfSuggest[count] != 0) {
                    countOfSuggest[count] = Math.max(countOfSuggest[count] / decrease, 1);
                }
                if (verbose) {
                    msg("suggest after scoring:" + count + " " + sw + " =" + countOfSuggest[count]);
                }
                count++;
                if (count == MAX_SUGGESTION) {
                    break;
                }
                decrease *= DECREASE_NEXT_SUGGESTION;
            }
            // cherche la suggestion la plus attrayante !
            int bestSuggest = -1;
            int bestLevel = 0;
            for (int i = 0; i < countOfSuggest.length; i++) {
                if (bestLevel < countOfSuggest[i]) {
                    bestLevel = countOfSuggest[i];
                    bestSuggest = i;
                }
            }
            //�valuation
            if (bestSuggest == -1) {
                return null;
            } // pas de meilleure suggestion
            if (countOfS == 0) {// pas de r�ponse pour s
                if (SUGGEST_MANDATORY_IF_ZERO) {
                    return suggestion[bestSuggest];
                } // au minimun 1
                if (bestLevel >= MIN_FACTOR_SUGGESTION) // un minimun de r�ponse
                {
                    return suggestion[bestSuggest];
                } else {
                    return null;
                } // pas de meilleure suggestion

            } else // il existe des r�ponses pour s
            if ((bestLevel / countOfS) >= MIN_FACTOR_SUGGESTION) {
                return suggestion[bestSuggest];
            } else {
                return null;
            } // pas de meilleure suggestion n'est pas suffisament riche
        }
        return null;
    }

    public static void main(String[] args) {

        OupsQuery oq = new OupsQuery(null, "C:/JG/VLI_RW/dict/fr.dic", "C:/JG/VLI_RW/dict/phonet.fr", "C:/JG/VLI_RW/dict/organisation.fr");
//        oq.test("frase");
//        oq.test("faclult�");
//        oq.test("faKult�");
//        oq.test("jauli");
//        oq.test("filozofie");
//        oq.test("filozofi");
//        oq.test("fisik");
//        oq.test("ortograf");
        oq.test("d�mocrassie");
        oq.test("axion");
//        oq.test("cui");
//        oq.test("cour");
//        oq.test("benzineb");
//        oq.test("benzined");

    }
}
