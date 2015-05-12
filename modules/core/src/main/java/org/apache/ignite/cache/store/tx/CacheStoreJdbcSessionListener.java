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
import org.apache.ignite.internal.util.typedef.internal.*;

import javax.cache.*;
import javax.sql.*;
import java.sql.*;

/**
 * TODO
 */
public class CacheStoreJdbcSessionListener implements CacheStoreSessionListener {
    /** */
    public static final String CONN_KEY = "__conn_";

    /** */
    private DataSource dataSrc;

    public CacheStoreJdbcSessionListener(DataSource dataSrc) {
        this.dataSrc = dataSrc;
    }

    public DataSource getDataSource() {
        return dataSrc;
    }

    public void setDataSource(DataSource dataSrc) {
        this.dataSrc = dataSrc;
    }

    /** {@inheritDoc} */
    @Override public void onSessionStart(CacheStoreSession ses) {
        assert !ses.properties().containsKey(CONN_KEY);

        try {
            Connection conn = dataSrc.getConnection();

            conn.setAutoCommit(false);

            ses.properties().put(CONN_KEY, conn);
        }
        catch (SQLException e) {
            throw new CacheException(e);
        }
    }

    /** {@inheritDoc} */
    @Override public void onSessionEnd(CacheStoreSession ses, boolean commit) {
        Connection conn = ses.<String, Connection>properties().remove(CONN_KEY);

        if (conn != null) {
            try {
                if (commit)
                    conn.commit();
                else
                    conn.rollback();
            }
            catch (SQLException e) {
                throw new CacheException(e);
            }
            finally {
                U.closeQuiet(conn);
            }
        }
    }
}
