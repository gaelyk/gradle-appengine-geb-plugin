package org.gradle.api.plugins.appenginegeb

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppEnginePluginExtension
import geb.buildadapter.SystemPropertiesBuildAdapter
import java.util.concurrent.CountDownLatch
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class IntegrationSpec extends Specification {

    @Rule final TemporaryFolder dir = new TemporaryFolder()

    Task task(String taskName) {
        Project project = ProjectBuilder.builder()
            .withProjectDir(dir.root)
            .build()

        Task t
        
        // here be dragons ... (see http://gradle.1045684.n5.nabble.com/taskGraph-whenReady-not-firing-in-my-unit-test-td4515237.html)
        project.evaluate()

        TaskExecutionGraph taskGraph = project.gradle.taskGraph
        taskGraph.buildOperationExecutor.currentOperation.set(new org.gradle.internal.progress.DefaultBuildOperationExecutor$OperationDetails(null, null))
        taskGraph.addTasks([])
        CountDownLatch latch = new CountDownLatch(1)
        taskGraph.whenReady {
            t = project.tasks.findByName(taskName)
            latch.countDown()
        }
        taskGraph.execute()
        latch.await()
        t
    }

    File getBuildFile() {
        file('build.gradle')
    }

    File file(String path) {
        def parts = path.split('/')
        if (parts.size() > 1) {
            dir.newFolder(* parts[0..-2])
        }
        def oldFile = new File(dir.root, path)
        if (oldFile.exists()) {
            return oldFile
        }
        dir.newFile(path)
    }

    def setup() {
        buildFile << """
            def GaeGebPlugin = project.class.classLoader.loadClass('org.gradle.api.plugins.appenginegeb.AppengineGebPlugin')
            
            apply plugin: 'war'
			apply plugin: 'appengine'
			apply plugin: GaeGebPlugin

            buildscript {
                repositories {
                    mavenCentral()
                }
                dependencies {
                    classpath 'com.google.appengine:gradle-appengine-plugin:1.9.34',
                              'org.codehaus.geb:geb-core:0.7.2'
                }
            }

			dependencies {
				appengineSdk "com.google.appengine:appengine-java-sdk:1.9.38"
			}

			repositories {
				mavenCentral()
			}

			appengine {
				downloadSdk = true
			}
		"""
    }

    @Unroll("buildUrl system property is set when gae.http value is '#conventionValue'")
    def "buildUrl system property is set based on httpPort convention of gae plugin"() {
        given:
        if (conventionValue) {
            buildFile << """
				appengine {
					httpPort = $conventionValue
				}
			"""
        }

        expect:
        Test testTask = task(AppEnginePlugin.APPENGINE_FUNCTIONAL_TEST)
        testTask.systemProperties[SystemPropertiesBuildAdapter.BASE_URL_PROPERTY_NAME] == "http://localhost:$propertyPort/"

        where:
        conventionValue | propertyPort
        null            | new AppEnginePluginExtension().httpPort
        8085            | 8085
    }
}
