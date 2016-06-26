ruleset {
	ruleset('rulesets/basic.xml')
	ruleset('rulesets/braces.xml')
	ruleset('rulesets/concurrency.xml')
	ruleset('rulesets/convention.xml')
	ruleset('rulesets/design.xml')
	ruleset('rulesets/dry.xml') {
		DuplicateNumberLiteral {
			doNotApplyToClassNames = '*Spec'
		}
	}
	ruleset('rulesets/exceptions.xml')
	ruleset('rulesets/formatting.xml') {
		ClassJavadoc {
			enabled = false
		}
	}
	ruleset('rulesets/generic.xml')
	ruleset('rulesets/grails.xml')
	ruleset('rulesets/groovyism.xml')
	ruleset('rulesets/imports.xml') {
		MisorderedStaticImports {
			comesBefore = false
		}
	}
	ruleset('rulesets/jdbc.xml')
	ruleset('rulesets/junit.xml')
	ruleset('rulesets/logging.xml')
	ruleset('rulesets/naming.xml') {
		MethodName {
			regex = /[a-z][\w\s]*/
		}
		FactoryMethodName {
			doNotApplyToClassNames = '*Spec'
		}
	}
	ruleset('rulesets/security.xml') {
		JavaIoPackageAccess {
			enabled = false
		}
	}
	ruleset('rulesets/serialization.xml')
	ruleset('rulesets/unnecessary.xml')
	ruleset('rulesets/unused.xml')
}