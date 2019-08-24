package de.clashsoft.gentreesrc.gradle;

import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.internal.MutableBoolean;
import org.gradle.process.JavaExecSpec;
import org.gradle.util.GFileUtils;

import java.io.File;
import java.util.*;

public class GenTreeSrcTask extends SourceTask
{
	// =============== Fields ===============

	private FileCollection toolClasspath;

	private File outputDirectory;

	private boolean visitPar     = true;
	private boolean visitReturn  = true;
	private boolean visitDefault = false;
	private boolean visitParent  = false;

	private String language;

	private List<String> extraArgs = new ArrayList<>();

	// =============== Properties ===============

	@Classpath
	public FileCollection getToolClasspath()
	{
		return this.toolClasspath;
	}

	public void setToolClasspath(FileCollection toolClasspath)
	{
		this.toolClasspath = toolClasspath;
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

	// --------------- Options ---------------

	@Input
	public boolean isVisitPar()
	{
		return this.visitPar;
	}

	public void setVisitPar(boolean visitPar)
	{
		this.visitPar = visitPar;
	}

	@Input
	public boolean isVisitReturn()
	{
		return this.visitReturn;
	}

	public void setVisitReturn(boolean visitReturn)
	{
		this.visitReturn = visitReturn;
	}

	@Input
	public boolean isVisitDefault()
	{
		return this.visitDefault;
	}

	public void setVisitDefault(boolean visitDefault)
	{
		this.visitDefault = visitDefault;
	}

	@Input
	public boolean isVisitParent()
	{
		return this.visitParent;
	}

	public void setVisitParent(boolean visitParent)
	{
		this.visitParent = visitParent;
	}

	@Input
	@Optional
	public String getLanguage()
	{
		return this.language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
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
	public void execute(IncrementalTaskInputs inputs)
	{
		// adapted from AntlrTask.execute(IncrementalTaskInputs)

		final Set<File> sourceFiles = this.getSource().getFiles();
		final Set<File> inputFiles = new HashSet<>();
		final MutableBoolean cleanRebuild = new MutableBoolean(false);

		inputs.outOfDate(details -> {
			File input = details.getFile();
			if (sourceFiles.contains(input))
			{
				inputFiles.add(input);
			}
			else
			{
				cleanRebuild.set(true);
			}
		});

		inputs.removed(details -> cleanRebuild.set(true));

		if (cleanRebuild.get())
		{
			GFileUtils.cleanDirectory(this.getOutputDirectory());
			inputFiles.addAll(sourceFiles);
		}

		this.getProject().javaexec(spec -> {
			this.configureOptions(spec);
			spec.args("--");
			spec.args(inputFiles);
		});
	}

	public void configureOptions(JavaExecSpec spec)
	{
		spec.setClasspath(this.getToolClasspath());
		spec.setMain(GenTreeSrcPlugin.MAIN_CLASS_NAME);

		spec.args(this.extraArgs);

		if (!this.visitPar)
		{
			spec.args("--no-visit-par");
		}
		if (!this.visitReturn)
		{
			spec.args("--visit-void");
		}
		if (this.visitDefault)
		{
			spec.args("--visit-default");
		}
		if (this.visitParent)
		{
			spec.args("--visit-parent");
		}
		if (this.language != null)
		{
			spec.args("--language", this.language);
		}

		spec.args("-o", this.getOutputDirectory());
	}
}
