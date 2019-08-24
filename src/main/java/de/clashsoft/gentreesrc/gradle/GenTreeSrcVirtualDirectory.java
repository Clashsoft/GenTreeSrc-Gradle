package de.clashsoft.gentreesrc.gradle;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface GenTreeSrcVirtualDirectory
{
	String NAME = "gentreesrc";

	SourceDirectorySet getGenTreeSrc();

	GenTreeSrcVirtualDirectory genTreeSrc(Closure configureClosure);

	GenTreeSrcVirtualDirectory genTreeSrc(Action<? super SourceDirectorySet> configureAction);
}
