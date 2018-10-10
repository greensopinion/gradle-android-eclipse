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
import org.gradle.plugins.ide.eclipse.model.SourceFolder;

public class AddSourceFoldersAction implements Action<Classpath> {
	
	private AndroidEclipseExtension ext;
	
	public AddSourceFoldersAction(AndroidEclipseExtension ext) {
		this.ext = ext;
	}
	
	@Override
	public void execute(Classpath classpath) {
		Log.log().info("Adding Android source folders");
		
		String srcDir = EclipseGeneratorPlugin.DEFAULT_SRC_DIR;
		if (ext != null && ext.srcDir != null) {
			srcDir = ext.srcDir;
		}
		
		classpath.getEntries().add(new SourceFolder(srcDir, "bin"));
		classpath.getEntries().add(new SourceFolder("build/generated/source/r/debug", "bin"));
		classpath.getEntries().add(new SourceFolder("build/generated/source/buildConfig/debug", "bin"));
		classpath.getEntries().add(new SourceFolder("build/generated/source/aidl/debug", "bin"));
	}
}
