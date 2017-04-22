/**
 * Copyright 2017 David Green
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.greensopinion.gradle.android.eclipse;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.slf4j.Logger;

import groovy.lang.MissingPropertyException;

public class EclipseGeneratorPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		Logger logger = Log.log();
		project.afterEvaluate(new Action<Project>() {

			@Override
			public void execute(Project project) {
				logger.info("Updating eclipse model with Android dependencies");

				EclipseModel eclipseModel = eclipseModel(project);
				eclipseModel.getClasspath().getFile().beforeMerged(new AddSourceFoldersAction());
				eclipseModel.getClasspath().getFile().whenMerged(new GenerateLibraryDependenciesAction(project));
				eclipseModel.getClasspath().getFile().whenMerged(new AndroidSdkLibraryDependenciesAction(project));

				project.getTasksByName("eclipseClasspath", false).forEach(t -> t.dependsOn("generateDebugSources"));

				logger.info("Android dependencies done");
			}
		});

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
