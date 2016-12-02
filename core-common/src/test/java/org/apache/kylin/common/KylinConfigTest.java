/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.kylin.common;

import static org.junit.Assert.*;

import java.util.Map;

import org.apache.kylin.common.util.LocalFileMetadataTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class KylinConfigTest extends LocalFileMetadataTestCase {

    @Before
    public void setUp() throws Exception {
        this.createTestMetadata();
    }

    @After
    public void after() throws Exception {
        this.cleanupTestMetadata();
    }

    @Test
    public void testMRConfigOverride() {
        KylinConfig config = KylinConfig.getInstanceFromEnv();
        Map<String, String> override = config.getMRConfigOverride();
        assertEquals(2, override.size());
        assertEquals("test1", override.get("test1"));
        assertEquals("test2", override.get("test2"));
    }
    
    @Test
    public void testBackwardCompatibility() {
        KylinConfig config = KylinConfig.getInstanceFromEnv();
        final String oldk = "kylin.test.bcc.old.key";
        final String newk = "kylin.test.bcc.new.key";
        
        assertNull(config.getOptional(oldk));
        assertNotNull(config.getOptional(newk));
        
        Map<String, String> override = Maps.newHashMap();
        override.put(oldk, "1");
        KylinConfigExt ext = KylinConfigExt.createInstance(config, override);
        assertEquals(ext.getOptional(oldk), null);
        assertEquals(ext.getOptional(newk), "1");
        assertNotEquals(config.getOptional(newk), "1");
        
        config.setProperty(oldk, "2");
        assertEquals(config.getOptional(newk), "2");
    }
    
    @Test
    public void testExtShareTheBase() {
        KylinConfig config = KylinConfig.getInstanceFromEnv();
        Map<String, String> override = Maps.newHashMap();
        KylinConfig configExt = KylinConfigExt.createInstance(config, override);
        
        assertTrue(config.properties == configExt.properties);
        config.setProperty("1234", "1234");
        assertEquals("1234", configExt.getOptional("1234"));
    }
}
