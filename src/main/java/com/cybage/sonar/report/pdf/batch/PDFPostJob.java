/*
 * SonarQube PDF Report
 * Copyright (C) 2010 klicap - ingenieria del puzle
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package com.cybage.sonar.report.pdf.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.postjob.PostJob;
import org.sonar.api.batch.postjob.PostJobContext;
import org.sonar.api.batch.postjob.PostJobDescriptor;
import org.sonar.api.config.Settings;

import edu.emory.mathcs.backport.java.util.Arrays;

public class PDFPostJob implements PostJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(PDFPostJob.class);

	private final FileSystem fs;

	public PDFPostJob(Settings settings, FileSystem fs) {
		this.fs = fs;
	}

	public static final String SKIP_PDF_KEY = "sonar.pdf.skip";
	public static final boolean SKIP_PDF_DEFAULT_VALUE = false;

	public static final String REPORT_TYPE = "report.type";
	public static final String REPORT_TYPE_DEFAULT_VALUE = "pdf";

	public static final String USERNAME = "sonar.pdf.username";
	public static final String USERNAME_DEFAULT_VALUE = "";

	public static final String PASSWORD = "sonar.pdf.password";
	public static final String PASSWORD_DEFAULT_VALUE = "";

	public static final String SONAR_HOST_URL = "sonar.host.url";
	public static final String SONAR_HOST_URL_DEFAULT_VALUE = "http://localhost:9000";

	public static final String SONAR_PROJECT_VERSION = "sonar.projectVersion";
	public static final String SONAR_PROJECT_VERSION_DEFAULT_VALUE = "1.0";

	public static final String SONAR_LANGUAGE = "sonar.language";

	@Override
	public void describe(PostJobDescriptor arg0) {

	}

	@Override
	public void execute(PostJobContext postJobContext) {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String projectKey = postJobContext.settings().getString("sonar.projectKey");

		LOGGER.info("Executing decorator: PDF Report");
		String sonarHostUrl = postJobContext.settings().hasKey(SONAR_HOST_URL)
				? postJobContext.settings().getString(SONAR_HOST_URL) : SONAR_HOST_URL_DEFAULT_VALUE;
		String username = postJobContext.settings().hasKey(USERNAME) ? postJobContext.settings().getString(USERNAME)
				: USERNAME_DEFAULT_VALUE;
		String password = postJobContext.settings().hasKey(PASSWORD) ? postJobContext.settings().getString(PASSWORD)
				: PASSWORD_DEFAULT_VALUE;
		String reportType = postJobContext.settings().hasKey(REPORT_TYPE)
				? postJobContext.settings().getString(REPORT_TYPE) : REPORT_TYPE_DEFAULT_VALUE;
		String projectVersion = postJobContext.settings().hasKey(SONAR_PROJECT_VERSION)
				? postJobContext.settings().getString(SONAR_PROJECT_VERSION) : SONAR_PROJECT_VERSION_DEFAULT_VALUE;
		List<String> sonarLanguage = postJobContext.settings().hasKey(SONAR_LANGUAGE)
				? Arrays.asList(postJobContext.settings().getStringArray(SONAR_LANGUAGE)) : null;

		PDFGenerator generator = new PDFGenerator(projectKey, projectVersion, sonarLanguage, fs, sonarHostUrl, username,
				password, reportType);

		generator.execute();

		/*
		 * String path = fs.workDir().getAbsolutePath() + "/" +
		 * projectKey.replace(':', '-') + ".pdf";
		 * 
		 * File pdf = new File(path); if (pdf.exists()) {
		 * FileUploader.upload(pdf, sonarHostUrl, username, password); } else {
		 * LOGGER.
		 * error("PDF file not found in local filesystem. Report could not be sent to server."
		 * ); }
		 */

	}

}
