package com.cybage.sonar.report.pdf.batch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.postjob.PostJob;
import org.sonar.api.batch.postjob.PostJobContext;
import org.sonar.api.batch.postjob.PostJobDescriptor;
import org.sonar.api.config.Settings;

import com.cybage.sonar.report.pdf.util.LeakPeriods;

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

	public static final String OTHER_METRICS = "sonar.pdf.otherMetrics";

	public static final String LEAK_PERIOD = "sonar.pdf.leakPeriod";
	public static final String LEAK_PERIOD_DEFAULT_VALUE = LeakPeriods.PREVIOUS_VERSION;

	@Override
	public void describe(PostJobDescriptor arg0) {

	}

	@Override
	public void execute(PostJobContext postJobContext) {
		if (postJobContext.settings().hasKey(SKIP_PDF_KEY)
				&& postJobContext.settings().getBoolean(SKIP_PDF_KEY) == true) {
			LOGGER.info("Skipping generation of PDF Report..");
		} else {

			try {
				Thread.sleep(4000);
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
			Set<String> otherMetrics = postJobContext.settings().hasKey(OTHER_METRICS)
					? new HashSet<String>(Arrays.asList(postJobContext.settings().getStringArray(OTHER_METRICS)))
					: null;

			// LOGGER.info("leak period in properties file : " +
			// postJobContext.settings().getString(LEAK_PERIOD));
			String leakPeriod = postJobContext.settings().hasKey(LEAK_PERIOD) && LeakPeriods.getAllLeakPeriods()
					.stream().filter(lp -> lp.equals(postJobContext.settings().getString(LEAK_PERIOD))).count() > 0
							? postJobContext.settings().getString(LEAK_PERIOD) : LEAK_PERIOD_DEFAULT_VALUE;

			LOGGER.info("Leak Period : " + leakPeriod);

			PDFGenerator generator = new PDFGenerator(projectKey, projectVersion, sonarLanguage, otherMetrics,
					leakPeriod, fs, sonarHostUrl, username, password, reportType);

			try {
				generator.execute();
			} catch (Exception ex) {
				LOGGER.error("Error generating PDF report..");
			}

		}
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
