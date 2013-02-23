/**
 * ********
 * Copyright © 2010-2012 Olanto Foundation Geneva
 *
 * This file is part of myCAT.
 *
 * myCAT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.idxvli.server;

import java.io.File;
import java.rmi.RemoteException;
import static org.olanto.idxvli.IdxConstant.*;
import org.olanto.idxvli.ref.UtilsFiles;
import org.olanto.senseos.SenseOS;

/**
 * implémentation par défault pour les services de reconstruction des URL des
 * originaux
 *
 */
public class OriginalService_Default implements OriginalService {

    /**
     * Pour demander avoir une url à partir d'un nom de fichier indexé
     */
    @Override
    public String getURL(String d) throws RemoteException {
        String lang = d.substring(0, 2);
        String path = d.substring(3, d.length() - 4).replace("¦", "/");  //
        int posExt = path.lastIndexOf(".");
        String original = path.substring(0, posExt) + "_" + lang + path.substring(posExt);
        if (ORIGINAL_FULL_URL.equals("HTTP")) {
            String url = "http://" + ORIGINAL_HOST
                    + ":" + ORIGINAL_PORT
                    + "/original/" + original;
            return url;
        } else {
            String url = ORIGINAL_FULL_URL + original;
            return url;
        }
    }

    @Override
    public String getPath(String d) throws RemoteException {
        String lang = d.substring(0, 2);
        String path = d.substring(3, d.length() - 4).replace("¦", "/");  //
        int posExt = path.lastIndexOf(".");
        String original = path.substring(0, posExt) + "_" + lang + path.substring(posExt);

        return SenseOS.getMYCAT_HOME() + "/corpus/docs/" + original;

    }

    @Override
     public String getSaveZipName(String d) throws RemoteException {
         String lang = d.substring(0, 2);
        String path = d.substring(3, d.length() - 4).replace("¦", "_");  //
        int posExt = path.lastIndexOf(".");
        String original = path.substring(0, posExt) + "_" + lang + path.substring(posExt);

        return  original;

    }
}
