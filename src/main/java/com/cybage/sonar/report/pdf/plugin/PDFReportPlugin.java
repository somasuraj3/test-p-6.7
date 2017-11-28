package com.cybage.sonar.report.pdf.plugin;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;

import com.cybage.sonar.report.pdf.batch.PDFPostJob;

@Properties({
		@Property(key = PDFPostJob.USERNAME, name = "Username", description = "Username for WS API access.", defaultValue = PDFPostJob.USERNAME_DEFAULT_VALUE, global = true, project = true, module = false, type = PropertyType.STRING),
		@Property(key = PDFPostJob.PASSWORD, name = "Password", description = "Password for WS API access.", defaultValue = PDFPostJob.PASSWORD_DEFAULT_VALUE, global = true, project = true, module = false, type = PropertyType.PASSWORD),
		@Property(key = PDFPostJob.SKIP_PDF_KEY, name = "Skip", description = "Skip generation of PDF report.", defaultValue = ""
				+ PDFPostJob.SKIP_PDF_DEFAULT_VALUE, global = true, project = true, module = false, type = PropertyType.BOOLEAN),
		@Property(key = PDFPostJob.LEAK_PERIOD, name = "Leak Period", description = "Leak period.", defaultValue = PDFPostJob.LEAK_PERIOD_DEFAULT_VALUE, global = true, project = true, module = false, type = PropertyType.SINGLE_SELECT_LIST, options = {
				"previous_version", "previous_analysis", "days" }),
		@Property(key = PDFPostJob.OTHER_METRICS, name = "Other Metrics", description = "Comma separated metrics list that you would like to include explicitly in your PDF report.", defaultValue = "", global = true, project = true, module = false, type = PropertyType.TEXT) })

public class PDFReportPlugin implements Plugin {

	@Override
	public void define(Context context) {
		context.addExtension(PDFPostJob.class);
		// context.addExtension(PdfReportWidget.class);
	}
}
