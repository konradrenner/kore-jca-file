/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kore.jca.file.store;

import java.io.Closeable;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import javax.resource.ResourceException;
import javax.resource.spi.*;
import static javax.resource.spi.ConnectionEvent.*;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

/**
 *
 * @author Konrad Renner
 */
public class GenericManagedConnection implements ManagedConnection, LocalTransaction, Closeable {

    private final ManagedConnectionFactory mcf;
    private PrintWriter out;
    private DefaultFileConnection fileConnection;
    private final ConnectionRequestInfo connectionRequestInfo;
    private final List<ConnectionEventListener> listeners;
    private final String rootDirectory;

    GenericManagedConnection(PrintWriter out, String rootDirectory, ManagedConnectionFactory mcf, ConnectionRequestInfo connectionRequestInfo) {
        this.out = out;
        this.rootDirectory = rootDirectory;
        out.println("#GenericManagedConnection");
        this.mcf = mcf;
        this.connectionRequestInfo = connectionRequestInfo;
        this.listeners = new LinkedList<>();
        this.fileConnection = new DefaultFileConnection(out, this.rootDirectory, this);
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo)
            throws ResourceException {
        out.println("#GenericManagedConnection.getConnection");
        return fileConnection;
    }

    @Override
    public void destroy() {
        out.println("#GenericManagedConnection.destroy");
        this.fileConnection.destroy();
    }

    @Override
    public void cleanup() {
        out.println("#GenericManagedConnection.cleanup");
        this.fileConnection.clear();
    }

    @Override
    public void associateConnection(Object connection) {
        out.println("#GenericManagedConnection.associateConnection " + connection);
        this.fileConnection = (DefaultFileConnection) connection;

    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        out.println("#GenericManagedConnection.addConnectionEventListener");
        this.listeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        out.println("#GenericManagedConnection.removeConnectionEventListener");
        this.listeners.remove(listener);
    }

    @Override
    public XAResource getXAResource()
            throws ResourceException {
        out.println("#GenericManagedConnection.getXAResource");
        throw new ResourceException("XA protocol is not supported by the file-jca adapter");
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        out.println("#GenericManagedConnection.getLocalTransaction");
        return this;
    }

    @Override
    public ManagedConnectionMetaData getMetaData()
            throws ResourceException {
        out.println("#GenericManagedConnection.getMetaData");
        return new ManagedConnectionMetaData() {

            public String getEISProductName()
                    throws ResourceException {
                out.println("#GenericManagedConnection.getEISProductName");
                return "File JCA";
            }

            public String getEISProductVersion()
                    throws ResourceException {
                out.println("#GenericManagedConnection.getEISProductVersion");
                return "1.0";
            }

            public int getMaxConnections()
                    throws ResourceException {
                out.println("#GenericManagedConnection.getMaxConnections");
                return 5;
            }

            public String getUserName()
                    throws ResourceException {
                return null;
            }
        };
    }

    @Override
    public void setLogWriter(PrintWriter out)
            throws ResourceException {
        System.out.println("#GenericManagedConnection.setLogWriter");
        this.out = out;
    }

    @Override
    public PrintWriter getLogWriter()
            throws ResourceException {
        System.out.println("#GenericManagedConnection.getLogWriter");
        return out;
    }

    ConnectionRequestInfo getConnectionRequestInfo() {
        return connectionRequestInfo;
    }

    @Override
    public void begin() throws ResourceException {
        this.fileConnection.begin();
        this.fireConnectionEvent(LOCAL_TRANSACTION_STARTED);
    }

    @Override
    public void commit() throws ResourceException {
        this.fileConnection.commit();
        this.fireConnectionEvent(LOCAL_TRANSACTION_COMMITTED);
    }

    @Override
    public void rollback() throws ResourceException {
        this.fileConnection.rollback();
        this.fireConnectionEvent(LOCAL_TRANSACTION_ROLLEDBACK);
    }

    public void fireConnectionEvent(int event) {
        ConnectionEvent connnectionEvent = new ConnectionEvent(this, event);
        connnectionEvent.setConnectionHandle(this.fileConnection);
        for (ConnectionEventListener listener : this.listeners) {
            switch (event) {
                case LOCAL_TRANSACTION_STARTED:
                    listener.localTransactionStarted(connnectionEvent);
                    break;
                case LOCAL_TRANSACTION_COMMITTED:
                    listener.localTransactionCommitted(connnectionEvent);
                    break;
                case LOCAL_TRANSACTION_ROLLEDBACK:
                    listener.localTransactionRolledback(connnectionEvent);
                    break;
                case CONNECTION_CLOSED:
                    listener.connectionClosed(connnectionEvent);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event: " + event);
            }
        }
    }

    @Override
    public void close() {
        this.fireConnectionEvent(CONNECTION_CLOSED);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenericManagedConnection other = (GenericManagedConnection) obj;
        if (this.connectionRequestInfo != other.connectionRequestInfo && (this.connectionRequestInfo == null || !this.connectionRequestInfo.equals(other.connectionRequestInfo))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.connectionRequestInfo != null ? this.connectionRequestInfo.hashCode() : 0);
        return hash;
    }
}
