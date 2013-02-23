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

package org.olanto.idxvli.mapio;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.util.Messages.*;

/**
 * g�re une structure de fichiers directement mapp� en m�moire.
 * 

 *
 *
 * g�re des fichiers � travers les buffers (impl�mentation de base).
 * cette impl�mentation est FULL.
 *
 */
public class FullIOMap implements IOMap {

    private static final boolean verbose = true;
    private FileChannel channel;
    private readWriteMode RW;
    private MappedByteBuffer[] mapping;
    private int slice2n = 13; // 13=8k   /// taille des slice
    private int comp32;
    private int sliceSize;
    private int maxSlice2n = 10;// 10=1k  /// nbr max de slice
    private int maxSlice;
    private long maxLength = (long) Math.pow(2, slice2n + maxSlice2n); // longueur maximum du mapping

    protected FullIOMap() {
    }  // sert pour le get

    /** retourne un gestionnaire de buffer des IO */
    public final IOMap get(FileChannel _channel, readWriteMode _RW) throws IOException {
        channel = _channel;
        RW = _RW;
        sliceSize = (int) Math.pow(2, slice2n);
        maxSlice = (int) (maxLength / sliceSize);
        comp32 = 32 - slice2n;
        mapping = new MappedByteBuffer[maxSlice];
        msg("FullIOMap.get slice2n:" + slice2n + " maxSlice:" + maxSlice + " sliceSize:" + sliceSize + " maxLength:" + maxLength);
        return this;
    }

    /** retourne un gestionnaire de buffer des IO, sp�cifiant les param�tres du mapping*/
    public final IOMap get(FileChannel _channel, readWriteMode _RW,
            int _slice2n, long _maxLength) throws IOException {
        slice2n = _slice2n;
        maxLength = _maxLength;
        return get(_channel, _RW);
    }

    /** fermer le gestionnaire */
    public final void close() throws IOException {
        for (int i = 0; i < maxSlice; i++) {
            if (mapping[i] != null) {
                msg("FullIOMap close i:" + i);
                if (RW == readWriteMode.rw) {
                    mapping[i].force();
                } // force l'�criture
                mapping[i] = null;
            }
        }
    }

    private final int getSlice(long pos) {
        return (int) pos >> slice2n;
    }

    private final int getRelPos(long pos) {
        return ((int) pos) << comp32 >>> comp32;  // peut �tre optimis� avec un masque !!!
    }

    private final void forceInMemory(int slice) throws IOException {
        if (mapping[slice] != null) {// d�ja en m�moire
            return;
        } else {  // map la tranche
            switch (RW) {
                case rw:
                    mapping[slice] = channel.map(FileChannel.MapMode.READ_WRITE, slice << slice2n, sliceSize);
                    break;
                case r:
                    mapping[slice] = channel.map(FileChannel.MapMode.READ_ONLY, slice << slice2n, sliceSize);
                    break;
            }
        }
    }

    /**  lire ces bytes � cette position*/
    public final void read(byte[] data, long pos) throws IOException {
        int readed = 0;
        while (data.length - readed != 0) {
            int s = getSlice(pos);
            int r = getRelPos(pos);
            if (verbose) {
                msg("read pos:" + pos + " slice:" + s + " rel:" + r + " readed:" + readed);
            }
            int maxInThisSlice = sliceSize - r;  // maximum que l'on peut �crire dans cette tranche
            int lengthReaded = Math.min(maxInThisSlice, data.length - readed); // longueur � �crire dans cette tranche
            if (verbose) {
                msg("   maxInThisSlice:" + maxInThisSlice + " lengthReaded:" + lengthReaded);
            }
            get(data, readed, lengthReaded, s, r);
            readed += lengthReaded;  // ajuste la longueur �crite;
            pos += lengthReaded; // ajuste la position
        }
    }

    /** lire data[from,from + length] dans la tranche slice depuis la position rel*/
    private final void get(byte[] data, int from, int length, int slice, int rel) throws IOException {
        forceInMemory(slice);
        mapping[slice].position(rel);
        mapping[slice].get(data, from, length);
    }

    /** �crire  ces bytes � cette position*/
    public final void write(byte[] data, long pos) throws IOException {
        int written = 0;
        while (data.length - written != 0) {
            int s = getSlice(pos);
            int r = getRelPos(pos);
            if (verbose) {
                msg("write pos:" + pos + " slice:" + s + " rel:" + r + " written:" + written);
            }
            int maxInThisSlice = sliceSize - r;  // maximum que l'on peut �crire dans cette tranche
            int lengthWritten = Math.min(maxInThisSlice, data.length - written); // longueur � �crire dans cette tranche
            if (verbose) {
                msg("   maxInThisSlice:" + maxInThisSlice + " lengthWritten:" + lengthWritten);
            }
            put(data, written, lengthWritten, s, r);
            written += lengthWritten;  // ajuste la longueur �crite;
            pos += lengthWritten; // ajuste la position
        }
    }

    /** �crire data[from,from + length] dans la tranche slice depuis la position rel*/
    private final void put(byte[] data, int from, int length, int slice, int rel) throws IOException {
        forceInMemory(slice);
        mapping[slice].position(rel);
        mapping[slice].put(data, from, length);
    }

    /**  lire un int � cette position*/
    public final int readInt(long pos) throws IOException {
        int s = getSlice(pos);
        int r = getRelPos(pos);
        int maxInThisSlice = sliceSize - r;
        if (maxInThisSlice >= 4) {
            forceInMemory(s);
            mapping[s].position(r);
            return mapping[s].getInt();
        } else {
            error_fatal("not align, int across two slice");
        } // pas alig�
        return 0;
    }

    /** �crire un int � cette position*/
    public final void writeInt(int data, long pos) throws IOException {
        int s = getSlice(pos);
        int r = getRelPos(pos);
        int maxInThisSlice = sliceSize - r;
        // msg("writeInt pos:"+pos+" r:"+r+" maxInThisSlice:"+maxInThisSlice);
        if (maxInThisSlice >= 4) {
            forceInMemory(s);
            mapping[s].position(r);
            mapping[s].putInt(data);
        } else {
            error_fatal("not align, int across two slice pos:" + pos + " r:" + r + " maxInThisSlice:" + maxInThisSlice);
        } // pas alig�

    }

    /**  lire un long � cette position*/
    public final long readLong(long pos) throws IOException {
        int s = getSlice(pos);
        int r = getRelPos(pos);
        int maxInThisSlice = sliceSize - r;
        if (maxInThisSlice >= 8) {
            forceInMemory(s);
            mapping[s].position(r);
            return mapping[s].getLong();
        } else {
            error_fatal("not align, int across two slice");
        } // pas alig�
        return 0;
    }

    /** �crire un long � cette position*/
    public final void writeLong(long data, long pos) throws IOException {
        int s = getSlice(pos);
        int r = getRelPos(pos);
        int maxInThisSlice = sliceSize - r;
        // msg("writeInt pos:"+pos+" r:"+r+" maxInThisSlice:"+maxInThisSlice);
        if (maxInThisSlice >= 8) {
            forceInMemory(s);
            mapping[s].position(r);
            mapping[s].putLong(data);
        } else {
            error_fatal("not align, int across two slice pos:" + pos + " r:" + r + " maxInThisSlice:" + maxInThisSlice);
        } // pas alig�

    }

    public final long getMaxLength() {
        return maxLength;
    }
}
