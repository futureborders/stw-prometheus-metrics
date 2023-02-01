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

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.PrometheusMetricTypeFactory;

@Component
public class InboundRequestMetrics {
  private final Counter counter;
  private final Histogram latency;

  static final double BILLION = 1_000_000_000d;

  @Autowired
  public InboundRequestMetrics(
      PrometheusMetricTypeFactory metricTypeFactory,
      CollectorRegistry collectorRegistry,
      @Value(
              "#{'${metrics.inbound.histogram.bucketsUpperLimits.csv:0.1, 0.5, 1.0, 3.0}'.split('[,;] *')}")
          double[] bucketsUpperLimits) {
    counter =
        metricTypeFactory.createCounter(
            "application_responses",
            "Counters for application inbound endpoints",
            "resourceName",
            "status");

    latency =
        metricTypeFactory.createHistogram(
            "application_latency",
            "Timers for application inbound endpoints.",
            bucketsUpperLimits,
            "resourceName");

    counter.register(collectorRegistry);
    latency.register(collectorRegistry);
  }

  public void incrementResponseCount(String resourceName, int status) {
    counter.labels(resourceName, String.valueOf(status)).inc();
  }

  public void recordResponseLatency(String resourceName, long elapsedTimeNanos) {
    double elapsedTimeSecs = elapsedTimeNanos / BILLION;
    latency.labels(resourceName).observe(elapsedTimeSecs);
  }
}
