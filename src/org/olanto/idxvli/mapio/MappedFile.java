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
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import static org.olanto.idxvli.IdxEnum.*;

/**
 *
 * utilise la struture standard des RandomAccessFile (version de r�f�rence pour comparer la performance).
 * 

 *
 *
 * dans cette impl�mentation:
 * <pre> 
 *  - NOMAP: utilise les randomfile
 *  - CACHE et FULL sont impl�ment�s avec les IO_Map
 *
 * </pre>
 */
public class MappedFile implements DirectIOFile {

    /** taille des buffer 2^DEFAULT_SLICE_SIZE = 8kb */
    public static final int DEFAULT_SLICE_SIZE = 13; // 8k
    /** nbre max des buffers=1024 */
    public static final int DEFAULT_MAX_SLICE = 10;  // 1024 buffer
    private RandomAccessFile file;
    private FileChannel channel;
    private MappedByteBuffer buffer;
    private IOMap map;
    private readWriteMode RW = readWriteMode.rw;
    private long currentPosition;
    private MappingMode mapType;

    /** Creates a new instance of MappedFile */
    public MappedFile() {
    }

    public final void open(String fileName, MappingMode _mapType, readWriteMode _RW) throws IOException {
        open(fileName, _mapType, _RW, DEFAULT_SLICE_SIZE, DEFAULT_MAX_SLICE);
    }

    public final void open(String fileName, MappingMode _mapType, readWriteMode _RW,
            int _slice2n, long _maxLength) throws IOException {
        mapType = _mapType;
        RW = _RW;
        currentPosition = 0;   // la premi�re lecture ou �criture doit se faire � 0 si aucun seek n'est fait
        file = new RandomAccessFile(fileName, RW.name());
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            channel = file.getChannel();
            map = (new FullIOMap()).get(channel, RW, _slice2n, _maxLength);
        }
    }

    public final void close() throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.close(); // map=null; attention le close est trop lent -> erreurs
            channel.close();// channel = null;
            //System.gc();  //actuellement c'est le seul moyen pour rendre les buffeurs (v�rifier l'�volution chez SUN!!)
        }
        file.close();//file = null;
    }

    public final void read(byte[] data) throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.read(data, currentPosition);
            currentPosition += data.length;
        } else {
            file.read(data);
        } // pas de mapping
    }

    public final void write(byte[] data) throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.write(data, currentPosition);
            currentPosition += data.length;
        } else {
            file.write(data);
        } // pas de mapping
    }

    /**  lire un int */
    public final int readInt() throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            int res = map.readInt(currentPosition);
            currentPosition += 4;
            return res;
        } else {
            return file.readInt();
        } // pas de mapping
    }

    /** �crire un int */
    public final void writeInt(int data) throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.writeInt(data, currentPosition);
            currentPosition += 4;
        } else {
            file.writeInt(data);
        } // pas de mapping
    }

    /**  lire un long */
    public final long readLong() throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            long res = map.readLong(currentPosition);
            currentPosition += 8;
            return res;
        } else {
            return file.readLong();
        } // pas de mapping
    }

    /** �crire un long */
    public final void writeLong(long data) throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.writeLong(data, currentPosition);
            currentPosition += 8;
        } else {
            file.writeLong(data);
        } // pas de mapping
    }

    public final void seek(long pos) throws IOException {
        if (mapType != MappingMode.NOMAP) // il existe un mapping
        {
            currentPosition = pos;
        } else {
            file.seek(pos);
        } // pas de mapping
    }

    /** taille maximum du fichier */
    public final long getMaxLength() throws IOException {
        if (mapType != MappingMode.NOMAP) // il existe un mapping
        {
            return map.getMaxLength();
        } else {
            return Long.MAX_VALUE;
        }
    }
}
