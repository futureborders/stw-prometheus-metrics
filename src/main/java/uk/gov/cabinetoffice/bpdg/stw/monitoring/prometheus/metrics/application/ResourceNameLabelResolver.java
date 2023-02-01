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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResourceNameLabelResolver {
    private static final String UNKNOWN_RESOURCE = "unknown";
    private final Map<Pattern, String> methodPathRegexPatternToMetricName;
    private final boolean ignoreUnknownUri;
    private final List<Pattern> excludedPathsRegex;

    public ResourceNameLabelResolver(Map<String, String> methodPathRegexPatternToMetricName, List<String> excludedPathsRegex, boolean ignoreUnknownUri) {
        this.excludedPathsRegex = excludedPathsRegex.stream().map(Pattern::compile).collect(Collectors.toList());
        this.methodPathRegexPatternToMetricName = methodPathRegexPatternToMetricName.entrySet().stream()
                .collect(Collectors.toMap(entry -> Pattern.compile(entry.getKey()), Map.Entry::getValue));
        this.ignoreUnknownUri = ignoreUnknownUri;
    }

    public Optional<String> getResourceName(String method, String path){
        String methodPath = String.format("%s-%s", method.toUpperCase(), path);
        if(excludedPathsRegex.stream().anyMatch(p -> p.matcher(methodPath).matches())){
            return Optional.empty();
        }
        Optional<Pattern> optionalPattern = getMatchingPatternForPath(methodPath);
        return resolveResourceName(optionalPattern);
    }

    private Optional<String> resolveResourceName(Optional<Pattern> optionalPattern) {
        Optional<String> resourceName = optionalPattern.map(methodPathRegexPatternToMetricName::get);
        return ignoreUnknownUri ? resourceName : Optional.of(resourceName.orElse(UNKNOWN_RESOURCE));
    }

    private Optional<Pattern> getMatchingPatternForPath(String methodPath) {
        return methodPathRegexPatternToMetricName.keySet().stream()
                    .filter(p -> p.matcher(methodPath).matches())
                    .findFirst();
    }
}
