package de.clashsoft.gentreesrc.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GenTreeSrcTask extends DefaultTask
{
	// =============== Fields ===============

	private FileCollection classpath;

	private File outputDirectory;
	private File inputDirectory;

	private boolean deleteOld = true;

	private List<String> extraArgs = new ArrayList<>();

	// =============== Properties ===============

	@Classpath
	public FileCollection getClasspath()
	{
		return this.classpath;
	}

	public void setClasspath(FileCollection classpath)
	{
		this.classpath = classpath;
	}

	@OutputDirectory
	public File getOutputDirectory()
	{
		return this.outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}

	@InputDirectory
	@SkipWhenEmpty
	public File getInputDirectory()
	{
		return this.inputDirectory;
	}

	public void setInputDirectory(File inputDirectory)
	{
		this.inputDirectory = inputDirectory;
	}

	// --------------- Options ---------------

	@Input
	public boolean isDeleteOld()
	{
		return this.deleteOld;
	}

	public void setDeleteOld(boolean deleteOld)
	{
		this.deleteOld = deleteOld;
	}

	// --------------- Extra Args ---------------

	@Input
	public List<String> getExtraArgs()
	{
		return this.extraArgs;
	}

	public void setExtraArgs(List<String> extraArgs)
	{
		Objects.requireNonNull(extraArgs);
		this.extraArgs = extraArgs;
	}

	public void extraArgs(Object... extraArgs)
	{
		for (final Object extraArg : extraArgs)
		{
			this.extraArgs.add(extraArg.toString());
		}
	}

	public void extraArgs(Iterable<?> extraArgs)
	{
		for (final Object extraArg : extraArgs)
		{
			this.extraArgs.add(extraArg.toString());
		}
	}

	// =============== Methods ===============

	@TaskAction
	public void execute()
	{
		this.getProject().javaexec(spec -> {
			spec.setClasspath(this.getClasspath());
			spec.setMain(GenTreeSrcPlugin.MAIN_CLASS_NAME);

			final List<String> args = new ArrayList<>(this.getExtraArgs());
			if (this.isDeleteOld())
			{
				args.add("--delete-old");
			}

			args.add("-o");
			args.add(this.getOutputDirectory().toString());
			args.add("--");
			args.add(this.getInputDirectory().toString());

			spec.setArgs(args);

			System.out.println(spec.getCommandLine());
		});
	}
}
