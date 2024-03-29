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

package uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics;

import static java.lang.System.nanoTime;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimerTest {
  Timer timer = new Timer();

  @Test
  void testStartNew() {
    Timer result = Timer.startNew();
    assertNotNull(result);
  }

  @Test
  void testStart() {
    long currentTime = nanoTime();
    Timer result = timer.start();
    assertTrue(result.end() < currentTime);
  }
}
