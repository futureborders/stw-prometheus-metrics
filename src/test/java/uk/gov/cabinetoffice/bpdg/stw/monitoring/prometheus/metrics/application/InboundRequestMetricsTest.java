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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.PrometheusMetricTypeFactory;

import static org.mockito.Mockito.*;
import static uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.application.InboundRequestMetrics.BILLION;

@ExtendWith(MockitoExtension.class)
class InboundRequestMetricsTest {

    private static final String LABEL = "postTitle";
    private InboundRequestMetrics inboundRequestMetrics;

    @Mock
    private CollectorRegistry mockCollectorRegistry;

    @Mock
    private Counter responseCounter;

    @Mock
    private Counter.Child counterChild;

    @Mock
    private Histogram latency;

    @Mock
    private Histogram.Child latencyChild;

    @Mock
    private PrometheusMetricTypeFactory metricTypeFactory;

    @BeforeEach
    void setUp() {
        when(metricTypeFactory.createCounter("application_responses",
                "Counters for application inbound endpoints",
                "resourceName", "status")).thenReturn(responseCounter);

        when(metricTypeFactory.createHistogram(
                "application_latency",
                "Timers for application inbound endpoints.",
                new double[]{1, 2, 3},
                "resourceName")).thenReturn(latency);

        inboundRequestMetrics = new InboundRequestMetrics(metricTypeFactory, mockCollectorRegistry, new double[]{1, 2, 3});
    }

    @Test
    void shouldIncrementResponseCount() {
        String expectedStatus = "200";

        when(responseCounter.labels(LABEL, expectedStatus)).thenReturn(counterChild);

        inboundRequestMetrics.incrementResponseCount(LABEL, 200);

        InOrder inOrder = inOrder(responseCounter, counterChild);
        inOrder.verify(responseCounter).labels(LABEL, expectedStatus);
        inOrder.verify(counterChild).inc();
    }

    @Test
    void shouldRecordResponseLatency() {
        int elapsedTimeNanos = 111;

        when(latency.labels(LABEL)).thenReturn(latencyChild);

        inboundRequestMetrics.recordResponseLatency(LABEL, elapsedTimeNanos);

        verify(latencyChild).observe(elapsedTimeNanos / BILLION);
    }
}

