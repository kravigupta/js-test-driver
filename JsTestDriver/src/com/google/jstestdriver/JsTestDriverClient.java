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
package com.google.jstestdriver;

import java.util.Collection;
import java.util.List;


/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public interface JsTestDriverClient {

  public Collection<BrowserInfo> listBrowsers();

  public void eval(String id, ResponseStream responseStream, String cmd);

  public void runAllTests(String id, ResponseStream responseStream, boolean captureConsole);

  public void reset(String id, ResponseStream responseStream);

  public void runTests(String id, ResponseStream responseStream, List<String> tests,
      boolean captureConsole);

  public void dryRun(String id, ResponseStream responseStream);
}
