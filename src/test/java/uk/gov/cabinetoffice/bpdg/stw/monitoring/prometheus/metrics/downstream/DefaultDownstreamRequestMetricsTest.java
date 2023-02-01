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

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.Collector.MetricFamilySamples.Sample;
import io.prometheus.client.CollectorRegistry;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.PrometheusMetricTypeFactory;

class DefaultDownstreamRequestMetricsTest {

  @Test
  void testRequestMetrics() {
    // given
    CollectorRegistry collectorRegistry = new CollectorRegistry();
    DefaultDownstreamRequestMetrics defaultDownstreamRequestMetrics =
        new DefaultDownstreamRequestMetrics(
            new PrometheusMetricTypeFactory(),
            collectorRegistry,
            new double[] {0.1, 0.5, 1.0, 3.0});
    // when
    defaultDownstreamRequestMetrics.record("downstream", "getCommodityGB", "200", 0L);
    // then
    final Enumeration<MetricFamilySamples> metricFamilySamples =
        collectorRegistry.metricFamilySamples();
    final Iterator<MetricFamilySamples> metricFamilySamplesIterator =
        metricFamilySamples.asIterator();
    final List<Sample> strings = metricFamilySamplesIterator.next().samples;
    assertTrue(strings.size() > 0);
  }
}
