package org.gradle.api.plugins.appenginegeb

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class IntegrationSpec extends Specification {

    public static final String FUNCTIONAL_TEST_TASK = ':appengineFunctionalTest'
    public static final String PATH_DELIMETER = '/'

    @Rule
    final TemporaryFolder dir = new TemporaryFolder()

    File file(String path) {
        def parts = path.split(PATH_DELIMETER)
        if (parts.size() > 1 && !new File(dir.root, parts[0..-2].join(PATH_DELIMETER)).exists()) {
            dir.newFolder(*parts[0..-2])
        }
        def oldFile = new File(dir.root, path)
        if (oldFile.exists()) {
            return oldFile
        }
        dir.newFile(path)
    }

    File getBuildFile() {
        file('build.gradle')
    }

    void writeBuildFile() {
        buildFile << '''
            buildscript {
                repositories {
                    mavenCentral()
                }

                dependencies {
                    classpath 'com.google.appengine:gradle-appengine-plugin:1.9.38'
                }
            }

            plugins {
                id 'groovy'
                id 'war'
                id 'appengine'
                id 'appengine-geb'
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                compile 'javax.servlet:servlet-api:2.5'
                appengineSdk 'com.google.appengine:appengine-java-sdk:1.9.38'
                functionalTestCompile 'org.spockframework:spock-core:1.0-groovy-2.4',
                    'org.gebish:geb-spock:0.13.1',
                    'org.seleniumhq.selenium:htmlunit-driver:2.21',
                    'org.seleniumhq.selenium:selenium-api:2.53.0'
            }

            appengine {
                downloadSdk = true
            }
        '''
    }

    void writeAppengineWebConfig() {
        file('src/main/webapp/WEB-INF/appengine-web.xml') << '''
            <?xml version="1.0" encoding="utf-8"?>
            <appengine-web-app xmlns="http://appengine.google.com/ns/1.0">
                <application>test</application>
                <version>1.0</version>
                <threadsafe>true</threadsafe>
            </appengine-web-app>
        '''.trim()
    }

    @SuppressWarnings('LineLength')
    void writeWebConfig() {
        file('src/main/webapp/WEB-INF/web.xml') << '''
            <?xml version="1.0" encoding="utf-8"?>
            <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee"
                     xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
                     xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
                     version="2.5">
                <servlet>
                    <servlet-name>root</servlet-name>
                    <servlet-class>org.gradle.api.plugins.appenginegeb.test.RootServlet</servlet-class>
                </servlet>
                <servlet-mapping>
                    <servlet-name>root</servlet-name>
                    <url-pattern>/</url-pattern>
                </servlet-mapping>
            </web-app>
        '''.trim()
    }

    void writeServletSource() {
        file('src/main/java/org/gradle/api/plugins/appenginegeb/test/RootServlet.java') << '''
            package org.gradle.api.plugins.appenginegeb.test;

            import javax.servlet.http.HttpServlet;
            import javax.servlet.http.HttpServletRequest;
            import javax.servlet.http.HttpServletResponse;
            import java.io.IOException;

            public class RootServlet extends HttpServlet {
                public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                    resp.setContentType("text/html");
                    resp.getWriter().println("<html><body>Hello world!</body></html>");
                }
            }
        '''
    }

    void writeSpecSource() {
        file('src/functionalTest/groovy/org/gradle/api/plugins/appenginegeb/test/AppengineGebSpec.groovy') << '''
            package org.gradle.api.plugins.appenginegeb

            import geb.spock.GebSpec

            class AppengineGebSpec extends GebSpec {

                def "can access application"() {
                    when:
                    go()

                    then:
                    $().text() == 'Hello world!'
                }

            }
        '''
    }

    def setupProject() {
        writeBuildFile()
        writeAppengineWebConfig()
        writeWebConfig()
        writeServletSource()
        writeSpecSource()
    }

    BuildResult executeFunctionalTests() {
        GradleRunner.create()
            .withProjectDir(dir.root)
            .withPluginClasspath()
            .withArguments(FUNCTIONAL_TEST_TASK)
            .build()
    }

    def "baseUrl is set appropriately for default value of the httpPort convention of appengine plugin"() {
        given:
        setupProject()

        when:
        def result = executeFunctionalTests()

        then:
        result.task(FUNCTIONAL_TEST_TASK).outcome == SUCCESS
    }

    def "baseUrl is set appropriately for custom value of the httpPort convention of appengine plugin"() {
        given:
        setupProject()
        buildFile << '''
            appengine {
                httpPort = 8085
            }
        '''

        when:
        def result = executeFunctionalTests()

        then:
        result.task(FUNCTIONAL_TEST_TASK).outcome == SUCCESS
    }
}
