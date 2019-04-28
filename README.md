# GenTreeSrc Gradle Plugin

[![Build Status](https://travis-ci.org/Clashsoft/GenTreeSrc-Gradle.svg?branch=master)](https://travis-ci.org/Clashsoft/GenTreeSrc-Gradle)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/de/clashsoft/gentreesrc-gradle/de.clashsoft.gentreesrc-gradle.gradle.plugin/maven-metadata.xml.svg?colorB=blue&label=Gradle%20Plugin%20Portal)](https://plugins.gradle.org/plugin/de.clashsoft.gentreesrc-gradle)

The Gradle plugin for the [GenTreeSrc](https://github.com/Clashsoft/GenTreeSrc) tool.

## Usage

The plugin is available on the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/de.clashsoft.gentreesrc-gradle)
and can be installed via the `plugins` DSL in `build.gradle`:

```groovy
plugins {
	// ...
	id 'java'
	id 'de.clashsoft.gentreesrc-gradle' version '0.2.1'
	// ...
}

// ...
```

See the [GenTreeSrc README](https://github.com/Clashsoft/GenTreeSrc/blob/master/README.md) for tool usage instructions.
