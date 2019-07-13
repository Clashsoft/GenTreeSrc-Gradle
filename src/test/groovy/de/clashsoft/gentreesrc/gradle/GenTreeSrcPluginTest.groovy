package de.clashsoft.gentreesrc.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.hamcrest.CoreMatchers.hasItem
import static org.hamcrest.CoreMatchers.notNullValue
import static org.junit.Assert.assertThat

class GenTreeSrcPluginTest {
	@Test
	void addsConfiguration() {
		Project project = ProjectBuilder.builder().withName('test').build()
		project.pluginManager.apply 'de.clashsoft.gentreesrc-gradle'

		assertThat(project.configurations.gentreesrc, notNullValue())
	}

	@Test
	void addsTasks() {
		Project project = ProjectBuilder.builder().withName('test').build()
		project.pluginManager.apply 'java'
		project.pluginManager.apply 'de.clashsoft.gentreesrc-gradle'

		assertThat(project.tasks.gentreesrcJava, notNullValue())
		assertThat(project.tasks.gentreesrcTestJava, notNullValue())
		assertThat(project.tasks.compileJava.dependsOn, hasItem('gentreesrcJava'))
		assertThat(project.tasks.compileTestJava.dependsOn, hasItem('gentreesrcTestJava'))
	}

	@Test
	void addsSourceDirs() {
		Project project = ProjectBuilder.builder().withName('test').build()
		project.pluginManager.apply 'java'
		project.pluginManager.apply 'de.clashsoft.gentreesrc-gradle'

		def sourceSets = project.convention.getPlugin(JavaPluginConvention).sourceSets

		assertThat(sourceSets.main.java.srcDirs, hasItem(new File("$project.buildDir/generated-src/gentreesrc/main/java/")))
		assertThat(sourceSets.test.java.srcDirs, hasItem(new File("$project.buildDir/generated-src/gentreesrc/test/java/")))
	}
}
