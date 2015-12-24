/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kore.jca.file.store;

import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.TransactionSupport;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

/**
 *
 * @author Konrad Renner
 */
@Connector(reauthenticationSupport = false,
        transactionSupport = TransactionSupport.TransactionSupportLevel.LocalTransaction)
public class FileAdapter implements ResourceAdapter {

    @Override
    public void start(BootstrapContext bc) {
    }

    @Override
    public void stop() {
    }

    @Override
    public void endpointActivation(MessageEndpointFactory mef, ActivationSpec as) {
    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory mef, ActivationSpec as) {
    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] ass) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
