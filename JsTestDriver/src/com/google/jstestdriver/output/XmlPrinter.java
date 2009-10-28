/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver.output;

import com.google.gson.Gson;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.JsException;
import com.google.jstestdriver.RunData;
import com.google.jstestdriver.TestResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class XmlPrinter implements TestResultPrinter {

  private static final String NEW_LINE = System.getProperty("line.separator");

  private final Gson gson = new Gson();

  private final TestXmlSerializer serializer;
  private final AtomicInteger browsers;
  private final ConcurrentHashMap<String, RunData> browsersRunData;
  private final RunData runData = new RunData();

  public XmlPrinter(TestXmlSerializer serializer, AtomicInteger browsers,
      ConcurrentHashMap<String, RunData> browsersRunData) {
    this.serializer = serializer;
    this.browsers = browsers;
    this.browsersRunData = browsersRunData;
  }

  public void open(String name) {
    serializer.startTestSuite(name);
  }

  public void close() {
    if (browsers.decrementAndGet() == 0) {
      StringBuilder output = new StringBuilder();

      for (Map.Entry<String, RunData> entry : browsersRunData.entrySet()) {
        RunData data = entry.getValue();
        List<TestResult> problems = data.getProblems();

        for (TestResult testResult : problems) {
          output.append(testResult.getLog() + NEW_LINE);
        }
      }
      if (output.length() > 0) {
        serializer.addOutput(output.toString());
      }
    }
    serializer.endTestSuite();
  }

  // TODO(jeremiele): I know what you think, I think it too...
  private void logData(TestResult testResult) {
    TestResult.Result result = testResult.getResult();
    String browserName = testResult.getBrowserInfo().getName();
    String browserVersion = testResult.getBrowserInfo().getVersion();
    String os = testResult.getBrowserInfo().getOs();

    // There is one thread per browser it should be added the first time
    browsersRunData.putIfAbsent(browserName + " " + browserVersion + " " + os, runData);
    String log = testResult.getLog();

    if (log.length() > 0) {
      runData.addProblem(testResult);
    }
    if (result == TestResult.Result.passed) {
      runData.addPass();
    } else if (result == TestResult.Result.failed) {
      runData.addFail();
    } else if (result == TestResult.Result.error) {
      runData.addError();
    }
  }

  public void print(TestResult testResult) {
    logData(testResult);
    BrowserInfo browserInfo = testResult.getBrowserInfo();

    serializer.startTestCase(testResult.getTestCaseName(), testResult.getTestName() + ":" +
        browserInfo.getName() + browserInfo.getVersion(), testResult.getTime() / 1000);
    if (testResult.getResult() != TestResult.Result.passed) {
      String message = "";

      try {
        JsException exception = gson.fromJson(testResult.getMessage(), JsException.class);

        message = exception.getMessage();
      } catch (Exception e) {
        message = testResult.getMessage();
      }
      if (testResult.getResult() == TestResult.Result.failed) {
        serializer.addFailure(testResult.getResult().toString(), message);
      } else if (testResult.getResult() == TestResult.Result.error) {
        serializer.addError(testResult.getResult().toString(), message);
      }
    }
    serializer.endTestCase();
  }
}