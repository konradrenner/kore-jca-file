/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kore.jca.file.store;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import javax.validation.constraints.Min;
import org.kore.jca.file.FileConnection;
import org.kore.jca.file.FileSource;

/**
 *
 * @author Konrad Renner
 */
@ConnectionDefinition(connectionFactory = FileSource.class,
        connectionFactoryImpl = FileStore.class,
        connection = FileConnection.class,
        connectionImpl = DefaultFileConnection.class)
public class GenericManagedConnectionFactory implements ManagedConnectionFactory, Serializable {

    private PrintWriter out;
    private String rootDirectory;

    public GenericManagedConnectionFactory() {
        out = new PrintWriter(System.out);
        out.println("#GenericManagedConnectionFactory.constructor");
    }

    @Min(1)
    @ConfigProperty(defaultValue = "./store/", supportsDynamicUpdates = true, description = "The root folder of the file store")
    public void setRootDirectory(String rootDirectory) {
        out.println("#FileBucket.setRootDirectory: " + rootDirectory);
        this.rootDirectory = rootDirectory;
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        out.println("#GenericManagedConnectionFactory.createConnectionFactory,1");
        return new FileStore(out, this, cxManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        out.println("#GenericManagedConnectionFactory.createManagedFactory,2");
        return new FileStore(out, this, null);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo info) {
        out.println("#GenericManagedConnectionFactory.createManagedConnection");
        return new GenericManagedConnection(out, this.rootDirectory, this, info);
    }

    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo info)
            throws ResourceException {
        out.println("#GenericManagedConnectionFactory.matchManagedConnections Subject " + subject + " Info: " + info);
        for (Object con : connectionSet) {
            GenericManagedConnection gmc = (GenericManagedConnection) con;
            ConnectionRequestInfo connectionRequestInfo = gmc.getConnectionRequestInfo();
            if ((info == null) || connectionRequestInfo.equals(info)) {
                return gmc;
            }
        }
        throw new ResourceException("Cannot find connection for info!");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        out.println("#GenericManagedConnectionFactory.setLogWriter");
        this.out = out;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        out.println("#GenericManagedConnectionFactory.getLogWriter");
        return this.out;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenericManagedConnectionFactory other = (GenericManagedConnectionFactory) obj;
        if (!Objects.equals(this.rootDirectory, other.rootDirectory)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.rootDirectory);
        return hash;
    }
}
