package de.clashsoft.gentreesrc.gradle.internal;

import de.clashsoft.gentreesrc.gradle.GenTreeSrcVirtualDirectory;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.reflect.HasPublicType;
import org.gradle.api.reflect.TypeOf;
import org.gradle.util.ConfigureUtil;

public class GenTreeSrcVirtualDirectoryImpl implements GenTreeSrcVirtualDirectory, HasPublicType
{
	private final SourceDirectorySet genTreeSrc;

	public GenTreeSrcVirtualDirectoryImpl(String parentDisplayName, ObjectFactory objectFactory)
	{
		this.genTreeSrc = objectFactory.sourceDirectorySet(parentDisplayName + ".gentreesrc",
		                                                   parentDisplayName + " GenTreeSrc source");
		this.genTreeSrc.getFilter().include("**/*.gts");
	}

	@Override
	public SourceDirectorySet getGenTreeSrc()
	{
		return this.genTreeSrc;
	}

	@Override
	public GenTreeSrcVirtualDirectory genTreeSrc(Closure configureClosure)
	{
		ConfigureUtil.configure(configureClosure, this.getGenTreeSrc());
		return this;
	}

	@Override
	public GenTreeSrcVirtualDirectory genTreeSrc(Action<? super SourceDirectorySet> configureAction)
	{
		configureAction.execute(this.getGenTreeSrc());
		return this;
	}

	@Override
	public TypeOf<?> getPublicType()
	{
		return TypeOf.typeOf(GenTreeSrcVirtualDirectory.class);
	}
}
