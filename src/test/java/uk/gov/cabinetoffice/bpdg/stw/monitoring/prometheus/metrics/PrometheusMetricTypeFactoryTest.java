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

package uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrometheusMetricTypeFactoryTest {
    private final PrometheusMetricTypeFactory factory = new PrometheusMetricTypeFactory();

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateCounterMetric() {
        String name = "name";
        String helpText = "help text";
        String[] labels = {"some_label", "another_label"};
        Counter counter = factory.createCounter(name, helpText, labels);

        Collector.MetricFamilySamples counterDetails = counter.describe().get(0);
        assertThat(counterDetails.name).isEqualTo(name);
        assertThat(counterDetails.help).isEqualTo(helpText);

        List<String> labelNames = (List<String>) ReflectionTestUtils.getField(counterDetails, "labelNames");

        assertThat(labelNames).contains(labels);
    }

    @Test
    void shouldRejectInvalidLabels() {
        String name = "name";
        String helpText = "help text";
        String[] labels = {"label with spaces"};

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> factory.createCounter(name, helpText, labels));

        assertThat(exception.getMessage()).isEqualTo("Invalid metric label name: label with spaces");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateHistogramMetric() {
        String name = "name";
        String helpText = "help text";
        String[] labels = {"some_label", "another_label"};
        double[] upperLimits = {10.0, 15.0};

        Histogram histogram = factory.createHistogram(name, helpText, upperLimits, labels);

        Collector.MetricFamilySamples histogramDetails = histogram.describe().get(0);
        assertThat(histogramDetails.name).isEqualTo(name);
        assertThat(histogramDetails.help).isEqualTo(helpText);

        List<String> labelNames = (List<String>) ReflectionTestUtils.getField(histogram, "labelNames");
        double[] bucketsUpperLimits = (double[]) ReflectionTestUtils.getField(histogram, "buckets");

        assertThat(labelNames).contains(labels);
        assertThat(bucketsUpperLimits).contains(upperLimits);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateGaugeMetric() {
        String name = "name";
        String helpText = "help text";
        String[] labels = {"some_label", "another_label"};
        Gauge gauge = factory.createGauge(name, helpText, labels);

        Collector.MetricFamilySamples gaugeDetails = gauge.describe().get(0);
        assertThat(gaugeDetails.name).isEqualTo(name);
        assertThat(gaugeDetails.help).isEqualTo(helpText);

        List<String> labelNames = (List<String>) ReflectionTestUtils.getField(gaugeDetails, "labelNames");

        assertThat(labelNames).contains(labels);
    }
}
