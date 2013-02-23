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
import java.util.zip.*;
import static org.olanto.util.Messages.*;

/**
 * Classe g�rant des op�rations courante (fichiers, copie, compression, etc).
 * 
 *
 *
 * Classe g�rant des op�rations courante (fichiers, copie, compression, etc).
 */
public class BytesAndFiles {

    /** valeur OK */
    public static final int STATUS_OK = 0;
    /** valeur ERREUR */
    public static final int STATUS_ERROR = -1;
    /** valeur SIMULATION */
    public static final int STATUS_SIMULATION = -2;
    private static final boolean nowrapboolean = true;  // no wrap pour le ZIP
    private static Deflater def = new Deflater(Deflater.DEFAULT_COMPRESSION, nowrapboolean);
    private static Deflater def2 = new Deflater(Deflater.DEFAULT_COMPRESSION, nowrapboolean);
    private static Inflater inf = new Inflater(nowrapboolean);
    private static Inflater inf2 = new Inflater(nowrapboolean);

    /**
     * lit le contenu d'un fichier texte encod�
     * @param fname nom du fichier
     * @param txt_encoding encodage
     * @return le contenu du fichier
     */
    public static final String file2String(String fname, String txt_encoding) {
        StringBuffer txt = new StringBuffer("");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), txt_encoding);
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            while (w != null) {
                txt.append(w);
                txt.append("\n");
                w = in.readLine();
            }
            return txt.toString();
        } catch (Exception e) {
            error("file2String", e);
            return null;
        }

    }

    /**
     *  stocke w encod� � la position pos du fichier r (si n'exc�de pas maxLengthString) et retourne 0 si ok sinon <0
     * @param w cha�ne � �crire
     * @param pos position
     * @param encode encodage
     * @param maxLengthString  longueur max du string converti en bytes
     * @param r fichier
     * @return status
     */
    public static final int writeString(String w, long pos, String encode, int maxLengthString, RandomAccessFile r) {
        byte[] bw = convertString2Bytes(w, encode);
        if (bw.length > maxLengthString) {
            error("*** error name too long '" + w + "' maxbyteslength:" + maxLengthString);
            //System.exit(1);
            writeString("!", pos, encode, maxLengthString, r); // on �crit une marque d'erreur
            return STATUS_ERROR;
        }
        try {
            r.seek(pos); // position la t�te sur le d�but du block

            r.writeInt(bw.length); // �crit la longueur du string

            r.write(bw);
            return STATUS_OK;
        } catch (Exception e) {
            error("IO error writeString", e);
            return STATUS_ERROR;  // en erreur

        }
    }

    /**
     *  lit w � la position pos du fichier r
     * @param pos position
     * @param encode encodage
     * @param r fichier
     * @return cha�ne lue
     */
    public static final String readString(long pos, String encode, RandomAccessFile r) {
        try {
            r.seek(pos); // position la t�te sur le d�but du block

            byte[] bw = new byte[r.readInt()];
            r.read(bw);
            return convertBytes2String(bw, encode);
        } catch (Exception e) {
            error("IO error readString", e);
            return null;  // en erreur

        }
    }

    /**
     *  stocke n � cette position et retourne 0 si ok sinon <0
     * @param n entier � �crire
     * @param pos position
     * @param r fichier
     * @return status
     */
    public static final int writeInt(int n, long pos, RandomAccessFile r) {
        int[] b = new int[1];
        b[0] = n;
        byte[] byteidx = new byte[b.length * 4];  // convertit les int en byte

        intTobyte(b, b.length * 4, byteidx);
        return writeBytes(byteidx, pos, r);
    }

    /**
     *  stock sur le disk ces bytes � cette position
     * retourne 0 si ok sinon <0
     * @param b byte � �crire
     * @param pos position
     * @param r fichier
     * @return status
     */
    public static final int writeBytes(byte[] b, long pos, RandomAccessFile r) {
        try {
            r.seek(pos); // position la t�te sur le d�but du block

            r.write(b); // ecrit le vecteur
            //msg("write "+b.length+" Bytes at "+pos+" in "+r);showVector(b);

            return STATUS_OK;
        } catch (Exception e) {
            error("IO error instoreNBytes", e);
            return STATUS_ERROR;  // en erreur

        }
    }

    /**
     *  read int � la position pos
     *   il est �vident que le status ne peut �tre test� si on travaille avec -1 !!!
     * @param pos position
     * @param r fichier
     * @return valeur
     */
    public static final int readInt(long pos, RandomAccessFile r) {
        try {
            int[] b = new int[1];
            byte[] byteidx = new byte[4];
            r.seek(pos);
            r.read(byteidx);
            byteToint(b, 4, byteidx);
            return b[0];
        } catch (Exception e) {
            error("IO error readInt", e);
            return STATUS_ERROR;  // en erreur

        }
    }

    /**
     *  lire n bytes � cette position
     * retourne le vecteur si ok sinon null
     * @param n nombres de bytes � lire
     * @param pos position
     * @param r fichier
     * @return valeurs
     */
    public static final byte[] readBytes(int n, long pos, RandomAccessFile r) {
        if (n <= 0) {
            return null;
        }
        try {
            byte[] byteidx = new byte[n];
//            TimerNano trSEEK= new TimerNano("read byte",true);
            r.seek(pos); // position la t�te sur le d�but du block
//            long readtimeseek=trSEEK.stop(true);
//            msg("pos :"+pos+" length:"+byteidx.length+" : time "+readtimeseek+"[us]");
//            TimerNano trPOS= new TimerNano("read byte",true);
//        Runtime runtime = Runtime.getRuntime();
//        msg("memory alloc/free: " + (runtime.totalMemory()/1024/1024)+" / "+(runtime.freeMemory()/1024/1024)); //la m�moire actuellement utilis�e

            r.read(byteidx); // lire le vecteur
//            long readtime=trPOS.stop(true);
//            msg("     readbyte time "+ readtime+"[us]");//msg("read "+n+" Bytes at "+pos+" in "+r);showVector(byteidx);

            return byteidx;
        } catch (Exception e) {
            error("IO error readBytes", e);
            return null;  // en erreur

        }
    }

    /**
     *  converti de byte en String avec un encodage 'UTF-8' par exemple
     * @param bs bytes � convertir
     * @param encode encodage
     * @return valeur convertie
     */
    public static final String convertBytes2String(byte[] bs, String encode) {
        //showVector(bs);
        try {
            String s = new String(bs, encode);
            //msg(s);
            return s;
        } catch (Exception e) {
            error("IO error convertBytes2String", e);
        }
        return null;
    }

    /**
     *  converti de String en byte avec un encodage 'UTF-8' par exemple
     * @param s cha�ne � convertir
     * @param encode encodage
     * @return bytes
     */
    public static final byte[] convertString2Bytes(String s, String encode) {
        try {
            byte[] bs = s.getBytes(encode);
            //msg(s);
            //showVector(bs);
            return bs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** copier le vecteur d'entiers dans un  vecteur de bytes.
     * @param v vecteur d'entier
     * @param lb longeur de copie en byte (*4)
     * @param b vecteur de bytes
     */
    public static final void intTobyte(int[] v, int lb, byte[] b) {
        // limit to 500'000'000!
        int r;
        for (int i = 0; i < lb; i += 4) {
            //msg(i+","+(i>>>2));
            r = v[i >>> 2]; // div 4 !!!

            b[i + 3] = (byte) r;
            r >>= 8;
            b[i + 2] = (byte) r;
            r >>= 8;
            b[i + 1] = (byte) r;
            r >>= 8;
            b[i] = (byte) r;
        }
    }

    /** copier le vecteur de bytes dans un vecteur d'entiers.
     * @param v vecteur d'entier
     * @param lb longeur de copie en byte (*4)
     * @param b vecteur de bytes
     */
    public static final void byteToint(int[] v, int lb, byte[] b) {
        // limit to 500'000'000!
        int t;
        for (int i = 0; i < lb; i += 4) {
            // msg(i+","+(i>>>2));
            t = b[i];
            t <<= 8;
            t |= (b[i + 1] << 24) >>> 24;
            t <<= 8;
            t |= (b[i + 2] << 24) >>> 24;
            t <<= 8;
            t |= (b[i + 3] << 24) >>> 24;
            v[i >>> 2] = t; // div 4 !!!

        }
    }

    /** incr�mente la taille d'un vecteur et recopie la totalit� si plus grand,
     * si l'incr�ment est n�gatif seul les n premiers bytes sont copi�s
     * @param v vecteur d'entier
     * @param increment +ajout/-troncation
     * @return nouveau vecteur
     */
    public static final int[] incrementSize(int[] v, int increment) {
        // si l'incr�ment est n�gatif seul les n premiers bytes sont copi�s
        int oldSize = v.length;
        int newSize = oldSize + increment;
        int[] it = new int[newSize];
        if (oldSize < newSize) {
            System.arraycopy(v, 0, it, 0, oldSize);
        } else {
            System.arraycopy(v, 0, it, 0, newSize);
        }
        return it;
    }

    /** copier le vecteur p dans un nouveau vecteur de longueur l.
     * Si p est plus petit que l alors la fin sera remplie de z�ro.
     * Si p est plus grand alors la fin de p sera tronqu�e.
     * @param p vecteur
     * @param l longueur du nouveau vecteur
     * @return nouveau vecteur
     */
    public static final int[] copyVector(int l, int[] p) {
        int[] r = new int[l];
        int maxi = Math.min(l, p.length); //  if p.lenght<l last will be fill with 0

        System.arraycopy(p, 0, r, 0, maxi);
        return r;
    }

    /** copier le vecteur p dans un nouveau vecteur de longueur l.
     * Si p est plus petit que l alors la fin sera remplie de z�ro.
     * Si p est plus grand alors la fin de p sera tronqu�e.
     * @param p vecteur
     * @param l longueur du nouveau vecteur
     * @return nouveau vecteur
     */
    public static final float[] copyVector(int l, float[] p) {
        float[] r = new float[l];
        int maxi = Math.min(l, p.length); //  if p.lenght<l last will be fill with 0

        System.arraycopy(p, 0, r, 0, maxi);
        return r;
    }

    /** copier le vecteur p dans un nouveau vecteur de longueur l.
     * Si p est plus petit que l alors la fin sera remplie de z�ro.
     * Si p est plus grand alors la fin de p sera tronqu�e.
     * @param p vecteur
     * @param l longueur du nouveau vecteur
     * @return nouveau vecteur
     */
    public static final byte[] copyVector(int l, byte[] p) {
        byte[] r = new byte[l];
        int maxi = Math.min(l, p.length); //  if p.lenght<l last will be fill with 0

        System.arraycopy(p, 0, r, 0, maxi);
        return r;
    }

    /** compresser un int[], genre ZIP
     * @param bi le vecteur a compresser
     * @return le vecteur de byte compress�
     */
    public synchronized static final byte[] compress(int[] bi) { // partage un compresseur commun en cas de //

        byte[] bb = new byte[bi.length * 4];
        byte[] bytecomp = new byte[bi.length * 4];
        intTobyte(bi, bi.length * 4, bb);

        def2.setInput(bb);
        def2.finish();
        def2.deflate(bytecomp);
        //showVector(bytecomp);
        int compresslength = def2.getTotalOut();
        def2.reset();  // free for a new use
        //msg("save :"+compresslength+" for:"+bi.length);

        return copyVector(compresslength, bytecomp);
    }

    /** compresser un byte[], genre ZIP
     * @param bb le vecteur a compresser
     * @return le vecteur de byte compress�
     */
    public synchronized static final byte[] compress(byte[] bb) { // partage un compresseur commun en cas de //

        byte[] bytecomp = new byte[bb.length + 100];  // pour les petites choses la compression peut �tre plus grande

        def.setInput(bb);
        def.finish();
        def.deflate(bytecomp);
        //showVector(bytecomp);
        int compresslength = def.getTotalOut();
        def.reset();  // free for a new use
        //msg("save :"+compresslength+" for:"+bi.length);

        return copyVector(compresslength, bytecomp);
    }

    /** compresser un int[] Variable Int
     * @param bi le vecteur a compresser
     * @return le vecteur de byte compress�
     */
    public static final byte[] compressVInt(int[] bi) { //

        byte[] bb = new byte[bi.length * 4];  // normalement doit �tre *5, on esp�re une compression peut donc g�n�rer des erreurs si pas adapt�!!!

        int k = 0;
        for (int j = 0; j < bi.length; j++) {
            int itov = bi[j];
            while ((itov & ~0x7F) != 0) {
                bb[k] = (byte) ((itov & 0x7f) | 0x80);
                k++;
                itov >>>= 7;
            }
            bb[k] = (byte) itov;
            k++;
        }
        // recopie la partie ok
        byte[] r = new byte[k];
        System.arraycopy(bb, 0, r, 0, k);
        return r;
    }

    /** d�compresser un int[] Variable Int
     * @param bb le vecteur a d�compresser
     * @param maxSize la taille maximum du vecteur attendu
     * @return le vecteur de int d�compress�
     */
    public static final int[] decompressVInt(byte[] bb, int maxSize) {
        //  TimerNano trPOS= new TimerNano("decompress",true);
        //msg("decompressVInt:"+maxSize);
        int[] bi = new int[maxSize];
        int k = 0;
        for (int j = 0; j < maxSize; j++) {
            byte b = bb[k];
            k++;
            bi[j] = b & 0x7F;
            for (int shift = 7; (b & 0x80) != 0; shift += 7) {
                b = bb[k];
                k++;
                bi[j] |= (b & 0x7F) << shift;
                //msg("j:"+j+" k:"+k+" bi[j]"+bi[j]);
            }
        }
//        long readtime=trPOS.stop(true);
//        msg("   decompress+"+bb.length+" > "+bi.length*4+" : time "+ readtime+"[us]");
//        showVector(bi);
        return bi;
    }

    /** compresser un int[] Variable Int
     * @param bi le vecteur a compresser
     * @return le vecteur de byte compress�
     */
    public static final byte[] compressVInt(int[] bi, int to) { //

        byte[] bb = new byte[to * 4];  // normalement doit �tre *5, on esp�re une compression peut donc g�n�rer des erreurs si pas adapt�!!!

        int k = 0;
        for (int j = 0; j < to; j++) {
            int itov = bi[j];
            while ((itov & ~0x7F) != 0) {
                bb[k] = (byte) ((itov & 0x7f) | 0x80);
                k++;
                itov >>>= 7;
            }
            bb[k] = (byte) itov;
            k++;
        }
        // recopie la partie ok
        byte[] r = new byte[k];
        System.arraycopy(bb, 0, r, 0, k);
        return r;
    }

    /** d�compresser un byte[]
     * @param bb le vecteur a d�compresser
     * @param maxSize la taille maximum du vecteur attendu
     * @return le vecteur de int d�compress�
     */
    public synchronized static final int[] decompress(byte[] bb, int maxSize) { // partage un compresseur commun en cas de //
        //msg("bb length:"+bb.length);
        //msg("maxSize:"+maxSize);

        int[] bi = new int[maxSize];
        byte[] bytedecomp = new byte[maxSize * 4];
        try {
            inf2.setInput(bb);
            inf2.inflate(bytedecomp);
            inf2.reset();

        } catch (Exception e) {
            error("ZIP decompress", e);
            return null;
        }
        byteToint(bi, bytedecomp.length, bytedecomp);
        return bi;
    }

    /** d�compresser un byte[]
     * @param bb le vecteur a d�compresser
     * @param realSize la taille maximum du vecteur attendu
     * @return le vecteur de int d�compress�
     */
    public synchronized static final byte[] decompress(int realSize, byte[] bb) { // partage un compresseur commun en cas de //

        byte[] bytedecomp = new byte[realSize];
        try {
            inf.setInput(bb);
            inf.inflate(bytedecomp);
            inf.reset();

        } catch (Exception e) {
            error("ZIP decompress", e);
            return null;
        }
        return bytedecomp;
    }

    /**
     * indique la m�moire utilis�e par l'indexeur
     * @return la m�moire utilis�e
     */
    public static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory(); //la m�moire actuellement utilis�e

    }

    /**
     * affiche la m�moire utilis�e par l'indexeur
     * @param s libell� de l'affichage
     */
    public static void usedMemory(String s) {
        Runtime runtime = Runtime.getRuntime();
        msg(s + ": " + (runtime.totalMemory() - runtime.freeMemory())); //la m�moire actuellement utilis�e

    }

    /**
     * Compacte la m�moire et affiche la m�moire utilis�e par l'indexeur 
     * @param s libell� de l'affichage
     */
    public static void compactMemory(String s) {
        usedMemory("Before GC " + s);
        System.gc();
        usedMemory("After GC " + s);
    }
}
