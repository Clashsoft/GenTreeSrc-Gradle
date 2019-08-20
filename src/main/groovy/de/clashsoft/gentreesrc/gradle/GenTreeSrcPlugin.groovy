package de.clashsoft.gentreesrc.gradle

import de.clashsoft.gentreesrc.gradle.internal.GenTreeSrcVirtualDirectoryImpl
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

@CompileStatic
class GenTreeSrcPlugin implements Plugin<Project> {

	public static final String MIN_TOOL_VERSION = '0.4.0'
	public static final String CONFIGURATION_NAME = 'gentreesrc'
	public static final String MAIN_CLASS_NAME = 'de.clashsoft.gentreesrc.Main'

	@Override
	void apply(Project project) {
		project.pluginManager.apply(JavaPlugin)

		// configuration
		project.configurations.register(CONFIGURATION_NAME, { Configuration it ->
			it.description = 'The GenTreeSrc libraries to use for this project.'
			it.visible = false
			it.resolutionStrategy.eachDependency { DependencyResolveDetails details ->
				final String version = details.requested.version
				if (version == null) {
					details.useVersion('+')
					details.because('latest version')
				}
				else if (version.startsWith('0.1.') || version.startsWith('0.2.') || version.startsWith('0.3.')) {
					details.useVersion(MIN_TOOL_VERSION)
					details.because('gentreesrc versions before 0.4.0 do not support the command-line syntax required' +
							' ' +
							'by the plugin')
				}
			}
		} as Action<Configuration>)

		project.convention.getPlugin(JavaPluginConvention).sourceSets.each {
			configureSourceSet(project, it)
		}
	}

	static void configureSourceSet(Project project, SourceSet sourceSet) {
		// for each source set we will:
		// 1) Add a new 'gentreesrc' virtual directory mapping

		final String srcDirName = "src/$sourceSet.name/gentreesrc"

		final GenTreeSrcVirtualDirectoryImpl directoryDelegate = new GenTreeSrcVirtualDirectoryImpl((
				(DefaultSourceSet) sourceSet).displayName, project.objects)

		new DslObject(sourceSet).convention.plugins.put(GenTreeSrcVirtualDirectory.NAME, directoryDelegate)

		directoryDelegate.genTreeSrc.srcDir(srcDirName)

		sourceSet.allSource.source(directoryDelegate.genTreeSrc)

		// 2) create a task for this sourceSet following the gradle
		//    naming conventions via call to sourceSet.getTaskName()

		final String taskName = sourceSet.getTaskName("gentreesrc", "Java")
		final String outputDirName = "$project.buildDir/generated-src/gentreesrc/$sourceSet.name/java"
		final File outputDir = project.file(outputDirName)

		project.tasks.register(taskName, GenTreeSrcTask, { GenTreeSrcTask it ->
			it.description = "Processes the $sourceSet.name GenTreeSrc definitions."

			// 4) set up convention mapping for default sources (allows user to not have to specify)
			it.outputDirectory = outputDir
			it.source = directoryDelegate.genTreeSrc
			it.classpath = project.configurations.getByName(CONFIGURATION_NAME)
		} as Action<GenTreeSrcTask>)

		// 3) Set up the gentreesrc output directory (adding to javac inputs!)
		sourceSet.java.srcDir(outputDir)

		// 5) register fact that gentreesrc should be run before compiling
		project.tasks.named(sourceSet.compileJavaTaskName) { Task it ->
			it.dependsOn taskName
		}
	}
}
