Android Eclipse Generator gradle-android-eclipse
================================================

This is not another Android SDK approach, but a Gradle plug-in to handle the Eclipse project files (`.project`, `.classpath` and `.settings/org.eclipse.jdt.core.prefs`). It enables the use of the Eclipse IDE for Android projects to navigate and edit all the sources and dependencies found in the Gradle configuration. The Eclipse build runs in parallel to the Gradle build, since it is development and not intended to be distributed.

What It Does
------------

This plug-in hooks into the [Gradle eclipse plugin](https://docs.gradle.org/current/userguide/eclipse_plugin.html) to make it work for Android projects, by doing the following:

 * Adds the Android source paths:
    * Java sources defined in [AndroidSourceSet](https://google.github.io/android-gradle-dsl/current/com.android.build.gradle.api.AndroidSourceSet.html#com.android.build.gradle.api.AndroidSourceSet:java) if exists
    * Generated Java sources in trying to find flavored paths like this:
       * `build/generated/aidl_source_output_dir/{debug/compileD|*}ebugAidl/out`
       * `build/generated/not_namespaced_r_class_sources/{debug/processD|*}ebugResources/r`
       * `build/generated/source/aidl/{*}debug`
       * `build/generated/source/apt/{*}debug`
       * `build/generated/source/BuildConfig/{*}debug`
 * Adds all the Dependencies found in the debug classpath with attached sources
 * For any dependency packaged as an aar ([Android Archive](https://developer.android.com/studio/projects/android-library.html)), the aar is extracted into `build/exploded-aars`
    * each aar-packaged jar is added to the `.classpath`
 * Adds the Android SDK to the `.classpath` found in the Gradle Android configuration
 * Configure the default output dir to `bin` instead of Buildships `bin/default`
 * Configure `eclipse.classpath.downloadSources = true` for development
 * Configure source and target compatibility to Java 1.8 needed by Android
 * Rename subprojects like `app` to `Parent-app` to avoid naming conflicts in Eclipse

How to Use
==========

It's highly experimental (caused of estimated Android SDK paths) but small and quite easy to understand and manipulate. I prefer to use the plug-in project build along the Android app projects, configured by a relative `files('../` reference.

Add the following to your build.gradle in parent project:

	buildscript {
        ...
	    dependencies {
          ...
           classpath files('../gradle-android-eclipse/build/libs/gradle-android-eclipse-1.2.jar')
	    }
	}

And to the app's build.gradle often in a sub project:

    apply plugin: 'eclipse'
    apply plugin: 'com.greensopinion.gradle-android-eclipse'

<!-- TODO:
See [src/test/SampleApplication/app/build.gradle](https://github.com/greensopinion/gradle-android-eclipse/blob/master/src/test/SampleApplication/app/build.gradle) for a complete working example. -->

Then from the command-line run:

    $ gradle eclipse

When done, a `.classpath`, `.project` and `.settings/org.eclipse.jdt.core.prefs` file should be in the current folder.

Debug
-----

To debug this plugin while configuring an Eclipse project start a waiting Gradle process with:

    $ ./gradlew --no-daemon -Dorg.gradle.debug=true eclipse

Then start a remote debug session to *Remote Java Application* on port `5005` in Eclipse.


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

----

This generator works with the new [Android build system](http://tools.android.com/tech-docs/new-build-system).

Based on [this stack overflow](http://stackoverflow.com/questions/17470831/how-to-use-gradle-to-generate-eclipse-and-intellij-project-files-for-android-pro) by [Johannes Brodwall](http://stackoverflow.com/users/27658/johannes-brodwall).

