/*-
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sling.dynamicinclude;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import org.apache.sling.dynamicinclude.pathmatcher.PrefixPathMatcher;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {

  private Configuration tested;

  @Before
  public void setUp() {
    tested = new Configuration();
  }

  @Test(expected = PatternSyntaxException.class)
  public void shouldThrowExceptionWhenRegexisInvalid() throws Exception {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(Configuration.PROPERTY_FILTER_PATH, "^(");

    tested.activate(null, properties);
  }

  @Test
  public void shouldSetDefaultValuesWhenPropertiesAreEmpty() throws Exception {
    Map<String, Object> properties = new HashMap<String, Object>();

    tested.activate(null, properties);

    assertThat(tested.getPathMatcher().getClass().isAssignableFrom(PrefixPathMatcher.class), is(true));
    assertThat(tested.getAddComment(), is(false));
    assertThat(tested.getIgnoreUrlParams().size(), is(0));
    assertThat(tested.getIncludeSelector(), is(Configuration.DEFAULT_FILTER_SELECTOR));
    assertThat(tested.getIncludeTypeName(), is(Configuration.DEFAULT_INCLUDE_TYPE));
    assertThat(tested.getRequiredHeader(), is(Configuration.DEFAULT_REQUIRED_HEADER));
    assertThat(tested.getTtl(), is(-1));
    assertThat(tested.isEnabled(), is(false));
    assertThat(tested.hasTtlSet(), is(false));
    assertThat(tested.isRewritePath(), is(false));
  }

  @Test
  public void shouldSetConfigurationValues() throws Exception {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(Configuration.PROPERTY_FILTER_PATH, "/content/test/path");
    properties.put(Configuration.PROPERTY_INCLUDE_TYPE, "ESI");
    properties.put(Configuration.PROPERTY_ADD_COMMENT, true);
    properties.put(Configuration.PROPERTY_COMPONENT_TTL, 60);
    properties.put(Configuration.PROPERTY_FILTER_ENABLED, true);
    properties.put(Configuration.PROPERTY_FILTER_RESOURCE_TYPES, new String[]{"test/resource/type"});
    properties.put(Configuration.PROPERTY_REQUIRED_HEADER, "CustomHeader: value");
    properties.put(Configuration.PROPERTY_FILTER_SELECTOR, "cache");
    properties.put(Configuration.PROPERTY_REWRITE_PATH, true);
    properties.put(Configuration.PROPERTY_IGNORE_URL_PARAMS, new String[] {"query"});

    tested.activate(null, properties);

    assertThat(tested.getPathMatcher().getClass().isAssignableFrom(PrefixPathMatcher.class), is(true));
    assertThat(tested.getAddComment(), is(true));
    assertThat(tested.getIgnoreUrlParams().size(), is(1));
    assertThat(tested.getIncludeSelector(), is("cache"));
    assertThat(tested.getIncludeTypeName(), is("ESI"));
    assertThat(tested.getRequiredHeader(), is("CustomHeader: value"));
    assertThat(tested.getTtl(), is(60));
    assertThat(tested.isEnabled(), is(true));
    assertThat(tested.hasTtlSet(), is(true));
    assertThat(tested.isRewritePath(), is(true));
    assertThat(tested.isSupportedResourceType("test/resource/type"), is(true));
  }
}
