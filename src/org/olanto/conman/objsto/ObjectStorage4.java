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

package org.olanto.conman.objsto;

import static org.olanto.idxvli.IdxEnum.*;

/**  Comportements d'un gestionaire d'objets.
 *
 * version 2.2
 * seul append et read sur les int sont conservés
 *
 * 
 *
 */
public interface ObjectStorage4 {

    /**  cr�e un ObjectStorage de taille 2^maxSize � l'endroit indiqu� par le path,
     *  ObjectStore ne prend en consid�ration que des objets de la minBigSize  exprim� (2^n) en byte,
     *  Exemple create("ici",20,32) cr�e 2^20 id avec un stockage minimum de 32 bytes au premier niveau soit 8 entiers,
     *  les objets plus petits (16) sont stock� dans une structure simplifi�e
     *
     *  modification realSize et StoredSize  //21-11-2005
     */
    public ObjectStorage4 create(implementationMode implementation, String path, int maxSize, int minBigSize);

    /**  ouvre un ObjectStorage  � l'endroit indiqu� par le path */
    public ObjectStorage4 open(implementationMode implementation, String path, readWriteMode _RW);

    /**  ferme un ObjectStorage  (et sauve les modifications*/
    public void close();

    /**  �crit des bytes a cet objet, si 0 = OK , l'identifiant est impos� de l'exterieur pour le premier
    realLength indique la longeur r�el de l'objet sans compression, l'objet doit d�j? ?tre compress� si l'on veut le compresser*/
    public int write(byte[] b, int user, int realLength);

    /**  retourne l'objet stock� completement,si null = erreur*/
    public byte[] read(int user);

    /**  retourne l'objet stock� partiellement de from � to,si null = erreur*/
    public byte[] read(int user, int from, int to);

    /**  retourne la taille stock�e de l'objet*/
    public int storedSize(int user);

    /**  retourne la taille r�el de l'objet sans compression*/
    public int realSize(int user);

    /**  lib�re un id (ceux vus par les utilisateurs)*/
    public void releaseId(int user);

    /**  imprime les statistiques */
    public void printStatistic();

    /**  imprime les statistiques */
    public void resetStatistic();

    /**  imprime les info sur un id (USER) */
    public void printNiceId(int user);

    /**  utilisation en byte de l'espace disque (sans l'overhead) */
    public long getSpace();
}
