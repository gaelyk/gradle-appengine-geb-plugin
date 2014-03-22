# Gradle AppEngine Geb plugin

This plugin is a simple integration point between [Gradle AppEngine plugin](https://github.com/GoogleCloudPlatform/gradle-apspengine-plugin) and [Geb browser automation framework](http://www.gebish.org/). It doesn't provide any new tasks but only sets Geb's `baseUrl` configuration property based on Gradle GAE plugin's conventions using `SystemPropertiesBuildAdapter` mechanism.

## Usage

**IMPORTANT:** Using this plugin only makes sense when **Gradle AppEngine plugin is applied to the project**. Please make sure that this is the case before applying this plugin to your project.

	buildscript {
		repositories {
			mavenCentral()
		}
		
		dependencies {
			classpath 'org.gradle.api.plugins:gradle-appengine-geb-plugin:0.4'
		}
	}
	
After applying the plugin you no longer need to specify `baseUrl` in `GebConfig.groovy` as it will be derived from your build settings.