package de.clashsoft.gentreesrc.gradle

import groovy.io.FileType
import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static org.gradle.testkit.runner.TaskOutcome.*

class FunctionalTest extends Specification {
	static String[] TEST_FILES = [
			'build.gradle',
			'settings.gradle',
			'src/main/gentreesrc/Main.gts',
			'src/test/gentreesrc/Test.gts',
			'src/test/java/com/example/Test.java',
	]

	@Rule
	TemporaryFolder testProjectDir = new TemporaryFolder()

	@CompileStatic
	void setup() {
		final Path rootPath = testProjectDir.root.toPath()
		for (final String fileName : TEST_FILES) {
			final Path source = Paths.get('src/functionalTest/testfiles', fileName)
			final Path target = rootPath.resolve(fileName)

			Files.createDirectories(target.parent)

			try {
				Files.createLink(target, source)
			}
			catch (UnsupportedOperationException ignored) {
				Files.copy(source, target)
			}
		}
	}

	@CompileStatic
	BuildResult run() {
		try {
			final BuildResult result = GradleRunner.create()
					.withProjectDir(testProjectDir.root)
					.withArguments('check')
					.withPluginClasspath()
					.build()

			println "-" * 30 + " Gradle Output " + "-" * 30
			println result.output
			println "-" * 30 + " Project Files " + "-" * 30
			return result
		}
		finally {
			testProjectDir.root.eachFileRecurse(FileType.FILES) {
				println it
			}
			println "-" * 75
		}
	}

	def runsWithTestDir() {
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
		result.task(":gentreesrcTestJava").outcome == NO_SOURCE
		result.task(":check").outcome != FAILED
	}
}
