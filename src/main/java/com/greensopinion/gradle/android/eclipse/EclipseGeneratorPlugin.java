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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;

import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.gradle.plugins.ide.eclipse.model.EclipseProject;
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
                configureDownloadSources(eclipseModel);
                configureOutputBin(eclipseModel);
                configureJavaEight(eclipseModel);
                renameSubproject(eclipseModel);

                AndroidModel android = new AndroidModel(project);
                eclipseModel.getClasspath().getFile().beforeMerged(new AddSourceFoldersAction(android));
                eclipseModel.getClasspath().getFile().whenMerged(new GenerateLibraryDependenciesAction(android));
                eclipseModel.getClasspath().getFile().whenMerged(new AndroidSdkLibraryDependenciesAction(android));
                enableTriggerOnGenerateSources(project);

				logger.info("Android dependencies done");
			}

            private void configureDownloadSources(EclipseModel eclipseModel) {
                eclipseModel.getClasspath().setDownloadSources(true);
            }

            private void configureOutputBin(EclipseModel eclipseModel) {
                eclipseModel.getClasspath().setDefaultOutputDir(new File(project.getProjectDir(), "bin"));
            }

            private void configureJavaEight(EclipseModel eclipseModel) {
                eclipseModel.getJdt().setSourceCompatibility(JavaVersion.VERSION_1_8);
                eclipseModel.getJdt().setTargetCompatibility(JavaVersion.VERSION_1_8);
            }

            private void renameSubproject(EclipseModel eclipseModel) {
                EclipseProject eclipseProject = eclipseModel.getProject();
                File parentDir = project.getProjectDir().getParentFile();
                File parentSettings = new File(parentDir, "settings.gradle");
                if (parentSettings.isFile()) {
                    removeBuildshipNatureToAvoidFightingWithName(eclipseProject);
                    String reference = String.format("':%s'", project.getName());
                    try {
                        for(Iterator<String> it = Files.lines(parentSettings.toPath(), StandardCharsets.UTF_8)
                                .iterator(); it.hasNext();) {
                            String each = it.next();
                            if (each.startsWith("include ") && each.contains(reference)) {
                                String subprojectName = parentDir.getName() + "-" + eclipseProject.getName();
                                eclipseProject.setName(subprojectName);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            }

            private void removeBuildshipNatureToAvoidFightingWithName(EclipseProject eclipseProject) {
                List<String> natures = eclipseProject.getNatures();
                natures.remove("org.eclipse.buildship.core.gradleprojectnature");
                eclipseProject.setNatures(natures);
            }

            private void enableTriggerOnGenerateSources(Project project) {
                project.getTasks().forEach(task -> {
                    String name = task.getName();
                    if (name.startsWith("generate") && name.endsWith("ebugSources")) {
                        project.getTasksByName("eclipseClasspath", false).forEach(t -> t.dependsOn(name));
                    }
                });
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
