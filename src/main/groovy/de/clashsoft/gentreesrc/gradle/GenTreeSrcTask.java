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

	private boolean visitPar     = true;
	private boolean visitReturn  = true;
	private boolean visitDefault = false;
	private boolean visitParent  = false;

	private String language;

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
			if (!this.visitPar)
			{
				args.add("--no-visit-par");
			}
			if (!this.visitReturn)
			{
				args.add("--visit-void");
			}
			if (this.visitDefault)
			{
				args.add("--visit-default");
			}
			if (this.visitParent)
			{
				args.add("--visit-parent");
			}
			if (this.language != null)
			{
				args.add("--language");
				args.add(this.language);
			}

			args.add("-o");
			args.add(this.getOutputDirectory().toString());
			args.add("--");
			args.add(this.getInputDirectory().toString());

			spec.setArgs(args);
		});
	}
}
