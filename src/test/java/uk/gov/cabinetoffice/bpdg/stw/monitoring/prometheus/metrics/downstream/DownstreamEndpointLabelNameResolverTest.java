/*
 * Copyright 2023 Crown Copyright (Single Trade Window)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream;

import static io.micrometer.core.ipc.http.HttpSender.Method.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DownstreamEndpointLabelNameResolverTest {
  DownstreamEndpointLabelNameResolver downstreamEndpointLabelNameResolver;

  static Stream<Map<String, Map<String, String>>> metricsRequestPathConfigMapSource() {
    return Stream.of(
        Map.of(
            "/api/v2/commodities/123456789",
            Map.of("GET-/api/v2/commodities/\\d+", "getCommodityGB")),
        Map.of(
            "/api/v1/commodities/123456789/duties",
            Map.of("GET-/api/v1/commodities/\\d+/duties", "getTariffAndTaxes")),
        Map.of(
            "/api/commodities/123456789/restrictive-measures",
            Map.of("GET-/api/commodities/\\d+/restrictive-measures", "getRestrictiveMeasures")),
        Map.of(
            "/api/commodities/123456789/additional-codes",
            Map.of("GET-/api/commodities/\\d+/additional-codes", "getAdditionalCodes")));
  }

  @BeforeEach
  void setUp() {}

  @ParameterizedTest
  @MethodSource("metricsRequestPathConfigMapSource")
  void testGetRequestPathMetricsTest(Map<String, Map<String, String>> requestPathConfigMap) {
    // given
    Map<String, String> patternMap = requestPathConfigMap.values().stream().findFirst().get();
    String requestPath = requestPathConfigMap.keySet().stream().findFirst().get();
    String expected = patternMap.values().stream().findFirst().get();
    downstreamEndpointLabelNameResolver = new DownstreamEndpointLabelNameResolver(patternMap);
    // when
    String result = downstreamEndpointLabelNameResolver.get(GET.name(), requestPath);
    // then
    assertEquals(expected, result);
  }
}
