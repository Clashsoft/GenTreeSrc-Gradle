package de.clashsoft.gentreesrc.gradle

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer

class GenTreeSrcPlugin implements Plugin<Project> {

	public static final String MIN_TOOL_VERSION = '0.4.0'

	@Override
	@CompileStatic
	void apply(Project project) {
		final String language = 'java'
		final String languageSuffix = language.capitalize()

		// configuration
		final String configurationName = 'gentreesrc'
		final Configuration configuration = project.configurations.create(configurationName)

		configuration.resolutionStrategy.eachDependency { DependencyResolveDetails details ->
			final String version = details.requested.version
			if (version == null) {
				details.useVersion('+')
				details.because('latest version')
			}
			else if (version.startsWith('0.1') || version.startsWith('0.2') || version.startsWith('0.3')) {
				details.useVersion(MIN_TOOL_VERSION)
				details.because('gentreesrc versions before 0.4.0 do not support the command-line syntax required by' +
						' ' +
						'the plugin')
			}
		}

		for (String s : [ 'main', 'test' ]) {
			// W/A for capture problem, see https://github.com/Clashsoft/GenTreeSrc-Gradle/issues/3
			final String sourceSet = s
			final String sourceSetSuffix = sourceSet == 'main' ? '' : sourceSet.capitalize()

			final String taskName = configurationName + sourceSetSuffix + languageSuffix
			final String inputDir = "src/$sourceSet/gentreesrc/"
			final String outputDir = "$project.buildDir/generated-src/gentreesrc/$sourceSet/$language"

			// task
			project.tasks.register(taskName, JavaExec, { JavaExec it ->
				it.classpath = configuration
				it.main = 'de.clashsoft.gentreesrc.Main'
				it.args = [ '-o', outputDir, '--delete-old', inputDir ]

				it.inputs.dir(inputDir)
				it.outputs.dir(outputDir)

				it.onlyIf { project.file(inputDir).exists() }
			} as Action<JavaExec>)

			project.plugins.withType(JavaPlugin) {
				final String compileTaskName = "compile${ sourceSetSuffix }Java"
				final SourceSetContainer sourceSets = project.convention.getPlugin(JavaPluginConvention).sourceSets

				// configure source directory
				sourceSets.getByName(sourceSet).java.srcDir(project.files(outputDir).builtBy(taskName))

				// configure compile task dependency
				project.tasks.getByName(compileTaskName).dependsOn(taskName)
			}
		}
	}
}
