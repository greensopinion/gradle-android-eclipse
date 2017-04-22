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

import java.lang.reflect.InvocationTargetException;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.ClasspathEntry;
import org.gradle.plugins.ide.eclipse.model.Variable;
import org.gradle.plugins.ide.eclipse.model.internal.FileReferenceFactory;

public class AndroidSdkLibraryDependenciesAction implements Action<Classpath> {
	private final Project project;

	public AndroidSdkLibraryDependenciesAction(Project project) {
		this.project = project;
	}

	@Override
	public void execute(Classpath classpath) {
		Log.log().info("Adding Android SDK classpath entry");
		classpath.getEntries().add(androidSdkEntry());
	}

	private ClasspathEntry androidSdkEntry() {
		FileReferenceFactory fileReferenceFactory = new FileReferenceFactory();
		Object compileSdkVersion = compileSdkVersion();
		Variable variable = new Variable(
				fileReferenceFactory.fromPath("ANDROID_HOME/platforms/" + compileSdkVersion + "/android.jar"));
		variable.setSourcePath(fileReferenceFactory.fromPath("ANDROID_HOME/sources/" + compileSdkVersion));
		return variable;
	}

	private Object compileSdkVersion() {
		Object android = project.property("android");
		try {
			return android.getClass().getMethod("getCompileSdkVersion").invoke(android);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Cannot get 'compileSdkVersion' property of 'android'.", e);
		}
	}
}
