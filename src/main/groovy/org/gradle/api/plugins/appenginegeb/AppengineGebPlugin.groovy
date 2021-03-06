/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.plugins.appenginegeb

import com.google.appengine.AppEnginePlugin
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.testing.Test
import geb.buildadapter.SystemPropertiesBuildAdapter

class AppengineGebPlugin implements Plugin<Project> {

	void apply(Project project) {
		project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
			def url = "http://localhost:${project.appengine.httpPort}/"
			Test gaeFunctionalTest = project.tasks.findByName(AppEnginePlugin.APPENGINE_FUNCTIONAL_TEST)
			gaeFunctionalTest.systemProperty(SystemPropertiesBuildAdapter.BASE_URL_PROPERTY_NAME, url)
		}
	}
}
