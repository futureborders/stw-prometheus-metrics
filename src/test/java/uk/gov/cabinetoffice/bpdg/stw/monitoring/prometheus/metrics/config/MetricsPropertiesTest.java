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

package uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@SpringBootTest(classes = {TestConfig.class})
@EnableConfigurationProperties(MetricsProperties.class)
public class MetricsPropertiesTest {

    @Autowired
    private MetricsProperties metricsProperties;

    @Test
    public void shouldBindYmlConfigToBeans() {
        assertThat(metricsProperties.getInbound().getExcludedPathsRegex()).contains("GET-.*/private/.*");
        assertThat(metricsProperties.getInbound().getMethodPathRegexToMetricName()).contains(entry("GET-/api/users", "getUsers"));
        assertThat(metricsProperties.getInbound().getMethodPathRegexToMetricName()).contains(entry("POST-/api/users", "createUser"));

        assertThat(metricsProperties.getInbound().isIgnoreUnknownUri()).isFalse();

        assertThat(metricsProperties.getOutbound().getMethodPathRegexToMetricName()).contains(entry("GET-/api/users", "getUsers"));
    }
}
