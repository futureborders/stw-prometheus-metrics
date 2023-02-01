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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class DownstreamEndpointLabelNameResolver {
    private static final String UNKNOWN_VALUE = "UNKNOWN";
    private final Map<Pattern, String> patternMap;

    public DownstreamEndpointLabelNameResolver(Map<String, String> metricsProperties) {
        patternMap = new HashMap<>();
        metricsProperties.forEach((k, v) -> patternMap.put(Pattern.compile(k.toLowerCase()), v));
    }

    public String get(String httpMethod, String requestPath) {
        Optional<Pattern> match = patternMap.keySet().stream()
                .filter(pattern -> pattern.matcher(String.format("%s-%s", httpMethod, requestPath).toLowerCase()).matches())
                .findFirst();

        return match.map(patternMap::get)
                .orElse(UNKNOWN_VALUE);
    }
}
