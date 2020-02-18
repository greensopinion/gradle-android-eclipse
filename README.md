Android Eclipse Generator
=========================

A Gradle plug-in that enables generation of Eclipse project files (.project and .classpath) to enable use of the Eclipse IDE for Android projects.

This generator works with the new [Android build system](http://tools.android.com/tech-docs/new-build-system).

Based on [this stack overflow](http://stackoverflow.com/questions/17470831/how-to-use-gradle-to-generate-eclipse-and-intellij-project-files-for-android-pro) by [Johannes Brodwall](http://stackoverflow.com/users/27658/johannes-brodwall).

What It Does
------------

This plug-in hooks into the [Gradle eclipse plugin](https://docs.gradle.org/current/userguide/eclipse_plugin.html) to make it work for Android projects, by doing the following:

 * Adds the following Android source paths:
    * `src/main/java`
    * `src/test/java`
    * `src/main/resources`
    * `build/generated/source/buildConfig/debug`
    * `build/generated/not_namespaced_r_class_sources/debug/r`
 * For any dependency packaged as an aar ([Android Archive](https://developer.android.com/studio/projects/android-library.html)), the aar is extracted into `build/exploded-aars`
    * each aar-packaged jar is added to the `.classpath`
 * Adds the Android SDK to the `.classpath`
 * Updates the Eclipse project compiler settings to match the `android.compileOptions` in the gradle build

How to Use
==========

Add the following to your build.gradle:

    apply plugin: 'com.greensopinion.gradle-android-eclipse'
    apply plugin: 'eclipse'

	buildscript {
	    repositories {
	        maven {
	          url "https://plugins.gradle.org/m2/"
	        }
	    }
	    dependencies {
	      classpath "gradle.plugin.com.greensopinion.gradle-android-eclipse:gradle-android-eclipse:1.1"
	    }
	}

	eclipse {
	  classpath {
	    plusConfigurations += [ configurations.compile, configurations.testCompile ]
	    downloadSources = true
	  }
	}

See [build.gradle](https://github.com/greensopinion/gradle-android-eclipse/blob/master/src/test/SampleApplication/app/build.gradle) for a complete working example.

Then from the command-line run:

    $ gradle eclipse

When done, a `.classpath` and `.project` file should be in the current folder.

Contributing
============

To open this project in Eclipse, first run the following:

    $ gradle eclipse

License
=======

Copyright 2017 David Green

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
