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
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.ClasspathEntry;
import org.gradle.plugins.ide.eclipse.model.SourceFolder;

public class AddSourceFoldersAction implements Action<Classpath> {

	public static final String OUTPUT_FOLDER = "bin";
	public static final String TEST_OUTPUT_FOLDER = "bin-test";

	@Override
	public void execute(Classpath classpath) {
		Log.log().info("Adding Android source folders");
		classpath.getEntries().add(sourceFolder("src/main/java"));
		classpath.getEntries().add(testSourceFolder("src/test/java"));
		classpath.getEntries().add(testSourceFolder("src/test/resources"));
		classpath.getEntries().add(sourceFolder("build/generated/source/buildConfig/debug"));
		classpath.getEntries().add(sourceFolder("build/generated/not_namespaced_r_class_sources/debug/r"));
	}

	private ClasspathEntry sourceFolder(String path) {
		return new SourceFolder(path, OUTPUT_FOLDER);
	}
	private ClasspathEntry testSourceFolder(String path) {
		SourceFolder entry = new SourceFolder(path, TEST_OUTPUT_FOLDER);
		entry.getEntryAttributes().put("test","true");
		return entry;
	}
}
