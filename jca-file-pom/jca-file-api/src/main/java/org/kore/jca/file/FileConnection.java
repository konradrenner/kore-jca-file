/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kore.jca.file;

import java.nio.file.attribute.FileTime;

/**
 *
 * @author Konrad Renner
 */
public interface FileConnection extends AutoCloseable {

    /**
     * Creates or replaces the given file, specified by the filename, with the
     * given content
     *
     * @param fileName
     * @param content
     */
    void write(String fileName, byte[] content);

    /**
     * Deletes a file with the given name
     *
     * @param fileName
     */
    void delete(String fileName);

    /**
     * Fetches the content of a file with the given name
     *
     * @param fileName
     * @return byte[] - file content
     */
    byte[] fetch(String fileName);

    /**
     * Returns the time, when the file was the last time modified
     *
     * @param fileName
     * @return FileTime - last modification
     */
    FileTime lastModified(String fileName);
}
