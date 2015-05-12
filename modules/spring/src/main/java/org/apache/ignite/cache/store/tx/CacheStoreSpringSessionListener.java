/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.cache.store.tx;

import org.apache.ignite.cache.store.*;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

/**
 * TODO
 */
public class CacheStoreSpringSessionListener implements CacheStoreSessionListener {
    public static final String TX_STATUS_KEY = "__tx_status_";

    private PlatformTransactionManager txMgr;

    public PlatformTransactionManager getTransactionManager() {
        return txMgr;
    }

    public void setTransactionManager(PlatformTransactionManager txMgr) {
        this.txMgr = txMgr;
    }

    /** {@inheritDoc} */
    @Override public void onSessionStart(CacheStoreSession ses) {
        assert ses.isWithinTransaction();
        assert !ses.properties().containsKey(TX_STATUS_KEY);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setIsolationLevel(txIsolation(ses));

        ses.properties().put(TX_STATUS_KEY, txMgr.getTransaction(def));
    }

    /** {@inheritDoc} */
    @Override public void onSessionEnd(CacheStoreSession ses, boolean commit) {
        TransactionStatus tx = ses.<String, TransactionStatus>properties().get(TX_STATUS_KEY);

        if (tx != null) {
            if (commit)
                txMgr.commit(tx);
            else
                txMgr.rollback(tx);
        }
    }

    /**
     * Gets DB transaction isolation level based on ongoing cache transaction isolation.
     *
     * @return DB transaction isolation.
     */
    private int txIsolation(CacheStoreSession ses) {
        switch (ses.transaction().isolation()) {
            case READ_COMMITTED:
                return TransactionDefinition.ISOLATION_READ_COMMITTED;

            case REPEATABLE_READ:
                return TransactionDefinition.ISOLATION_REPEATABLE_READ;

            case SERIALIZABLE:
                return TransactionDefinition.ISOLATION_SERIALIZABLE;

            default:
                throw new IllegalStateException(); // Will never happen.
        }
    }
}
