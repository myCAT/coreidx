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
package org.olanto.idxvli.util;

import java.io.*;
import static org.olanto.util.Messages.*;
import static org.olanto.idxvli.util.BytesAndFiles.*;

/**
 * Impl�mente les op�rations de base sur un vecteur de bit de taille fixe n*32.
 * <p>
 * Cette classe est 3 fois plus rapide pour les get que BitSet de Sun (pas de test ?). Les
 * op�rations sont 30% plus lente (sans doute l'utilisation des int � la place des long).
 * La classe permet de r�cup�rer le vecteur zip�.
 * <p>
 * <p>
 * 
 *
 */
public class SetOfBits implements Serializable {

    /** effectue une op�ration sur tous les bits (voir and, or ) */
    public static final int ALL = -1;
    /** mask pour chaque bit d'un int */
    static final int[] mask = {1, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6, 1 << 7, 1 << 8, 1 << 9,
        1 << 10, 1 << 11, 1 << 12, 1 << 13, 1 << 14, 1 << 15, 1 << 16, 1 << 17, 1 << 18, 1 << 19,
        1 << 20, 1 << 21, 1 << 22, 1 << 23, 1 << 24, 1 << 25, 1 << 26, 1 << 27, 1 << 28, 1 << 29,
        1 << 30, 1 << 31
    };
    /** nbre de bits dans un int */
    private final int INT_LENGTH = 32;
    /** taille du vecteur de bits */
    private int cardinality = 0;
    /** taille interne du vecteur */
    private int internalSize;
    /** vecteur de int repr�sentant les bits du vecteur */
    private int[] binaryInt;

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(binaryInt);
        out.writeInt(cardinality);
        out.writeInt(internalSize);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        binaryInt = (int[]) in.readObject();
        cardinality = in.readInt();
        internalSize = in.readInt();
    }

    /**
     * cr�e un vecteur vide (false),
     * ne g�re pas les tailles inf�rieur non divisible par 32
     * @param nbits taille du vecteur de bits
     */
    public SetOfBits(int nbits) {
        if (nbits % INT_LENGTH != 0) {
            error("BitArray_Basic size must be a multiple of 32");
        }
        cardinality = nbits;
        internalSize = cardinality / INT_LENGTH;
        binaryInt = new int[internalSize];
    }

    /**
     * cr�e un vecteur � partir des bit d'un vecteur de int
     * @param binaryInt les bits des ints deviennent ceux du vecteur de bit
     */
    public SetOfBits(int[] binaryInt) {
        cardinality = binaryInt.length;
        this.binaryInt = binaryInt;
    }

    /**
     * crée un vecteur à partir des bit d'un vecteur de int
     * @param binaryInt les bits des ints deviennent ceux du vecteur de bit
     */
    public SetOfBits(SetOfBits s) {
        cardinality = s.cardinality;
         internalSize = s.internalSize;
       binaryInt = s.binaryInt.clone();
    }

    /** cr�e un vecteur � partir d'un zip dont les nombre de bits �tait initialement nbits
     * @param bZip zip sous forme de byte
     *  @param nbits taille du vecteur de bits
     */
    public SetOfBits(byte[] bZip, int nbits) {
        cardinality = nbits;
        internalSize = cardinality / INT_LENGTH;
        binaryInt = decompress(bZip, internalSize);
    }

    /** retourne une copie du vecteur stockant les bits
     *  @return les bits
     */
    public int[] getIntStructure() {

        return binaryInt.clone();
    }

    /**
     * mets � jour la position bitIndex avec la valeur value
     * @param bitIndex position � mettre � jour
     * @param value valeur de la mise � jour
     */
    public final void set(int bitIndex, boolean value) {
        //msg("bitIntdex:"+bitIndex);
        //msg("binaryInt.length:"+binaryInt.length);
        if (value) {
            binaryInt[bitIndex / INT_LENGTH] |= mask[bitIndex % INT_LENGTH];
        } // set bit
        else {
            binaryInt[bitIndex / INT_LENGTH] &= ~mask[bitIndex % INT_LENGTH];
        }
    }

    /**
     * cherche la valeur du bit � la position bitIndex
     * @param bitIndex position recherch�e
     * @return valeur de la position
     */
    public final boolean get(int bitIndex) {
        return (binaryInt[bitIndex / INT_LENGTH] & mask[bitIndex % INT_LENGTH]) == mask[bitIndex % INT_LENGTH];
    }

    /**
     * cherche le nombre de bit à 1
     * @return nbr à 1
     */
    public final int countTrue() {
        int res = 0;
        for (int i = 0; i < cardinality; i++) {
            if (get(i)) {
                res++;
            }
        }
        return res;
    }

    /**
     * retourne le vecteur compress�
     * @return zip du vecteur de bits
     */
    public final byte[] getZip() {
        return compress(binaryInt);
    }

    /**
     * retourne la taille du vecteur
     * @return taille du vecteur de bits
     */
    public final int length() {
        return cardinality;
    }

    /**
     * convertit les nbits en nInts
     * @param nbits taille en bits
     * @return taille en repr�sentation interne
     */
    private final int bitsToInts(int nbits) {
        if (nbits != ALL) {
            return (1 + nbits / INT_LENGTH);
        } else {
            return internalSize;
        }
    }

    /**
     * this = this AND operand
     * @param operand pour effectuer l'op�ration
     * @param firstbits l'op�ration est restreinte aux premiers bits (ALL=tous)
     */
    public final void and(SetOfBits operand, int firstbits) {
        int first = bitsToInts(firstbits);
        for (int i = 0; i < first; i++) {
            binaryInt[i] &= operand.binaryInt[i];
        }
    }

    /**
     * this = this OR operand
     * @param operand pour effectuer l'op�ration
     * @param firstbits l'op�ration est restreinte aux premiers bits (ALL=tous)
     */
    public final void or(SetOfBits operand, int firstbits) {
        int first = bitsToInts(firstbits);
        for (int i = 0; i < first; i++) {
            binaryInt[i] |= operand.binaryInt[i];
        }
    }

    /**
     * this = NOT (this)
     * @param firstbits l'op�ration est restreinte aux premiers bits (ALL=tous)
     */
    public final void not(int firstbits) {
        int first = bitsToInts(firstbits);
        for (int i = 0; i < first; i++) {
            binaryInt[i] = ~binaryInt[i];
        }
    }

    /**
     * true if all false
     * @param firstbits l'op�ration est restreinte aux premiers bits (ALL=tous)
     * return true if all false
     */
    public final boolean allFalse(int firstbits) {
        int first = bitsToInts(firstbits);
        for (int i = 0; i < first; i++) {
            if (binaryInt[i] != 0) {
                return false;
            }
        }
        return true;
    }
}
