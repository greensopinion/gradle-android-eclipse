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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import org.gradle.api.Action;
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.SourceFolder;

public class AddSourceFoldersAction implements Action<Classpath> {

    private final AndroidModel android;

    public AddSourceFoldersAction(AndroidModel android) {
        this.android = android;
    }

	@Override
	public void execute(Classpath classpath) {
		Log.log().info("Adding Android source folders");

        for (String projectRelativePath : android.getJavaSourceFolders()) {
            addSourceFolderIfProjectRelative(classpath, projectRelativePath);
        }
        System.out.println("productFlavors " + Arrays.toString(android.getProductFlavors()));
        System.out.println("buildTypes " + Arrays.toString(android.getBuildTypes()));

        // TODO gradle build dir could be relocated
        locateProjectDir("build/generated/aidl_source_output_dir/", "debug/compileD", "ebugAidl/out") //
                .ifPresent(t -> addSourceFolder(classpath, t));
        locateProjectDir("build/generated/not_namespaced_r_class_sources/", "debug/processD", "ebugResources/r") //
                .ifPresent(t -> addSourceFolder(classpath, t));
        locateProjectDir("build/generated/source/aidl/", "", "debug") //
                .ifPresent(t -> addSourceFolder(classpath, t));
        locateProjectDir("build/generated/source/apt/", "", "debug") //
                .ifPresent(t -> addSourceFolder(classpath, t));
        locateProjectDir("build/generated/source/buildConfig/", "", "debug") //
                .ifPresent(t -> addSourceFolder(classpath, t));

//		classpath.getEntries().forEach(System.out::println);
    }

    private boolean addSourceFolderIfProjectRelative(Classpath classpath, String projectRelativePath) {
        if (android.isProjectDirectory(projectRelativePath)) {
            addSourceFolder(classpath, projectRelativePath);
            return true;
        }
        return false;
    }

    private boolean addSourceFolder(Classpath classpath, String t) {
        return classpath.getEntries().add(new SourceFolder(t, null));
    }

    private Optional<String> locateProjectDir(String base, String byFlavor, String rest) {
        String projectRelativePath = String.format("%s%s%s", base, byFlavor, rest);
        if (android.isProjectDirectory(projectRelativePath)) {
            return Optional.of(projectRelativePath);
        }
        File dir = new File(android.getProjectDir(), base);
        if (dir.isDirectory()) {
            try {
                for (Iterator<Path> it = Files.walk(dir.toPath(), 3).iterator(); it.hasNext();) {
                    Path each = it.next();
                    if (each.toString().endsWith(rest)) {
                        return Optional.of(android.relativePath(each));
                    }
                }
            } catch (IOException e) {
                System.err.println("Couldn't walk dir: " + e);
            }
        }
        return Optional.empty();
	}
}
