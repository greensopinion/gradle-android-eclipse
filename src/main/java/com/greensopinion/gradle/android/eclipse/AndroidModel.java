package com.greensopinion.gradle.android.eclipse;

import java.io.File;
import java.util.Iterator;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import groovy.lang.GroovyObject;
import groovy.lang.MissingPropertyException;

/**
 * Used elements of the Android configuration to fail fast and check the reverse
 * engineered assumptions. Avoid to fiddle with the Groovy objects and
 * properties out of this class.
 */
public class AndroidModel {

    private final Project project;

    private final String compileSdkVersion;

    private final String[] productFlavors;

    private final String[] buildTypes;

    private final String[] javaSourceFolders;

    private final String sdkDirectory;

    /**
     * Construct every reference to fail fast in case of incompatibilities.
     */
    public AndroidModel(Project project) {
        GroovyObject android = gradleAndroidConfiguration(project);
        GroovyObject androidFlavours = (GroovyObject) android.getProperty("productFlavors");
        GroovyObject androidBuildTypes = (GroovyObject) android.getProperty("buildTypes");
        GroovyObject androidSourceSets = (GroovyObject) android.getProperty("sourceSets");
        GroovyObject mainSourceSet = (GroovyObject) androidSourceSets.getProperty("main");
//        androidFlavours.getMetaClass().getProperties().forEach(t -> System.out.println(t.getName()));
//        System.out.println("empty=" + androidFlavours.getProperty("empty"));
//        System.out.println("names=" + androidFlavours.getProperty("names"));
//        System.out.println("androidSourceSets:");
//        System.out.println("names=" + androidSourceSets.getProperty("names"));
//        androidSourceSets.getMetaClass().getProperties().forEach(t -> System.out.println(t.getName()));
        // System.out.println("asMap=" + androidSourceSets.getProperty("asMap"));

//        GroovyObject debugSourceSet = (GroovyObject) androidSourceSets.getProperty("fatDebug");
//        debugSourceSet.getMetaClass().getProperties().forEach(t -> System.out.println(t.getName()));
//        System.out.println(debugSourceSet.getProperty("javaDirectories"));
//        System.out.println(debugSourceSet.getProperty("buildArtifactsReport$gradle"));

//        System.out.println("androidBuildTypes:");
//        androidBuildTypes.getMetaClass().getProperties().forEach(t -> System.out.println(t.getName()));
//        System.out.println("names=" + androidBuildTypes.getProperty("names"));
        this.project = project;
        this.compileSdkVersion = String.valueOf(android.getProperty("compileSdkVersion"));
        this.buildTypes = split(androidBuildTypes.getProperty("names"));
        this.productFlavors = split(androidFlavours.getProperty("names"));
        this.javaSourceFolders = split(mainSourceSet.getProperty("java"));
//  https://stackoverflow.com/q/20203787
//  if (android.hasProperty('plugin')) {
//      if (android.plugin.hasProperty('sdkHandler')) {
//          androidPath = android.plugin.sdkHandler.sdkFolder
//      } else {
//          androidPath = android.plugin.sdkDirectory
//      }
//  } else {
//      androidPath = android.sdkDirectory
//  }
        this.sdkDirectory = propertyOrNull(android, "sdkDirectory");
    }

    private String propertyOrNull(GroovyObject parent, String property) {
        try {
            return String.valueOf(parent.getProperty(property));
        } catch (MissingPropertyException e) {
            return null;
        }
    }

    private String[] split(Object arrayString) {
        String value = String.valueOf(arrayString);
        return value.substring(1, value.length() - 1).split("\\s*,\\s*");
    }

    private GroovyObject gradleAndroidConfiguration(Project project) {
        GroovyObject result = (GroovyObject) project.getProperties().get("android");
        if (result == null) {
            throw new IllegalArgumentException(
                    "Cannot find 'android' property.\nEnsure that the following is in your project: \n\napply plugin: 'com.android.tools.build:gradle:{VERSION}'\n\n");
        }
        return result;
    }

    public String[] getProductFlavors() {
        return productFlavors;
    }

    public String[] getBuildTypes() {
        return buildTypes;
    }

    public String[] getJavaSourceFolders() {
        return javaSourceFolders;
    }

    public boolean isProjectDirectory(String subDir) {
        return new File(project.getProjectDir(), subDir).isDirectory();
    }

    public String getCompileSdkVersion() {
        return compileSdkVersion;
    }

    public File getProjectDir() {
        return project.getProjectDir();
    }

    public String relativePath(Object each) {
        return project.relativePath(each);
    }

    public String getSdkDirectory() {
        return sdkDirectory;
    }

    public Configuration getConfiguration() {
        // TODO find the best instead the first flavor: **fat**DebugCompileClasspath
        for (Iterator<Configuration> it = project.getConfigurations().iterator(); it.hasNext();) {
            Configuration each = it.next();
            if (each.getName().endsWith("ebugCompileClasspath")) {
                return each;
            }
        }
        project.getConfigurations().forEach(System.out::println);
        return null;
    }

}
