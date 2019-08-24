package de.clashsoft.gentreesrc.gradle;

import de.clashsoft.gentreesrc.gradle.internal.GenTreeSrcVirtualDirectoryImpl;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import java.io.File;

class GenTreeSrcPlugin implements Plugin<Project>
{
	public static final String MIN_TOOL_VERSION   = "0.4.0";
	public static final String CONFIGURATION_NAME = "gentreesrc";
	public static final String MAIN_CLASS_NAME    = "de.clashsoft.gentreesrc.Main";

	@Override
	public void apply(Project project)
	{
		project.getPluginManager().apply(JavaPlugin.class);

		// configuration
		project.getConfigurations().register(CONFIGURATION_NAME, it -> {
			it.setDescription("The GenTreeSrc libraries to use for this project.");
			it.setVisible(false);

			it.getResolutionStrategy().eachDependency(details -> {
				final String version = details.getRequested().getVersion();
				if (version == null)
				{
					details.useVersion("+");
					details.because("latest version");
				}
				else if (version.startsWith("0.1.") || version.startsWith("0.2.") || version.startsWith("0.3."))
				{
					details.useVersion(MIN_TOOL_VERSION);
					details.because(
						"gentreesrc versions before 0.4.0 do not support the command-line syntax required" + ' '
						+ "by the plugin");
				}
			});
		});

		for (final SourceSet sourceSet : project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets())
		{
			configureSourceSet(project, sourceSet);
		}
	}

	static void configureSourceSet(Project project, SourceSet sourceSet)
	{
		// for each source set we will:
		// 1) Add a new 'gentreesrc' virtual directory mapping

		final String srcDirName = "src/" + sourceSet.getName() + "/gentreesrc";

		final GenTreeSrcVirtualDirectoryImpl directoryDelegate = new GenTreeSrcVirtualDirectoryImpl(
			((DefaultSourceSet) sourceSet).getDisplayName(), project.getObjects());

		new DslObject(sourceSet).getConvention().getPlugins().put(GenTreeSrcVirtualDirectory.NAME, directoryDelegate);

		directoryDelegate.getGenTreeSrc().srcDir(srcDirName);

		sourceSet.getAllSource().source(directoryDelegate.getGenTreeSrc());

		// 2) create a task for this sourceSet following the gradle
		//    naming conventions via call to sourceSet.getTaskName()

		final String taskName = sourceSet.getTaskName("gentreesrc", "Java");
		final File outputDir = project.file(
			project.getBuildDir() + "/generated-src/gentreesrc/" + sourceSet.getName() + "/java");

		// 3) Set up the gentreesrc output directory (adding to javac inputs!)
		sourceSet.getJava().srcDir(outputDir);

		project.getTasks().register(taskName, GenTreeSrcTask.class, it -> {
			it.setDescription("Processes the " + sourceSet.getName() + " GenTreeSrc definitions.");

			// 4) set up convention mapping for default sources (allows user to not have to specify)
			it.setSource(directoryDelegate.getGenTreeSrc());
			it.setOutputDirectory(outputDir);
			it.setToolClasspath(project.getConfigurations().getByName(CONFIGURATION_NAME));
		});

		// 5) register fact that gentreesrc should be run before compiling
		project.getTasks().named(sourceSet.getCompileJavaTaskName(), it -> it.dependsOn(taskName));
	}
}
