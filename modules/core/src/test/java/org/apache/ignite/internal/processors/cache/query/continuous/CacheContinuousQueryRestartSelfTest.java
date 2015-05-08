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

package org.apache.ignite.internal.processors.cache.query.continuous;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.cache.query.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.marshaller.optimized.*;
import org.apache.ignite.spi.discovery.tcp.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.*;
import org.apache.ignite.testframework.*;
import org.apache.ignite.testframework.junits.common.*;
import org.jsr166.*;

import javax.cache.*;
import javax.cache.event.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.apache.ignite.cache.CacheRebalanceMode.*;
import static org.apache.ignite.cache.CacheWriteSynchronizationMode.*;

/**
 * Continuous queries test.
 */
public class CacheContinuousQueryRestartSelfTest extends GridCommonAbstractTest {
    /** IP finder. */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /** Grid count. */
    private static final int GRID_COUNT = 3;

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        CacheConfiguration cacheCfg = defaultCacheConfiguration();

        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setBackups(2);
        cacheCfg.setRebalanceMode(ASYNC);
        cacheCfg.setWriteSynchronizationMode(FULL_SYNC);
        cacheCfg.setReadThrough(true);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setCacheStoreFactory(new GridCacheContinuousQueryAbstractSelfTest.StoreFactory());
        cacheCfg.setLoadPreviousValue(true);

        cfg.setCacheConfiguration(cacheCfg);

        TcpDiscoverySpi disco = new TcpDiscoverySpi();

        disco.setIpFinder(IP_FINDER);

        cfg.setDiscoverySpi(disco);

        cfg.setMarshaller(new OptimizedMarshaller(false));

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        startGrids(GRID_COUNT);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    public void testNodeJoin() throws Exception {
        final ContinuousQuery<Integer, Integer> qry = new ContinuousQuery<>();

        final Collection<CacheEntryEvent<? extends Integer, ? extends Integer>> all = new ConcurrentLinkedDeque8<>();

        qry.setLocalListener(new CacheEntryUpdatedListener<Integer, Integer>() {
            @Override public void onUpdated(Iterable<CacheEntryEvent<? extends Integer, ? extends Integer>> evts) {
                for (CacheEntryEvent<? extends Integer, ? extends Integer> evt : evts)
                    all.add(evt);
            }
        });

        final AtomicInteger id = new AtomicInteger(GRID_COUNT);

        final int keyCount = 1000;

        final Random random = new Random(keyCount);

        IgniteInternalFuture fut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
            @Override public void run() {
                for (int i = 0; i < 2; ++i) {
                    IgniteCache cache = grid(0).cache(null);

                    int gridId = id.getAndIncrement();

                    try (QueryCursor<Cache.Entry<Integer, Integer>> ignored = grid(0).cache(null).query(qry)) {
                        cache.put(0, 0);

                        startGrid(gridId);

                        for (int j = 1; j < 40; j++)
                            cache.put(random.nextInt(), j);
                    }
                    catch (Exception e) {
                        throw new IgniteException(e);
                    }
                }
            }
        }, 5, "QueryThread");

        fut.get();

        for (int i = 0; i < id.get(); i++) {
            IgniteCache<Object, Object> cache = grid(i).cache(null);

            cache.removeAll();
        }

        for (int i = 0; i < GRID_COUNT; i++)
            assertEquals("Cache is not empty [entrySet=" + grid(i).cache(null).localEntries() +
                    ", i=" + i + ']', 0, grid(i).cache(null).localSize());
    }
}
