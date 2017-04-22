package com.greensopinion.gradle.android.eclipse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Log {

	public static Logger log() {
		return LoggerFactory.getLogger(EclipseGeneratorPlugin.class);
	}

	private Log() {
		// prevent instantiation
	}
}
