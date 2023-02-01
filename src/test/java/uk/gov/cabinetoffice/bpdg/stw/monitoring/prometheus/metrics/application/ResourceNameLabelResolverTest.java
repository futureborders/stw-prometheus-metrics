/*
 * Copyright 2021 Crown Copyright (Single Trade Window)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNameLabelResolverTest {
    private ResourceNameLabelResolver resourceNameLabelResolver;

    private Map<String, String> methodPathRegexToMetricName;

    private List<String> excludedPathsRegex;

    @BeforeEach
    void setUp() {
        methodPathRegexToMetricName = Map.ofEntries(
                Map.entry("POST-.*/api/title", "postTitle"),
                Map.entry("POST-.*/api/content-prep", "contentPrep"));
        excludedPathsRegex = List.of("GET-.*/private/.*");

        resourceNameLabelResolver = new ResourceNameLabelResolver(methodPathRegexToMetricName, excludedPathsRegex, false);
    }

    @Test
    void shouldReturnNameMatchingPathAndMethod() {
        assertThat(resourceNameLabelResolver.getResourceName("POST", "/appContext/api/title").get()).isEqualTo("postTitle");
    }

    @Test
    void shouldReturnNameMatchingPathAndMethodForContentPrep() {
        assertThat(resourceNameLabelResolver.getResourceName("POST", "/appContext/api/content-prep").get()).isEqualTo("contentPrep");
    }

    @Test
    void shouldConvertMethodToUpperCase() {
        assertThat(resourceNameLabelResolver.getResourceName("post", "/appContext/api/title").get()).isEqualTo("postTitle");
    }

    @Test
    void shouldReturnUnknownWhenNoMatches() {
        assertThat(resourceNameLabelResolver.getResourceName("post", "/appContext/api/somethere").get()).isEqualTo("unknown");
    }

    @Test
    void shouldReturnEmptyWhenPathMatchesExclusionList() {
        assertThat(resourceNameLabelResolver.getResourceName("get", "/appContext/private/status")).isEmpty();
    }

    @Test
    public void shouldReturnEmptyForUnknownUriWhenSetToIgnoreUnknown() {
        resourceNameLabelResolver = new ResourceNameLabelResolver(methodPathRegexToMetricName, excludedPathsRegex, true);

        assertThat(resourceNameLabelResolver.getResourceName("get", "/appContext/api/unknown")).isEmpty();
    }

}
