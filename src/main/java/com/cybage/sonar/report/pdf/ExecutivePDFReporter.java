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
package com.cybage.sonar.report.pdf;

import static com.cybage.sonar.report.pdf.util.MetricDomains.*;
import static com.cybage.sonar.report.pdf.util.MetricKeys.*;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cybage.sonar.report.pdf.entity.Project;
import com.cybage.sonar.report.pdf.entity.exception.ReportException;
import com.cybage.sonar.report.pdf.util.Credentials;
import com.cybage.sonar.report.pdf.util.Rating;
import com.cybage.sonar.report.pdf.util.SonarUtil;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.ChapterAutoNumber;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ExecutivePDFReporter extends PDFReporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutivePDFReporter.class);

	private static final String REPORT_TYPE_EXECUTIVE = "executive";

	private URL logo;
	private String projectKey;
	private Properties configProperties;
	private Properties langProperties;

	public ExecutivePDFReporter(final Credentials credentials, final URL logo, final String projectKey,
			final Properties configProperties, final Properties langProperties) {
		super(credentials);
		this.logo = logo;
		this.projectKey = projectKey;
		this.configProperties = configProperties;
		this.langProperties = langProperties;
	}

	@Override
	protected URL getLogo() {
		return this.logo;
	}

	@Override
	protected String getProjectKey() {
		return this.projectKey;
	}

	@Override
	protected Properties getLangProperties() {
		return langProperties;
	}

	@Override
	protected Properties getReportProperties() {
		return configProperties;
	}

	@Override
	protected void printFrontPage(final Document frontPageDocument, final PdfWriter frontPageWriter)
			throws ReportException {
		try {
			URL largeLogo;
			if (super.getConfigProperty("front.page.logo").startsWith("http://")) {
				largeLogo = new URL(super.getConfigProperty("front.page.logo"));
			} else {
				largeLogo = this.getClass().getClassLoader().getResource(super.getConfigProperty("front.page.logo"));
			}
			Image logoImage = Image.getInstance(largeLogo);
			logoImage.scaleAbsolute(360, 200);
			Rectangle pageSize = frontPageDocument.getPageSize();
			logoImage.setAbsolutePosition(Style.FRONTPAGE_LOGO_POSITION_X, Style.FRONTPAGE_LOGO_POSITION_Y);
			frontPageDocument.add(logoImage);

			PdfPTable title = new PdfPTable(1);
			title.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			title.getDefaultCell().setBorder(Rectangle.NO_BORDER);

			String projectRow = super.getProject().getName();
			// String versionRow =
			// super.getProject().getMeasures().getVersion();
			String versionRow = "1.1.1.1.1";
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			// String dateRow =
			// df.format(super.getProject().getMeasures().getDate());
			String dateRow = df.format(new Date());
			String descriptionRow = super.getProject().getDescription();

			title.addCell(new Phrase(projectRow, Style.FRONTPAGE_FONT_1));
			title.addCell(new Phrase(versionRow, Style.FRONTPAGE_FONT_1));
			title.addCell(new Phrase(descriptionRow, Style.FRONTPAGE_FONT_2));
			title.addCell(new Phrase(super.getProject().getMeasure(PROFILE).getValue(), Style.FRONTPAGE_FONT_3));
			title.addCell(new Phrase(dateRow, Style.FRONTPAGE_FONT_3));
			title.setTotalWidth(pageSize.getWidth() - frontPageDocument.leftMargin() - frontPageDocument.rightMargin());
			title.writeSelectedRows(0, -1, frontPageDocument.leftMargin(), Style.FRONTPAGE_LOGO_POSITION_Y - 150,
					frontPageWriter.getDirectContent());

		} catch (IOException e) {
			LOGGER.error("Can not generate front page", e);
		} catch (BadElementException e) {
			LOGGER.error("Can not generate front page", e);
		} catch (DocumentException e) {
			LOGGER.error("Can not generate front page", e);
		}
	}

	@Override
	protected void printPdfBody(final Document document) throws DocumentException, IOException, ReportException {
		try {
			Project project = super.getProject();
			// Chapter 1: Report Overview (Parent project)
			ChapterAutoNumber chapter1 = new ChapterAutoNumber(new Paragraph(project.getName(), Style.CHAPTER_FONT));
			chapter1.add(new Paragraph(getTextProperty("main.text.misc.overview"), Style.NORMAL_FONT));
			chapter1.add(new Paragraph(" ", new Font(FontFamily.COURIER, 8)));
			Section section11 = chapter1
					.addSection(new Paragraph(getTextProperty("general.report_overview"), Style.TITLE_FONT));
			printDashboard(project, section11);
			// Section section12 = chapter1.addSection(new Paragraph(
			// getTextProperty("general.violations_analysis"),
			// Style.TITLE_FONT));
			// printMostViolatedRules(project, section12);
			// printMostViolatedFiles(project, section12);
			// printMostComplexFiles(project, section12);
			// printMostDuplicatedFiles(project, section12);
			document.add(chapter1);

			/*
			 * Iterator<Project> it = project.getSubprojects().iterator(); while
			 * (it.hasNext()) { Project subproject = it.next();
			 * ChapterAutoNumber chapterN = new ChapterAutoNumber(new Paragraph(
			 * subproject.getName(), Style.CHAPTER_FONT));
			 * 
			 * Section sectionN1 = chapterN.addSection(new Paragraph(
			 * getTextProperty("general.report_overview"), Style.TITLE_FONT));
			 * printDashboard(subproject, sectionN1);
			 * 
			 * Section sectionN2 = chapterN.addSection(new Paragraph(
			 * getTextProperty("general.violations_analysis"),
			 * Style.TITLE_FONT)); printMostViolatedRules(subproject,
			 * sectionN2); printMostViolatedFiles(subproject, sectionN2);
			 * printMostComplexFiles(subproject, sectionN2);
			 * printMostDuplicatedFiles(subproject, sectionN2);
			 * document.add(chapterN); }
			 */
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Error in printPdfBody..");
			e.printStackTrace();
		}
	}

	protected void printDashboard(final Project project, final Section section) throws DocumentException {
		printReliabilityBoard(project, section);
		printSecurityBoard(project, section);
		printMaintainabilityBoard(project, section);
		// printDuplicationsBoard(project, section);
		// printSizeBoard(project, section);
		// printComplexityBoard(project, section);
		// printDocumentationBoard(project, section);
		// printIssuesBoard(project, section);
	}

	protected void printMostDuplicatedFiles(final Project project, final Section section) {
		/*
		 * List<FileInfo> files = project.getMostDuplicatedFiles();
		 * Iterator<FileInfo> it = files.iterator(); List<String> left = new
		 * LinkedList<String>(); List<String> right = new LinkedList<String>();
		 * 
		 * while (it.hasNext()) { FileInfo file = it.next();
		 * left.add(file.getName()); right.add(file.getDuplicatedLines()); }
		 * 
		 * PdfPTable mostDuplicatedFilesTable = Style.createSimpleTable(left,
		 * right, getTextProperty("general.most_duplicated_files"),
		 * getTextProperty("general.no_duplicated_files"));
		 * section.add(mostDuplicatedFilesTable);
		 */
	}

	protected void printMostComplexFiles(final Project project, final Section section) {
		/*
		 * List<FileInfo> files = project.getMostComplexFiles();
		 * Iterator<FileInfo> it = files.iterator(); List<String> left = new
		 * LinkedList<String>(); List<String> right = new LinkedList<String>();
		 * 
		 * while (it.hasNext()) { FileInfo file = it.next();
		 * left.add(file.getName()); right.add(file.getComplexity()); }
		 * 
		 * PdfPTable mostComplexFilesTable = Style.createSimpleTable(left,
		 * right, getTextProperty("general.most_complex_files"),
		 * getTextProperty("general.no_complex_files"));
		 * section.add(mostComplexFilesTable);
		 */
	}

	protected void printMostViolatedRules(final Project project, final Section section) {
		/*
		 * List<Rule> mostViolatedRules = project.getMostViolatedRules();
		 * Iterator<Rule> it = mostViolatedRules.iterator();
		 * 
		 * List<String> left = new LinkedList<String>(); List<String> right =
		 * new LinkedList<String>(); int limit = 0; while (it.hasNext() && limit
		 * < 5) { Rule rule = it.next(); left.add(rule.getName());
		 * right.add(String.valueOf(rule.getViolationsNumberFormatted()));
		 * limit++; }
		 * 
		 * PdfPTable mostViolatedRulesTable = Style.createSimpleTable(left,
		 * right, getTextProperty("general.most_violated_rules"),
		 * getTextProperty("general.no_violated_rules"));
		 * section.add(mostViolatedRulesTable);
		 */
	}

	protected void printMostViolatedFiles(final Project project, final Section section) {
		/*
		 * List<FileInfo> files = project.getMostViolatedFiles();
		 * Iterator<FileInfo> it = files.iterator(); List<String> left = new
		 * LinkedList<String>(); List<String> right = new LinkedList<String>();
		 * 
		 * while (it.hasNext()) { FileInfo file = it.next();
		 * left.add(file.getName()); right.add(file.getViolations()); }
		 * 
		 * PdfPTable mostViolatedFilesTable = Style.createSimpleTable(left,
		 * right, getTextProperty("general.most_violated_files"),
		 * getTextProperty("general.no_violated_files"));
		 * section.add(mostViolatedFilesTable);
		 */
	}

	@Override
	protected void printTocTitle(final Toc tocDocument) throws DocumentException {
		Paragraph tocTitle = new Paragraph(super.getTextProperty("main.table.of.contents"), Style.TOC_TITLE_FONT);
		tocTitle.setAlignment(Element.ALIGN_CENTER);
		tocDocument.getTocDocument().add(tocTitle);
		tocDocument.getTocDocument().add(Chunk.NEWLINE);
	}

	protected void printReliabilityBoard(final Project project, final Section section) throws DocumentException {
		// Reliability
		Paragraph reliabilityTitle = new Paragraph(getTextProperty("metrics." + RELIABILITY), Style.UNDERLINED_FONT);

		// Main Reliability Table
		PdfPTable tableReliability = new PdfPTable(3);
		tableReliability.setWidthPercentage(93);
		tableReliability.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableReliability.setWidths(new int[] { 2, 2, 2 });
		tableReliability.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// Bugs Table
		PdfPTable tableBugs = new PdfPTable(1);
		tableBugs.setSpacingAfter(5);
		tableBugs.getDefaultCell().setBorderColor(BaseColor.GRAY);

		PdfPCell bugsValue = new PdfPCell(new Phrase(project.getMeasure(BUGS).getValue(), Style.DASHBOARD_DATA_FONT));
		bugsValue.setVerticalAlignment(Element.ALIGN_CENTER);
		bugsValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		bugsValue.setExtraParagraphSpace(10);
		tableBugs.addCell(bugsValue);

		PdfPCell bugs = new PdfPCell(new Phrase(getTextProperty("metrics." + BUGS), Style.DASHBOARD_TITLE_FONT));
		bugs.setVerticalAlignment(Element.ALIGN_CENTER);
		bugs.setHorizontalAlignment(Element.ALIGN_CENTER);
		bugs.setExtraParagraphSpace(3);
		tableBugs.addCell(bugs);

		// New Bugs Table
		PdfPTable tableNewBugs = new PdfPTable(1);
		tableNewBugs.setSpacingAfter(5);

		PdfPCell newBugsValue = new PdfPCell(
				new Phrase(project.getMeasure(NEW_BUGS).getPeriods().get(0).getValue(), Style.DASHBOARD_DATA_FONT));
		newBugsValue.setVerticalAlignment(Element.ALIGN_CENTER);
		newBugsValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		newBugsValue.setBackgroundColor(Style.DASHBOARD_NEW_METRIC_BACKGROUND_COLOR);
		newBugsValue.setExtraParagraphSpace(10);
		tableNewBugs.addCell(newBugsValue);

		PdfPCell newBugs = new PdfPCell(new Phrase(getTextProperty("metrics." + NEW_BUGS), Style.DASHBOARD_TITLE_FONT));
		newBugs.setVerticalAlignment(Element.ALIGN_CENTER);
		newBugs.setHorizontalAlignment(Element.ALIGN_CENTER);
		newBugs.setExtraParagraphSpace(3);
		tableNewBugs.addCell(newBugs);

		// Reliability Rating Table
		PdfPTable tableReliabilityRating = new PdfPTable(1);
		tableReliabilityRating.setSpacingAfter(5);

		PdfPCell reliabilityRatingValue = new PdfPCell(
				new Phrase(Rating.getRating(project.getMeasure(RELIABILITY_RATING).getValue()),
						Rating.getRatingStyle(project.getMeasure(RELIABILITY_RATING).getValue())));
		reliabilityRatingValue.setVerticalAlignment(Element.ALIGN_CENTER);
		reliabilityRatingValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		reliabilityRatingValue.setExtraParagraphSpace(10);
		tableReliabilityRating.addCell(reliabilityRatingValue);

		PdfPCell reliabilityRating = new PdfPCell(
				new Phrase(getTextProperty("metrics." + RELIABILITY_RATING), Style.DASHBOARD_TITLE_FONT));
		reliabilityRating.setVerticalAlignment(Element.ALIGN_CENTER);
		reliabilityRating.setHorizontalAlignment(Element.ALIGN_CENTER);
		reliabilityRating.setExtraParagraphSpace(3);
		tableReliabilityRating.addCell(reliabilityRating);

		// Reliability Other Metrics Table
		PdfPTable tableReliabilityOther = new PdfPTable(1);
		tableReliabilityOther.setWidthPercentage(93);
		tableReliabilityOther.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		tableReliabilityOther.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

		// Reliability Remediation Effort Table
		PdfPTable tableReliabilityRemediationEffort = new PdfPTable(2);
		tableReliabilityRemediationEffort.setWidthPercentage(100);
		tableReliabilityRemediationEffort.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableReliabilityRemediationEffort.setWidths(new int[] { 8, 2 });

		PdfPCell reliabilityRemediationEffort = new PdfPCell(
				new Phrase(getTextProperty("metrics." + RELIABILITY_REMEDIATION_EFFORT), Style.DASHBOARD_TITLE_FONT));
		reliabilityRemediationEffort.setVerticalAlignment(Element.ALIGN_CENTER);
		reliabilityRemediationEffort.setHorizontalAlignment(Element.ALIGN_LEFT);
		reliabilityRemediationEffort.setExtraParagraphSpace(5);
		tableReliabilityRemediationEffort.addCell(reliabilityRemediationEffort);

		PdfPCell reliabilityRemediationEffortValue = new PdfPCell(new Phrase(
				SonarUtil
						.getConversion(Integer.parseInt(project.getMeasure(RELIABILITY_REMEDIATION_EFFORT).getValue())),
				Style.DASHBOARD_DATA_FONT_2));
		reliabilityRemediationEffortValue.setVerticalAlignment(Element.ALIGN_CENTER);
		reliabilityRemediationEffortValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
		reliabilityRemediationEffortValue.setExtraParagraphSpace(5);
		reliabilityRemediationEffortValue.setPaddingRight(2);
		tableReliabilityRemediationEffort.addCell(reliabilityRemediationEffortValue);

		tableReliabilityOther.addCell(tableReliabilityRemediationEffort);

		// Reliability Remediation Effort On New Code Table
		if (project.getMeasures().containsMeasure(NEW_RELIABILITY_REMEDIATION_EFFORT)) {
			PdfPTable tableReliabilityRemediationEffortNew = new PdfPTable(2);
			tableReliabilityRemediationEffortNew.setWidthPercentage(100);
			tableReliabilityRemediationEffortNew.setHorizontalAlignment(Element.ALIGN_CENTER);
			tableReliabilityRemediationEffortNew.setWidths(new int[] { 8, 2 });

			PdfPCell reliabilityRemediationEffortNew = new PdfPCell(new Phrase(
					getTextProperty("metrics." + NEW_RELIABILITY_REMEDIATION_EFFORT), Style.DASHBOARD_TITLE_FONT));
			reliabilityRemediationEffortNew.setVerticalAlignment(Element.ALIGN_CENTER);
			reliabilityRemediationEffortNew.setHorizontalAlignment(Element.ALIGN_LEFT);
			reliabilityRemediationEffortNew.setExtraParagraphSpace(5);
			tableReliabilityRemediationEffortNew.addCell(reliabilityRemediationEffortNew);

			PdfPCell reliabilityRemediationEffortNewValue = new PdfPCell(new Phrase(
					SonarUtil.getConversion(Integer.parseInt(
							project.getMeasure(NEW_RELIABILITY_REMEDIATION_EFFORT).getPeriods().get(0).getValue())),
					Style.DASHBOARD_DATA_FONT_2));
			reliabilityRemediationEffortNewValue.setVerticalAlignment(Element.ALIGN_CENTER);
			reliabilityRemediationEffortNewValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
			reliabilityRemediationEffortNewValue.setBackgroundColor(Style.DASHBOARD_NEW_METRIC_BACKGROUND_COLOR);
			reliabilityRemediationEffortNewValue.setExtraParagraphSpace(5);
			reliabilityRemediationEffortNewValue.setPaddingRight(2);
			tableReliabilityRemediationEffortNew.addCell(reliabilityRemediationEffortNewValue);

			tableReliabilityOther.addCell(tableReliabilityRemediationEffortNew);
		}

		tableReliability.addCell(tableBugs);
		tableReliability.addCell(tableNewBugs);
		tableReliability.addCell(tableReliabilityRating);

		section.add(new Paragraph(" "));
		section.add(reliabilityTitle);
		section.add(new Paragraph(" "));
		section.add(tableReliability);
		section.add(new Paragraph(" "));
		section.add(tableReliabilityOther);

	}

	protected void printSecurityBoard(final Project project, final Section section) throws DocumentException {

		// Security
		Paragraph securityTitle = new Paragraph(getTextProperty("metrics." + SECURITY), Style.UNDERLINED_FONT);

		// Main Security Table
		PdfPTable tableSecurity = new PdfPTable(3);
		tableSecurity.setWidthPercentage(93);
		tableSecurity.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableSecurity.setWidths(new int[] { 2, 2, 2 });
		tableSecurity.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// Vulnerabilities Table
		PdfPTable tableVulnerabilities = new PdfPTable(1);
		tableVulnerabilities.setSpacingAfter(5);
		tableVulnerabilities.getDefaultCell().setBorderColor(BaseColor.GRAY);

		PdfPCell vulnerabilitiesValue = new PdfPCell(
				new Phrase(project.getMeasure(VULNERABILITIES).getValue(), Style.DASHBOARD_DATA_FONT));
		vulnerabilitiesValue.setVerticalAlignment(Element.ALIGN_CENTER);
		vulnerabilitiesValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		vulnerabilitiesValue.setExtraParagraphSpace(10);
		tableVulnerabilities.addCell(vulnerabilitiesValue);

		PdfPCell vulnerabilities = new PdfPCell(
				new Phrase(getTextProperty("metrics." + VULNERABILITIES), Style.DASHBOARD_TITLE_FONT));
		vulnerabilities.setVerticalAlignment(Element.ALIGN_CENTER);
		vulnerabilities.setHorizontalAlignment(Element.ALIGN_CENTER);
		vulnerabilities.setExtraParagraphSpace(3);
		tableVulnerabilities.addCell(vulnerabilities);

		// New Vulnerabilities Table
		PdfPTable tableNewVulnerabilities = new PdfPTable(1);
		tableNewVulnerabilities.setSpacingAfter(5);

		PdfPCell newVulnerabilitiesValue = new PdfPCell(new Phrase(
				project.getMeasure(NEW_VULNERABILITIES).getPeriods().get(0).getValue(), Style.DASHBOARD_DATA_FONT));
		newVulnerabilitiesValue.setVerticalAlignment(Element.ALIGN_CENTER);
		newVulnerabilitiesValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		newVulnerabilitiesValue.setBackgroundColor(Style.DASHBOARD_NEW_METRIC_BACKGROUND_COLOR);
		newVulnerabilitiesValue.setExtraParagraphSpace(10);
		tableNewVulnerabilities.addCell(newVulnerabilitiesValue);

		PdfPCell newVulnerabilities = new PdfPCell(
				new Phrase(getTextProperty("metrics." + NEW_VULNERABILITIES), Style.DASHBOARD_TITLE_FONT));
		newVulnerabilities.setVerticalAlignment(Element.ALIGN_CENTER);
		newVulnerabilities.setHorizontalAlignment(Element.ALIGN_CENTER);
		newVulnerabilities.setExtraParagraphSpace(3);
		tableNewVulnerabilities.addCell(newVulnerabilities);

		// Security Rating Table
		PdfPTable tableSecurityRating = new PdfPTable(1);
		tableSecurityRating.setSpacingAfter(5);

		PdfPCell securityRatingValue = new PdfPCell(
				new Phrase(Rating.getRating(project.getMeasure(SECURITY_RATING).getValue()),
						Rating.getRatingStyle(project.getMeasure(SECURITY_RATING).getValue())));
		securityRatingValue.setVerticalAlignment(Element.ALIGN_CENTER);
		securityRatingValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		securityRatingValue.setExtraParagraphSpace(10);
		tableSecurityRating.addCell(securityRatingValue);

		PdfPCell securityRating = new PdfPCell(
				new Phrase(getTextProperty("metrics." + SECURITY_RATING), Style.DASHBOARD_TITLE_FONT));
		securityRating.setVerticalAlignment(Element.ALIGN_CENTER);
		securityRating.setHorizontalAlignment(Element.ALIGN_CENTER);
		securityRating.setExtraParagraphSpace(3);
		tableSecurityRating.addCell(securityRating);

		// Security Other Metrics Table
		PdfPTable tableSecurityOther = new PdfPTable(1);
		tableSecurityOther.setWidthPercentage(93);
		tableSecurityOther.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		tableSecurityOther.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

		// Security Remediation Effort Table
		PdfPTable tableSecurityRemediationEffort = new PdfPTable(2);
		tableSecurityRemediationEffort.setWidthPercentage(100);
		tableSecurityRemediationEffort.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableSecurityRemediationEffort.setWidths(new int[] { 8, 2 });

		PdfPCell securityRemediationEffort = new PdfPCell(
				new Phrase(getTextProperty("metrics." + SECURITY_REMEDIATION_EFFORT), Style.DASHBOARD_TITLE_FONT));
		securityRemediationEffort.setVerticalAlignment(Element.ALIGN_CENTER);
		securityRemediationEffort.setHorizontalAlignment(Element.ALIGN_LEFT);
		securityRemediationEffort.setExtraParagraphSpace(5);
		tableSecurityRemediationEffort.addCell(securityRemediationEffort);

		PdfPCell securityRemediationEffortValue = new PdfPCell(new Phrase(
				SonarUtil.getConversion(Integer.parseInt(project.getMeasure(SECURITY_REMEDIATION_EFFORT).getValue())),
				Style.DASHBOARD_DATA_FONT_2));
		securityRemediationEffortValue.setVerticalAlignment(Element.ALIGN_CENTER);
		securityRemediationEffortValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
		securityRemediationEffortValue.setExtraParagraphSpace(5);
		securityRemediationEffortValue.setPaddingRight(2);
		tableSecurityRemediationEffort.addCell(securityRemediationEffortValue);

		tableSecurityOther.addCell(tableSecurityRemediationEffort);

		// Security Remediation Effort on New Code Table
		if (project.getMeasures().containsMeasure(NEW_SECURITY_REMEDIATION_EFFORT)) {
			PdfPTable tableSecurityRemediationEffortNew = new PdfPTable(2);
			tableSecurityRemediationEffortNew.setWidthPercentage(100);
			tableSecurityRemediationEffortNew.setHorizontalAlignment(Element.ALIGN_CENTER);
			tableSecurityRemediationEffortNew.setWidths(new int[] { 8, 2 });

			PdfPCell securityRemediationEffortNew = new PdfPCell(new Phrase(
					getTextProperty("metrics." + NEW_SECURITY_REMEDIATION_EFFORT), Style.DASHBOARD_TITLE_FONT));
			securityRemediationEffortNew.setVerticalAlignment(Element.ALIGN_CENTER);
			securityRemediationEffortNew.setHorizontalAlignment(Element.ALIGN_LEFT);
			securityRemediationEffortNew.setExtraParagraphSpace(5);
			tableSecurityRemediationEffortNew.addCell(securityRemediationEffortNew);

			PdfPCell securityRemediationEffortNewValue = new PdfPCell(new Phrase(
					SonarUtil.getConversion(Integer.parseInt(
							project.getMeasure(NEW_SECURITY_REMEDIATION_EFFORT).getPeriods().get(0).getValue())),
					Style.DASHBOARD_DATA_FONT_2));
			securityRemediationEffortNewValue.setVerticalAlignment(Element.ALIGN_CENTER);
			securityRemediationEffortNewValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
			securityRemediationEffortNewValue.setBackgroundColor(Style.DASHBOARD_NEW_METRIC_BACKGROUND_COLOR);
			securityRemediationEffortNewValue.setExtraParagraphSpace(5);
			securityRemediationEffortNewValue.setPaddingRight(2);
			tableSecurityRemediationEffortNew.addCell(securityRemediationEffortNewValue);

			tableSecurityOther.addCell(tableSecurityRemediationEffortNew);
		}

		tableSecurity.addCell(tableVulnerabilities);
		tableSecurity.addCell(tableNewVulnerabilities);
		tableSecurity.addCell(tableSecurityRating);

		section.add(new Paragraph(" "));
		section.add(securityTitle);
		section.add(new Paragraph(" "));
		section.add(tableSecurity);
		section.add(new Paragraph(" "));
		section.add(tableSecurityOther);
	}

	protected void printMaintainabilityBoard(final Project project, final Section section) throws DocumentException {
		// Maintainability
		Paragraph maintainabilityTitle = new Paragraph(getTextProperty("metrics." + MAINTAINAILITY),
				Style.UNDERLINED_FONT);

		// Main Maintainability Table
		PdfPTable tableMaintainability = new PdfPTable(3);
		tableMaintainability.setWidthPercentage(93);
		tableMaintainability.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableMaintainability.setWidths(new int[] { 2, 2, 2 });
		tableMaintainability.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		// Code Smells Table
		PdfPTable tableCodeSmells = new PdfPTable(1);
		tableCodeSmells.setSpacingAfter(5);

		PdfPCell codeSmellsValue = new PdfPCell(
				new Phrase(project.getMeasure(CODE_SMELLS).getValue(), Style.DASHBOARD_DATA_FONT));
		codeSmellsValue.setVerticalAlignment(Element.ALIGN_CENTER);
		codeSmellsValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		codeSmellsValue.setExtraParagraphSpace(10);
		tableCodeSmells.addCell(codeSmellsValue);

		PdfPCell codeSmells = new PdfPCell(
				new Phrase(getTextProperty("metrics." + CODE_SMELLS), Style.DASHBOARD_TITLE_FONT));
		codeSmells.setVerticalAlignment(Element.ALIGN_CENTER);
		codeSmells.setHorizontalAlignment(Element.ALIGN_CENTER);
		codeSmells.setExtraParagraphSpace(3);
		tableCodeSmells.addCell(codeSmells);

		// New Code Smells Table
		PdfPTable tableNewCodeSmells = new PdfPTable(1);
		tableNewCodeSmells.setSpacingAfter(5);

		PdfPCell newCodeSmellsValue = new PdfPCell(new Phrase(
				project.getMeasure(NEW_CODE_SMELLS).getPeriods().get(0).getValue(), Style.DASHBOARD_DATA_FONT));
		newCodeSmellsValue.setVerticalAlignment(Element.ALIGN_CENTER);
		newCodeSmellsValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		newCodeSmellsValue.setBackgroundColor(Style.DASHBOARD_NEW_METRIC_BACKGROUND_COLOR);
		newCodeSmellsValue.setExtraParagraphSpace(10);
		tableNewCodeSmells.addCell(newCodeSmellsValue);

		PdfPCell newCodeSmells = new PdfPCell(
				new Phrase(getTextProperty("metrics." + NEW_CODE_SMELLS), Style.DASHBOARD_TITLE_FONT));
		newCodeSmells.setVerticalAlignment(Element.ALIGN_CENTER);
		newCodeSmells.setHorizontalAlignment(Element.ALIGN_CENTER);
		newCodeSmells.setExtraParagraphSpace(3);
		tableNewCodeSmells.addCell(newCodeSmells);

		// Maintainability Rating Table
		PdfPTable tableMaintainabilityRating = new PdfPTable(1);
		tableMaintainabilityRating.setSpacingAfter(5);
		
		PdfPCell maintainabilityRatingValue = new PdfPCell(
				new Phrase(Rating.getRating(project.getMeasure(SQALE_RATING).getValue()),
						Rating.getRatingStyle(project.getMeasure(SQALE_RATING).getValue())));
		maintainabilityRatingValue.setVerticalAlignment(Element.ALIGN_CENTER);
		maintainabilityRatingValue.setHorizontalAlignment(Element.ALIGN_CENTER);
		maintainabilityRatingValue.setExtraParagraphSpace(10);
		tableMaintainabilityRating.addCell(maintainabilityRatingValue);

		PdfPCell maintainabilityRating = new PdfPCell(
				new Phrase(getTextProperty("metrics." + SQALE_RATING), Style.DASHBOARD_TITLE_FONT));
		maintainabilityRating.setVerticalAlignment(Element.ALIGN_CENTER);
		maintainabilityRating.setHorizontalAlignment(Element.ALIGN_CENTER);
		maintainabilityRating.setExtraParagraphSpace(3);
		tableMaintainabilityRating.addCell(maintainabilityRating);

		// Maintainability Other Metrics Table
		PdfPTable tableMaintainabilityOther = new PdfPTable(1);
		tableMaintainabilityOther.setWidthPercentage(93);
		tableMaintainabilityOther.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		tableMaintainabilityOther.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

		// Technical Debt Table
		PdfPTable tableTechnicalDebt = new PdfPTable(2);
		tableTechnicalDebt.setWidthPercentage(100);
		tableTechnicalDebt.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableTechnicalDebt.setWidths(new int[] { 8, 2 });

		PdfPCell technicalDebt = new PdfPCell(
				new Phrase(getTextProperty("metrics." + SQALE_INDEX), Style.DASHBOARD_TITLE_FONT));
		technicalDebt.setVerticalAlignment(Element.ALIGN_CENTER);
		technicalDebt.setHorizontalAlignment(Element.ALIGN_LEFT);
		technicalDebt.setExtraParagraphSpace(5);
		tableTechnicalDebt.addCell(technicalDebt);

		PdfPCell technicalDebtValue = new PdfPCell(
				new Phrase(SonarUtil.getConversion(Integer.parseInt(project.getMeasure(SQALE_INDEX).getValue())),
						Style.DASHBOARD_DATA_FONT_2));
		technicalDebtValue.setVerticalAlignment(Element.ALIGN_CENTER);
		technicalDebtValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
		technicalDebtValue.setExtraParagraphSpace(5);
		technicalDebtValue.setPaddingRight(2);

		tableTechnicalDebt.addCell(technicalDebtValue);

		tableMaintainabilityOther.addCell(tableTechnicalDebt);

		// Added Technical Debt Table
		if (project.getMeasures().containsMeasure(NEW_TECHNICAL_DEBT)) {
			PdfPTable tableTechnicalDebtNew = new PdfPTable(2);
			tableTechnicalDebtNew.setWidthPercentage(100);
			tableTechnicalDebtNew.setHorizontalAlignment(Element.ALIGN_CENTER);
			tableTechnicalDebtNew.setWidths(new int[] { 8, 2 });

			PdfPCell technicalDebtNew = new PdfPCell(
					new Phrase(getTextProperty("metrics." + NEW_TECHNICAL_DEBT), Style.DASHBOARD_TITLE_FONT));
			technicalDebtNew.setVerticalAlignment(Element.ALIGN_CENTER);
			technicalDebtNew.setHorizontalAlignment(Element.ALIGN_LEFT);
			technicalDebtNew.setExtraParagraphSpace(5);
			tableTechnicalDebtNew.addCell(technicalDebtNew);

			PdfPCell technicalDebtNewValue = new PdfPCell(new Phrase(
					SonarUtil.getConversion(
							Integer.parseInt(project.getMeasure(NEW_TECHNICAL_DEBT).getPeriods().get(0).getValue())),
					Style.DASHBOARD_DATA_FONT_2));
			technicalDebtNewValue.setVerticalAlignment(Element.ALIGN_CENTER);
			technicalDebtNewValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
			technicalDebtNewValue.setBackgroundColor(Style.DASHBOARD_NEW_METRIC_BACKGROUND_COLOR);
			technicalDebtNewValue.setExtraParagraphSpace(5);
			technicalDebtNewValue.setPaddingRight(2);
			tableTechnicalDebtNew.addCell(technicalDebtNewValue);

			tableMaintainabilityOther.addCell(tableTechnicalDebtNew);
		}

		// Technical Debt Ratio Table
		PdfPTable tableTechnicalDebtRatio = new PdfPTable(2);
		tableTechnicalDebtRatio.setWidthPercentage(100);
		tableTechnicalDebtRatio.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableTechnicalDebtRatio.setWidths(new int[] { 8, 2 });

		PdfPCell technicalDebtRatio = new PdfPCell(
				new Phrase(getTextProperty("metrics." + SQALE_DEBT_RATIO), Style.DASHBOARD_TITLE_FONT));
		technicalDebtRatio.setVerticalAlignment(Element.ALIGN_CENTER);
		technicalDebtRatio.setHorizontalAlignment(Element.ALIGN_LEFT);
		technicalDebtRatio.setExtraParagraphSpace(5);
		tableTechnicalDebtRatio.addCell(technicalDebtRatio);

		PdfPCell technicalDebtRatioValue = new PdfPCell(
				new Phrase(project.getMeasure(SQALE_DEBT_RATIO).getValue() + "%", Style.DASHBOARD_DATA_FONT_2));
		technicalDebtRatioValue.setVerticalAlignment(Element.ALIGN_CENTER);
		technicalDebtRatioValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
		technicalDebtRatioValue.setExtraParagraphSpace(5);
		technicalDebtRatioValue.setPaddingRight(2);

		tableTechnicalDebtRatio.addCell(technicalDebtRatioValue);

		tableMaintainabilityOther.addCell(tableTechnicalDebtRatio);

		// Technical Debt Ratio on New Code Table
		if (project.getMeasures().containsMeasure(NEW_SQALE_DEBT_RATIO)) {
			PdfPTable tableTechnicalDebtRatioNew = new PdfPTable(2);
			tableTechnicalDebtRatioNew.setWidthPercentage(100);
			tableTechnicalDebtRatioNew.setHorizontalAlignment(Element.ALIGN_CENTER);
			tableTechnicalDebtRatioNew.setWidths(new int[] { 8, 2 });

			PdfPCell technicalDebtRatioNew = new PdfPCell(
					new Phrase(getTextProperty("metrics." + NEW_SQALE_DEBT_RATIO), Style.DASHBOARD_TITLE_FONT));
			technicalDebtRatioNew.setVerticalAlignment(Element.ALIGN_CENTER);
			technicalDebtRatioNew.setHorizontalAlignment(Element.ALIGN_LEFT);
			technicalDebtRatioNew.setExtraParagraphSpace(5);
			tableTechnicalDebtRatioNew.addCell(technicalDebtRatioNew);

			PdfPCell technicalDebtRatioNewValue = new PdfPCell(
					new Phrase(project.getMeasure(NEW_SQALE_DEBT_RATIO).getPeriods().get(0).getValue() + "%",
							Style.DASHBOARD_DATA_FONT_2));
			technicalDebtRatioNewValue.setVerticalAlignment(Element.ALIGN_CENTER);
			technicalDebtRatioNewValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
			technicalDebtRatioNewValue.setBackgroundColor(Style.DASHBOARD_NEW_METRIC_BACKGROUND_COLOR);
			technicalDebtRatioNewValue.setExtraParagraphSpace(5);
			technicalDebtRatioNewValue.setPaddingRight(2);
			tableTechnicalDebtRatioNew.addCell(technicalDebtRatioNewValue);

			tableMaintainabilityOther.addCell(tableTechnicalDebtRatioNew);
		}

		// Effort To Reach Maintainability Rating A Table
		PdfPTable tableEffortToReachMaintainabilityRatingA = new PdfPTable(2);
		tableEffortToReachMaintainabilityRatingA.setWidthPercentage(100);
		tableEffortToReachMaintainabilityRatingA.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableEffortToReachMaintainabilityRatingA.setWidths(new int[] { 8, 2 });

		PdfPCell effortToReachMaintainabilityRatingA = new PdfPCell(new Phrase(
				getTextProperty("metrics." + EFFORT_TO_REACH_MAINTAINABILITY_RATING_A), Style.DASHBOARD_TITLE_FONT));
		effortToReachMaintainabilityRatingA.setVerticalAlignment(Element.ALIGN_CENTER);
		effortToReachMaintainabilityRatingA.setHorizontalAlignment(Element.ALIGN_LEFT);
		effortToReachMaintainabilityRatingA.setExtraParagraphSpace(5);
		tableEffortToReachMaintainabilityRatingA.addCell(effortToReachMaintainabilityRatingA);

		PdfPCell effortToReachMaintainabilityRatingAValue = new PdfPCell(new Phrase(
				SonarUtil.getConversion(
						Integer.parseInt(project.getMeasure(EFFORT_TO_REACH_MAINTAINABILITY_RATING_A).getValue())),
				Style.DASHBOARD_DATA_FONT_2));
		effortToReachMaintainabilityRatingAValue.setVerticalAlignment(Element.ALIGN_CENTER);
		effortToReachMaintainabilityRatingAValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
		effortToReachMaintainabilityRatingAValue.setExtraParagraphSpace(5);
		effortToReachMaintainabilityRatingAValue.setPaddingRight(2);

		tableEffortToReachMaintainabilityRatingA.addCell(effortToReachMaintainabilityRatingAValue);

		tableMaintainabilityOther.addCell(tableEffortToReachMaintainabilityRatingA);

		tableMaintainability.addCell(tableCodeSmells);
		tableMaintainability.addCell(tableNewCodeSmells);
		tableMaintainability.addCell(tableMaintainabilityRating);

		section.add(new Paragraph(" "));
		section.add(maintainabilityTitle);
		section.add(new Paragraph(" "));
		section.add(tableMaintainability);
		section.add(new Paragraph(" "));
		section.add(tableMaintainabilityOther);

	}

	protected void printDuplicationsBoard(final Project project, final Section section) throws DocumentException {

	}

	protected void printSizeBoard(final Project project, final Section section) throws DocumentException {

	}

	protected void printComplexityBoard(final Project project, final Section section) throws DocumentException {

	}

	protected void printDocumentationBoard(final Project project, final Section section) throws DocumentException {

	}

	protected void printIssuesBoard(final Project project, final Section section) throws DocumentException {

	}

	@Override
	public String getReportType() {
		return REPORT_TYPE_EXECUTIVE;
	}
}