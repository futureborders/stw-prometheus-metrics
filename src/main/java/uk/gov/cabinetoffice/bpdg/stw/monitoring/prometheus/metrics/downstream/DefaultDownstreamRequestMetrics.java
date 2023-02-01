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

package uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.PrometheusMetricTypeFactory;

@Component
public class DefaultDownstreamRequestMetrics implements DownstreamRequestMetrics {

    private static final String COUNTER_NAME = "downstream_system_responses";
    private static final String COUNTER_HELP = "Downstream requests";
    static final String[] COUNTER_LABELS = {"downstream", "endpoint", "status"};

    private static final String LATENCY_NAME = "downstream_system_latency";
    private static final String LATENCY_HELP = "Downstream latency";
    static final String[] LATENCY_LABELS = {"downstream", "endpoint"};

    static final double BILLION = 1_000_000_000d;

    private final Counter downstreamRequests;
    private final Histogram downstreamLatency;

    @Autowired
    public DefaultDownstreamRequestMetrics(PrometheusMetricTypeFactory metricTypeFactory, CollectorRegistry collectorRegistry,
                                           @Value("#{'${metrics.outbound.histogram.bucketsUpperLimits.csv:0.1, 0.5, 1.0, 3.0}'.split('[,;] *')}") double[] bucketsUpperLimits) {
        downstreamRequests = metricTypeFactory.createCounter(COUNTER_NAME, COUNTER_HELP, COUNTER_LABELS);
        downstreamLatency = metricTypeFactory.createHistogram(LATENCY_NAME, LATENCY_HELP, bucketsUpperLimits, LATENCY_LABELS);

        collectorRegistry.register(downstreamRequests);
        collectorRegistry.register(downstreamLatency);
    }

    @Override
    public void record(String downstream, String resource, String status, long elapsedTimeNanos) {
        recordCount(downstream, resource, status);
        recordLatency(downstream, resource, elapsedTimeNanos);
    }

    @Override
    public void recordCount(String downstream, String resource, String status) {
        downstreamRequests.labels(downstream, resource, status).inc();
    }

    @Override
    public void recordLatency(String downstream, String resource, long elapsedTimeNanos) {
        double elapsedTimeSecs = elapsedTimeNanos / BILLION;

        downstreamLatency.labels(downstream, resource).observe(elapsedTimeSecs);
    }

    @Override
    public void initCounter(String downstream, String resource, String status) {
        downstreamRequests.labels(downstream, resource, status).inc(0.0);
    }
}
