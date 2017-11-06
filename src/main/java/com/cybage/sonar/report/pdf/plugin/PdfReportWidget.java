package com.cybage.sonar.report.pdf.plugin;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;

/**
 * {@inheritDoc}
 */
@Description("Allows to download PDF report of SonarQube analysis")
public final class PdfReportWidget extends AbstractRubyTemplate implements RubyRailsWidget {

	protected String getTemplatePath() {
		return "/com/cybage/sonar/report/pdf/dashboard_widget.erb";
	}

	public String getId() {
		return "pdf-report-widget";
	}

	public String getTitle() {
		return "PDF Report Widget";
	}
}