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
jstestdriver.TestCaseBuilder = function(testCaseManager) {
  this.testCaseManager_ = testCaseManager;
  jstestdriver.global.TestCase = jstestdriver.bind(this, this.TestCase);

  // legacy
  jstestdriver.testCaseManager.TestCase = TestCase;
};


jstestdriver.TestCaseBuilder.prototype.TestCase = function(testCaseName, proto) {
  var testCaseClass = function() {};

  if (proto) {
    testCaseClass.prototype = proto;
  }
  if (typeof testCaseClass.prototype.setUp == 'undefined') {
    testCaseClass.prototype.setUp = function() {};
  }
  if (typeof testCaseClass.prototype.tearDown == 'undefined') {
    testCaseClass.prototype.tearDown = function() {};
  }
  this.testCaseManager_.add(new jstestdriver.TestCaseInfo(testCaseName, testCaseClass));
  return testCaseClass;
};
