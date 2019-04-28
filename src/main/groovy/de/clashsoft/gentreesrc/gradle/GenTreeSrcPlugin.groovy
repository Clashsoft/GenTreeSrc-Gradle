package de.clashsoft.gentreesrc.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer

class GenTreeSrcPlugin implements Plugin<Project> {
	@Override
	void apply(Project project) {
		String language = 'java'
		String languageSuffix = language.capitalize()

		// configuration
		String configurationName = 'gentreesrc'
		Configuration configuration = project.configurations.create(configurationName)

		configuration.resolutionStrategy.eachDependency { DependencyResolveDetails details ->
			String version = details.requested.version
			if (version.startsWith('0.1') || version.startsWith('0.2') || version == '0.3.0') {
				details.useVersion('0.3.1')
				details.because('gentreesrc versions before 0.3.1 do not support the command-line syntax required by the ' +
						'plugin')
			}
		}

		for (String sourceSet : [ 'main', 'test' ]) {
			String sourceSetSuffix = sourceSet == 'main' ? '' : sourceSet.capitalize()

			String taskName = configurationName + sourceSetSuffix + languageSuffix
			String inputDir = "src/$sourceSet/gentreesrc/"
			String outputDir = "$project.buildDir/generated-src/gentreesrc/$sourceSet/$language"

			// task
			project.tasks.register(taskName, JavaExec) {
				classpath = configuration
				main = 'de.clashsoft.gentreesrc.Main'
				args = [ '-o', outputDir, inputDir ]

				inputs.dir(inputDir)
				outputs.dir(outputDir)
			}

			project.plugins.withType(JavaPlugin) {
				String compileTaskName = "compile${ sourceSetSuffix }Java"
				SourceSetContainer sourceSets = project.convention.getPlugin(JavaPluginConvention).sourceSets

				// configure source directory
				sourceSets.getByName(sourceSet).java.srcDir(project.files(outputDir).builtBy(taskName))

				// configure compile task dependency
				project.tasks.getByName(compileTaskName).dependsOn(taskName)
			}
		}
	}
}
