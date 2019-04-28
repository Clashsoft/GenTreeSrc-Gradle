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
