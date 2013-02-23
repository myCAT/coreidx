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

package org.olanto.idxvli.ref;

import java.io.IOException;
import java.io.Serializable;

/** uploaded file managager ?
 * 
 * 
 */
public class UploadedFile implements Serializable {

    private String contentString;
    private byte[] contentBytes;
    private String fileName;

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(contentString);
        out.writeObject(contentBytes);
        out.writeObject(fileName);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        contentString = (String) in.readObject();
        contentBytes = (byte[]) in.readObject();
        fileName = (String) in.readObject();
    }

    public UploadedFile(String content, String fileName) {
        this.contentString = content;
        this.contentBytes = null;
        this.fileName = fileName;
    }

    public UploadedFile(byte[] content, String fileName) {
        this.contentString = null;
        this.contentBytes = content;
        this.fileName = fileName;
    }

    /**
     * @return the content
     */
    public String getContentString() {
        return contentString;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    public boolean isTxt() {
        return fileName.toLowerCase().endsWith(".txt");
    }

    public boolean isSdlxliff() {
        return fileName.toLowerCase().endsWith(".sdlxliff");
    }

    public boolean isDocx() {
        return fileName.toLowerCase().endsWith(".docx");
    }

    /**
     * @return the contentBytes
     */
    public byte[] getContentBytes() {
        return contentBytes;
    }
}
