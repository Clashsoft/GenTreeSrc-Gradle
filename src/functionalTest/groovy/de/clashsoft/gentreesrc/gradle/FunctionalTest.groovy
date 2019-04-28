package de.clashsoft.gentreesrc.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SKIPPED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class FunctionalTest extends Specification {
	@Rule
	TemporaryFolder testProjectDir = new TemporaryFolder()

	def setup() {
		testProjectDir.newFile('settings.gradle') << /* language=Groovy */ """
		rootProject.name = 'test'
		"""

		testProjectDir.newFile('build.gradle') << /* language=Groovy */ """
		plugins {
			id 'java'
			id 'de.clashsoft.gentreesrc-gradle'
		}
		
		repositories {
			jcenter()
		}
		
		dependencies {
			gentreesrc group: 'de.clashsoft', name: 'gentreesrc', version: '+'
			
			// https://mvnrepository.com/artifact/junit/junit
			testCompile group: 'junit', name: 'junit', version: '4.12'
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

	BuildResult run() {
		BuildResult result = GradleRunner.create()
				.withProjectDir(testProjectDir.root)
				.withArguments('check')
				.withPluginClasspath()
				.build()

		println "-" * 30 + " Gradle Output " + "-" * 30
		println result.output
		println "-" * 30 + " Project Files " + "-" * 30
		testProjectDir.root.eachFileRecurse {
			println it
		}
		println "-" * 75
		return result
	}

	def runsWithTestDir() {
		given:
		testProjectDir.newFolder('src', 'test', 'java', 'com', 'example')
		testProjectDir.newFile('src/test/java/com/example/Test.java') << /* language=Java */"""
		package com.example;

		public class Test {
			@org.junit.Test
			public void test() {
				Foo r = Bar.of("abc");
				Foo z = Baz.of(123);
				
				A a = B.of(A.of());
			}
		}
		"""

		when:
		def result = run()

		then:
		result.task(":gentreesrcJava").outcome == SUCCESS
		result.task(":gentreesrcTestJava").outcome == SUCCESS
		result.task(":check").outcome == SUCCESS

		def mainOutputDir = new File(testProjectDir.root, "build/generated-src/gentreesrc/main/java/")
		new File(mainOutputDir, 'com/example/Foo.java').exists()
		new File(mainOutputDir, 'com/example/Bar.java').exists()
		new File(mainOutputDir, 'com/example/Baz.java').exists()

		def testOutputDir = new File(testProjectDir.root, "build/generated-src/gentreesrc/test/java/")
		new File(testOutputDir, 'com/example/A.java').exists()
		new File(testOutputDir, 'com/example/B.java').exists()
	}

	def runsWithoutTestDir() {
		given:
		new File(testProjectDir.root, 'src/test/').deleteDir()

		when:
		def result = run()

		then:
		result.task(":gentreesrcJava").outcome == SUCCESS
		result.task(":gentreesrcTestJava").outcome == SKIPPED
		result.task(":check").outcome != FAILED
	}
}
