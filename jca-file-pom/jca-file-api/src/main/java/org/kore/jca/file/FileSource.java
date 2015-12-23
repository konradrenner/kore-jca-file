/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kore.jca.file;

import java.io.Serializable;
import javax.resource.Referenceable;

/**
 *
 * @author Konrad Renner
 */
public interface FileSource extends Serializable, Referenceable {

    /**
     * Attempts to establish a connection with the file source that this
     * FileSource object represents.
     *
     * @return FileConnection
     */
    FileConnection getConnection();
}
