# GenTreeSrc Gradle Plugin v0.1.0

+ The plugin now adds the `gentreesrc` configuration.
+ The plugin now adds the `gentreesrcJava` task.
+ The plugin now adds a dependency on `gentreesrcJava` to the `compileJava` task.
+ The plugin now adds the task output directory as a source directory of `main.java`.

# GenTreeSrc Gradle Plugin v0.1.1

* Fixed `java`-plugin specific configurations being applied incorrectly.

# GenTreeSrc Gradle Plugin v0.2.0

+ The plugin now adds the `gentreesrcTestJava` task.
+ The plugin now adds a dependency on `gentreesrcTestJava` to the `compileTestJava` task.
+ The plugin now adds the task output directory as a source directory of `test.java`.

# GenTreeSrc Gradle Plugin v0.2.1

* Fixed the output directory of the `main` task being added to the `test` source directories. #3
* The plugin now uses static Groovy compilation.

# GenTreeSrc Gradle Plugin v0.3.0

* Bumped minimum tool version to `0.4.0`.
* Fixed an NPE when requested tool dependency version is `null`.
* The `gentreesrc*Java` tasks are now skipped if the `src/*/gentreesrc` directory does not exist. #4
* The tool is now invoked with the `--delete-old` option. #5

# GenTreeSrc Gradle Plugin v0.4.0

* Fixed tool versions `0.10+` resolving as `0.4.0`. #6
* Updated the configuration code to mimic the ANTLR plugin.

# GenTreeSrc Gradle Plugin v0.5.0

+ Added a dedicated `GenTreeSrcTask` type. #7
* GenTreeSrc tasks are no longer debuggable. #8
* The `gentreesrc` configuration is now created lazily.

# GenTreeSrc Gradle Plugin v0.6.0

* The `GenTreeSrcTask` now supports incremental compilation. #9
* The `GenTreeSrcTask` now supports include and exclude patterns. #10
