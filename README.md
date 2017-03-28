# Android Eclipse Generator #

A Gradle plug-in that enables generation of Eclipse project files (.project and .classpath) to enable use of the Eclipse IDE for Android projects.

This generator works with the new [Android build system](http://tools.android.com/tech-docs/new-build-system). 

Based on [this stack overflow](http://stackoverflow.com/questions/17470831/how-to-use-gradle-to-generate-eclipse-and-intellij-project-files-for-android-pro) by [Johannes Brodwall](http://stackoverflow.com/users/27658/johannes-brodwall).

# Installation #

Download the sources, and then from the command-line run:

    $ mvn install -Dgradle.home=/path/to/gradle/home

# How to Use #

Add the following to your build.gradle:

    apply plugin: 'com.greensopinion.gradle.android.eclipse'
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
	        classpath 'com.greensopinion.gradle:android-eclipse:0.0.1-SNAPSHOT'
	    }
	}

See [build.gradle](https://github.com/greensopinion/gradle-android-eclipse/blob/master/src/test/SampleApplication/app/build.gradle) for a complete working example.

Then from the command-line run: 

    $ gradle eclipse

When done, a `.classpath` and `.project` file should be in the current folder.

# License #

Copyright 2017 David Green

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
