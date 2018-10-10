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

import org.gradle.api.Plugin;
import org.gradle.api.Project;


public class EclipseGeneratorPlugin implements Plugin<Project> {
	
	public static final String PLUGIN_NAME = "androidEclipse";
	public static final String MAIN_TASK_NAME = "androidEclipse";
	
	public static final String DEFAULT_SRC_DIR = "src/main/java";
	public static final String DEFAULT_AAR_EXPLODED_DIR = "build/exploded-aars";
	
	@Override
	public void apply(Project project) {
		project.getExtensions().create(PLUGIN_NAME, AndroidEclipseExtension.class);
		project.getTasks().create(MAIN_TASK_NAME, AndroidEclipseTask.class);
	}

}
