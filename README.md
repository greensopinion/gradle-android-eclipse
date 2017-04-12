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
    * `build/generated/source/r/debug`
    * `build/generated/source/buildConfig/debug`
    * `build/generated/source/aidl/debug`
 * For any dependency packaged as an aar ([Android Archive](https://developer.android.com/studio/projects/android-library.html)), the aar is extracted into `build/exploded-aars`
    * each aar-packaged jar is added to the `.classpath`
 * Adds the Android SDK to the `.classpath`

Installation
============

Download the sources, and then from the command-line run:

    $ gradle publishToMavenLocal

How to Use
==========

Add the following to your build.gradle:

    apply plugin: 'com.greensopinion.android.eclipse'
    apply plugin: 'eclipse'

	eclipse {
	  classpath {
	    plusConfigurations += [ configurations.compile, configurations.testCompile ]
	    downloadSources = true
	  }
	}

	buildscript {
	    repositories {
	        mavenLocal()
	        jcenter()
	        mavenCentral()
	    }
	    dependencies {
	        classpath 'com.greensopinion:gradle-android-eclipse:0.2.0-SNAPSHOT'
	    }
	}

See [build.gradle](https://github.com/greensopinion/gradle-android-eclipse/blob/master/src/test/SampleApplication/app/build.gradle) for a complete working example.

Then from the command-line run:

    $ gradle eclipse

When done, a `.classpath` and `.project` file should be in the current folder.

License
=======

Copyright 2017 David Green

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
