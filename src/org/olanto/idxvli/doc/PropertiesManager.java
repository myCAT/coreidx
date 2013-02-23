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

package org.olanto.idxvli.doc;

import java.util.*;
import org.olanto.idxvli.util.*;
import static org.olanto.idxvli.IdxEnum.*;

/**
 *
 *  Comportements d'un gestionnaire de propiétés des documents.
 *
 * 
 *
 *
 */
public interface PropertiesManager {

    /**
     *  crée un gestionnaire  de 2^_maxPropertie par défaut à l'endroit indiqué par le path, (maximum=2^31), objet=bit[2^_maxDoc]
     * @param _typeDocImplementation (FAST BIG)
     * @param _path directoire
     * @param _file nom racine des fichier
     * @param _maxPropertie 2^_maxPropertie
     * @param _lengthString longueur max d'une propriété
     * @param _maxDoc maximum de documents
     * @return gestionnaire de propriétés
     */
    public PropertiesManager create(implementationMode _typeDocImplementation, String _path, String _file, int _maxPropertie, int _lengthString, int _maxDoc);

    /**
     *  ouvre le gestionnaire à l'endroit indiqué par le path
     * @param _typeDocImplementation (FAST BIG)
     * @param _path directoire
     * @param _file nom racine des fichier
     * @param _RW (lecture/lect+écriture)
     * @return gestionnaire de propriétés
     */
    public PropertiesManager open(implementationMode _typeDocImplementation, String _path, String _file, readWriteMode _RW);

    /**  ferme le gestionnaire (et sauve les modifications*/
    public void close();

    /**
     *  récupère le dictionnaire de propriétés
     * @return liste des propriétés actives
     */
    public List<String> getDictionnary();

    /**
     *  récupère le dictionnaire de propriétés ayant un certain préfix (COLECT., LANG.)
     * @param prefix préfixe des propriétés
     * @return liste des propriétés actives
     */
    public List<String> getDictionnary(String prefix);

    /**
     *  ajoute une propiété pour un document
     * @param properties nom de la propriété
     * @param doc document id
     */
    public void put(String properties, int doc);

    /**
     *  élimine une propiété pour un document
     * @param properties nom de la propriété
     * @param doc document id
     */
    public void clear(String properties, int doc);

    /**
     *  ajoute une propiété pour tous les documents avec un masque extérieur
     * @param properties nom de la propriété
     * @param corpus masque
     */
    public void put(String properties, SetOfBits corpus);

    /**
     *  vérifie si le document possède la propriété
     * @param properties nom de la propriété
     * @param doc document id
     * @return true=la propriété est active pour ce document
     */
    public boolean get(String properties, int doc);

    /**
     *  cherche toutes les valeurs d'une propriétés
     * @param properties nom de la propriété
     * @return masque sur l'ensemble du corpus
     */
    public SetOfBits get(String properties);

    /**
     *  retourne le nbr de propriétés actuellement gérées dans le gestionnaire
     * @return nbr de propriétés
     */
    public int getCount();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  imprime des statistiques */
    public String getStatistic();
}
