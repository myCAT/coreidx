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

package org.olanto.idxvli.knn;

import org.olanto.idxvli.*;

/**
 * Une interface pour effectuer le calcul de la distance entre les documents. 
 * 

 *
 */
public interface KNNManager {

    /**
     * PrÃ©pare une structure de calcul de KNN.
     * @param _verbose pour le debuging (true)
     * @param _glue indexation de rÃ©fÃ©rence
     * @param minocc minimum d'occurences pour Ãªtre dans la prÃ©sÃ©lection.
     * @param maxlevel maximum d'occurences  en 0/00 du corpus pour Ãªtre dans a prÃ©sÃ©lection.
     * @param formulaIDF inverse document frequency formula.
     * @param formulaTF terme frequency formula.
     */
    public void initialize(IdxStructure _glue, int minocc, int maxlevel, boolean _verbose, int formulaIDF, int formulaTF);

    /** Chercher les N premiers voisins du document d, sans formattage.
     * @param doc document
     * @param N nombre de voisins
     * @return rÃ©ponse
     */
    public int[][] getKNNForDoc(int doc, int N);

    public KNNResult KNNForDoc(int doc, int N);

    /** Chercher les N premiers voisins du texte request, sans formattage.
     * @param request texte de rÃ©fÃ©rence
     * @param N nombre de voisins
     * @return rÃ©ponse
     */
    public KNNResult getKNN1(String request, int N);

    /** Chercher les N premiers voisins du texte request, sans formattage, avec une liste prÃ©dÃ©finie de document.
     * @param topic liste de doc
     * @param request texte de rÃ©fÃ©rence
     * @param N nombre de voisins
     * @return rÃ©ponse
     */
    public KNNResult getKNNinTopic(int[] topic, String request, int N);

    public float[] getRawKNN(String request);

    public int[][] getKNN(String request, int N);

    /** Chercher la pondÃ©ration des documents
     * @param request texte de rÃ©fÃ©rence
     * @return rÃ©ponse
     */
    public float[] getSimilarity(String request);

    /** visualiser le rÃ©sultat d'une rÃ©ponse knn
     * @param res RÃ©sultat d'une requÃªte KNN (getKNN)
     */
    public void showKNN(int[][] res);

    public void showKNNWithName(int[][] res);

    /** Chercher les N premiers voisins du texte request
     * @param request texte de rÃ©fÃ©rence
     * @param N nombre de voisins
     * @return XML format
     */
    public String searchforKNN(String request, int N);

    /** formatage XML d'une ligne de rÃ©ponse
     * @param fn nom du fichier
     * @param doc document
     * @param score niveau de similaritÃ©
     * @return XML format
     */
    public String XMLrefFNWithScore(String fn, int doc, int score);
}
