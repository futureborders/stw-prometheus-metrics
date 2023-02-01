// Copyright 2021 Crown Copyright (Single Trade Window)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.application.ResourceNameLabelResolver;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream.DownstreamEndpointLabelNameResolver;

@Configuration
@ComponentScan("uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus")
public class MetricsConfig {

  @Bean
  public ResourceNameLabelResolver resourceNameLabelResolver(
      final MetricsProperties metricsProperties) {
    Assert.notNull(metricsProperties.getOutbound(), "Inbound properties must not be null");
    MetricCategory inboundMetricProps = metricsProperties.getInbound();
    return new ResourceNameLabelResolver(
        inboundMetricProps.getMethodPathRegexToMetricName(),
        inboundMetricProps.getExcludedPathsRegex(),
        inboundMetricProps.isIgnoreUnknownUri());
  }

  @Bean
  public DownstreamEndpointLabelNameResolver downstreamEndpointLabelNameResolver(
      final MetricsProperties metricsProperties) {
    Assert.notNull(metricsProperties.getOutbound(), "Outbound properties must not be null");
    MetricCategory outboundMetricProps = metricsProperties.getOutbound();
    return new DownstreamEndpointLabelNameResolver(
        outboundMetricProps.getMethodPathRegexToMetricName());
  }
}
