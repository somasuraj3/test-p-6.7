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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputModule;

import com.cybage.sonar.report.pdf.ExecutivePDFReporter;
import com.cybage.sonar.report.pdf.PDFReporter;
import com.cybage.sonar.report.pdf.TeamWorkbookPDFReporter;
import com.cybage.sonar.report.pdf.entity.exception.ReportException;
import com.cybage.sonar.report.pdf.util.Credentials;
import com.itextpdf.text.DocumentException;

public class PDFGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(PDFGenerator.class);

	private String sonarHostUrl;
	private String username;
	private String password;
	private String reportType;

	private String projectKey;
	private FileSystem fs;

	public PDFGenerator(final String projectKey, final FileSystem fs, final String sonarHostUrl,
			final String username, final String password, final String reportType) {
		this.projectKey = projectKey;
		this.fs = fs;
		this.sonarHostUrl = sonarHostUrl;
		this.username = username;
		this.password = password;
		this.reportType = reportType;
	}

	public void execute() {
		Properties config = new Properties();
		Properties configLang = new Properties();

		try {
			if (sonarHostUrl != null) {
				if (sonarHostUrl.endsWith("/")) {
					sonarHostUrl = sonarHostUrl.substring(0, sonarHostUrl.length() - 1);
				}
				config.put("sonar.base.url", sonarHostUrl);
				config.put("front.page.logo", "sonar.png");
			} else {
				config.load(this.getClass().getResourceAsStream("/report.properties"));
			}
			configLang.load(this.getClass().getResourceAsStream("/report-texts-en.properties"));

			Credentials credentials = new Credentials(config.getProperty("sonar.base.url"), username, password);

			String sonarProjectId = projectKey;
			String path = fs.workDir().getAbsolutePath() + "/" + sonarProjectId.replace(':', '-') + ".pdf";

			PDFReporter reporter = null;
			if (reportType != null) {
				if (reportType.equals("executive")) {
					LOGGER.info("Executive report type selected");
					reporter = new ExecutivePDFReporter(credentials, this.getClass().getResource("/sonar.png"),
							sonarProjectId, config, configLang);
				}/* else if (reportType.equals("workbook")) {
					LOGGER.info("Team workbook report type selected");
					reporter = new TeamWorkbookPDFReporter(credentials, this.getClass().getResource("/sonar.png"),
							sonarProjectId, config, configLang);
				}*/
			} else {
				LOGGER.info("No report type provided. Default report selected (Team workbook)");
				reporter = new TeamWorkbookPDFReporter(credentials, this.getClass().getResource("/sonar.png"),
						sonarProjectId, config, configLang);
			}

			//ByteArrayOutputStream baos = reporter.getReport();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileOutputStream fos = new FileOutputStream(new File(path));
			baos.writeTo(fos);
			fos.flush();
			fos.close();
			LOGGER.info("PDF report generated (see " + sonarProjectId.replace(':', '-')
					+ ".pdf on build output directory)");
		} catch (IOException e) {
			e.printStackTrace();
		} /*catch (DocumentException e) {
			LOGGER.error("Problem generating PDF file.");
			e.printStackTrace();
		} catch (ReportException e) {
			LOGGER.error("Internal error: " + e.getMessage());
			e.printStackTrace();
		}*/
	}
	
	

}
