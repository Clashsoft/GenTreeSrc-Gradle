package de.clashsoft.gentreesrc.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class FunctionalTest extends Specification {
	@Rule
	TemporaryFolder testProjectDir = new TemporaryFolder()

	def setup() {
		testProjectDir.newFile('settings.gradle') << """
		rootProject.name = 'test'
		"""

		testProjectDir.newFile('build.gradle') << """
		plugins {
			id 'java'
			id 'de.clashsoft.gentreesrc-gradle'
		}
		
		repositories {
			jcenter()
		}
		
		dependencies {
			gentreesrc group: 'de.clashsoft', name: 'gentreesrc', version: '+'
		}
		"""

		testProjectDir.newFolder('src', 'main', 'gentreesrc')
		testProjectDir.newFile('src/main/gentreesrc/Main.gts') << """
		com.example.Foo {
			Bar(text: String)
			Baz(value: int)
		}
		"""

		testProjectDir.newFolder('src', 'test', 'gentreesrc')
		testProjectDir.newFile('src/test/gentreesrc/Test.gts') << """
		com.example.A {
			B(a: A)
		}
		"""
	}

	def run() {
		when:
		def result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments('gentreesrcJava', 'gentreesrcTestJava')
				.withPluginClasspath()
				.build()

		println "-" * 30 + " Gradle Output " + "-" * 30
		println result.output
		println "-" * 30 + " Project Files " + "-" * 30
		testProjectDir.root.eachFileRecurse {
			println it
		}
		println "-" * 75

		then:
		result.task(":gentreesrcJava").outcome == SUCCESS
		result.task(":gentreesrcTestJava").outcome == SUCCESS

		def mainOutputDir = new File(testProjectDir.root, "build/generated-src/gentreesrc/main/java/")
		new File(mainOutputDir, 'com/example/Foo.java').exists()
		new File(mainOutputDir, 'com/example/Bar.java').exists()
		new File(mainOutputDir, 'com/example/Baz.java').exists()

		def testOutputDir = new File(testProjectDir.root, "build/generated-src/gentreesrc/test/java/")
		new File(testOutputDir, 'com/example/A.java').exists()
		new File(testOutputDir, 'com/example/B.java').exists()
	}
}
