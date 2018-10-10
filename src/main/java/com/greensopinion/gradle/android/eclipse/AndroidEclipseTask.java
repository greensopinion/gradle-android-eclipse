package com.greensopinion.gradle.android.eclipse;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.slf4j.Logger;

import groovy.lang.MissingPropertyException;

public class AndroidEclipseTask extends DefaultTask {

	@TaskAction
	public void run() {
		Logger logger = Log.log();

		Project project = getProject();
		AndroidEclipseExtension ext = (AndroidEclipseExtension) project.getExtensions().getByName(EclipseGeneratorPlugin.PLUGIN_NAME);
		
		logger.info("Updating eclipse model with Android dependencies");
		
		EclipseModel eclipseModel = (EclipseModel) eclipseModel(project);
		eclipseModel.getClasspath().getFile().beforeMerged(new AddSourceFoldersAction(ext));
		eclipseModel.getClasspath().getFile().whenMerged(new GenerateLibraryDependenciesAction(project, ext));
		eclipseModel.getClasspath().getFile().whenMerged(new AndroidSdkLibraryDependenciesAction(project));
		
		project.getTasksByName("eclipseClasspath", false).forEach(t -> t.dependsOn("generateDebugSources"));
		
		logger.info("Android dependencies done");
	}
	
	private EclipseModel eclipseModel(Project project) {
		try {
			return (EclipseModel) project.property("eclipse");
		} catch (MissingPropertyException e) {
			throw new RuntimeException(
					"Cannot find 'eclipse' property.\nEnsure that the following is in your project: \n\napply plugin: 'eclipse'\n\n",
					e);
		}
	}
	
}
