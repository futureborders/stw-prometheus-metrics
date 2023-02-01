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

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import org.springframework.stereotype.Component;

@Component
public class PrometheusMetricTypeFactory {

    public Counter createCounter(String name, String helpText, String... labels) {
        return Counter.build()
                .name(name)
                .help(helpText)
                .labelNames(labels)
                .create();
    }

    public Histogram createHistogram(String name, String helpText, double[] bucketsUpperLimits, String... labels) {
        return Histogram.build()
                .name(name)
                .help(helpText)
                .labelNames(labels)
                .buckets(bucketsUpperLimits)
                .create();
    }

    public Gauge createGauge(String name, String helpText, String... labels) {
        return Gauge.build()
            .name(name)
            .help(helpText)
            .labelNames(labels)
            .create();
    }
}
