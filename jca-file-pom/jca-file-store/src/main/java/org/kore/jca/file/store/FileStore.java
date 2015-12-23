/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kore.jca.file.store;

import java.io.PrintWriter;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;
import org.kore.jca.file.FileConnection;
import org.kore.jca.file.FileSource;

/**
 *
 * @author Konrad Renner
 */
public class FileStore implements FileSource {

    private static final long serialVersionUID = 1L;
    private ManagedConnectionFactory mcf;
    private Reference reference;
    private ConnectionManager cm;
    private PrintWriter out;

    public FileStore(PrintWriter out, ManagedConnectionFactory mcf, ConnectionManager cm) {
        out.println("#FileBucketStore");
        this.mcf = mcf;
        this.cm = cm;
        this.out = out;
    }

    @Override
    public FileConnection getConnection() {
        out.println("#FileBucketStore.getConnection " + this.cm + " MCF: " + this.mcf);
        try {
            return (FileConnection) cm.allocateConnection(mcf, getConnectionRequestInfo());
        } catch (ResourceException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public Reference getReference() {
        return reference;
    }

    private ConnectionRequestInfo getConnectionRequestInfo() {
        return new ConnectionRequestInfo() {

            @Override
            public boolean equals(Object obj) {
                return true;
            }

            @Override
            public int hashCode() {
                return 1;
            }
        };
    }
}
