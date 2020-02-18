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

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.ClasspathEntry;
import org.gradle.plugins.ide.eclipse.model.Library;
import org.gradle.plugins.ide.eclipse.model.internal.FileReferenceFactory;

public class GenerateLibraryDependenciesAction implements Action<Classpath> {

	public static final String ECLIPSE_FILES = "eclipse_dependencies";
	private final Project project;

	public GenerateLibraryDependenciesAction(Project project) {
		this.project = project;
	}

	@Override
	public void execute(Classpath classpath) {
		List<ClasspathEntry> entries = classpath.getEntries().stream().flatMap(entry -> mapToJars(entry))
				.collect(Collectors.toList());
		entries.forEach(entry -> {
			Log.log().info("Adding Android classpath entry: " + entry.toString());
		});
		classpath.setEntries(entries);
	}

	private String toVersionId(Library library) {
		Log.log().info(" ----> library: " + library.getPath() +" VERSION: "+library.getModuleVersion());
		return library.getModuleVersion().toString().replaceAll(":", "-");
	}

	private File toTargetFolder(Library library) {
		File aarFile = new File(library.getPath());
		String jarId = toVersionId(library);
		return new File(new File(project.getProjectDir(), ECLIPSE_FILES), jarId);
	}

	private Stream<ClasspathEntry> mapToJars(ClasspathEntry entry) {
		if (entry instanceof Library) {
			Library library = (Library) entry;
			if (library.getPath().endsWith(".aar")) {
				return explodeAarJarFiles(library);
			}
		}
		return Stream.of(entry);
	}

	private Stream<ClasspathEntry> explodeAarJarFiles(Library aarLibrary) {
		File aarFile = new File(aarLibrary.getPath());
		File targetFolder = toTargetFolder(aarLibrary);
		if (!targetFolder.exists()) {
			if (!targetFolder.mkdirs()) {
				throw new RuntimeException(format("Cannot create folder: {0}", targetFolder.getAbsolutePath()));
			}
			try (ZipFile zipFile = new ZipFile(aarFile)) {
				zipFile.stream().forEach(f -> {
					String name = f.getName();
					if (name.endsWith(".jar")) {
						String targetName = toFilename(name);
						File targetFile = new File(targetFolder, targetName);
						ensureParentFolderExists(targetFile);
						int index = 1;
						while (targetFile.exists()) {
							targetFile = new File(targetFolder, format("{0}_{1}", ++index, targetName));
						}
						copy(zipFile, targetFile, f);
					}
				});
			} catch (IOException e) {
				throw new RuntimeException(
						format("Cannot explode aar: {0}: {1}", e.getMessage(), aarFile.getAbsolutePath()), e);
			}
		}
		List<File> files = listFilesTraversingFolders(targetFolder);
		FileReferenceFactory fileReferenceFactory = new FileReferenceFactory();
		return files.stream().filter(f -> f.getName().endsWith(".jar")).map(f -> {
			Library library = new Library(fileReferenceFactory.fromFile(f));
			library.setSourcePath(aarLibrary.getSourcePath());
			return library;
		});
	}

	private String toFilename(String path) {
		String[] segments = Pattern.compile("/|\\\\").split(path);
		return segments[segments.length-1];
	}

	private List<File> listFilesTraversingFolders(File folder) {
		List<File> files = new ArrayList<>();
		File[] children = folder.listFiles();
		if (children != null) {
			for (File child : children) {
				if (child.isFile()) {
					files.add(child);
				} else if (child.isDirectory()) {
					files.addAll(listFilesTraversingFolders(child));
				}
			}
		}
		return files;
	}

	private void ensureParentFolderExists(File targetFile) {
		File parentFolder = targetFile.getParentFile();
		if (!parentFolder.exists()) {
			if (!parentFolder.mkdirs()) {
				throw new RuntimeException(format("Cannot create folder: {0}", parentFolder.getAbsolutePath()));
			}
		}
	}

	private void copy(ZipFile zipFile, File targetFile, ZipEntry entry) {
		try (InputStream inputStream = zipFile.getInputStream(entry)) {
			Files.copy(inputStream, targetFile.toPath());
		} catch (IOException e) {
			throw new RuntimeException(
					format("Cannot write entry to file: {0}: {1}", e.getMessage(), targetFile.getAbsolutePath()), e);
		}
	}
}
