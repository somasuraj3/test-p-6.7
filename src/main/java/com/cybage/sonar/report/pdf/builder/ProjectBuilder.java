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
package com.cybage.sonar.report.pdf.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.HttpDownloader.HttpException;
import org.sonarqube.ws.WsComponents.Component;
import org.sonarqube.ws.WsComponents.ShowWsResponse;
import org.sonarqube.ws.WsComponents.TreeWsResponse;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.component.ShowWsRequest;
import org.sonarqube.ws.client.component.TreeWsRequest;

import com.cybage.sonar.report.pdf.PDFReporter;
import com.cybage.sonar.report.pdf.entity.Measures;
import com.cybage.sonar.report.pdf.entity.Project;
import com.cybage.sonar.report.pdf.entity.exception.ReportException;
import com.cybage.sonar.report.pdf.util.Credentials;
import com.itextpdf.text.DocumentException;


public class ProjectBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger("org.sonar.PDF");

	private static ProjectBuilder builder;

	private Credentials credentials;

	private WsClient wsClient;

	private PDFReporter pdfRefporter;

	public ProjectBuilder(final Credentials credentials, final WsClient wsClient, final PDFReporter pdfRefporter) {
		this.credentials = credentials;
		this.wsClient = wsClient;
		this.pdfRefporter = pdfRefporter;
	}

	public static ProjectBuilder getInstance(final Credentials credentials, final WsClient wsClient,
			final PDFReporter pdfRefporter) {
		if (builder == null) {
			return new ProjectBuilder(credentials, wsClient, pdfRefporter);
		}

		return builder;
	}

	/**
	 * Initialize: - Project basic data - Project measures - Project categories
	 * violations - Project most violated rules - Project most violated files -
	 * Project most duplicated files
	 * 
	 * @param sonarAccess
	 * @throws HttpException
	 * @throws IOException
	 * @throws DocumentException
	 * @throws ReportException
	 */
	public Project initializeProject(final String key) throws IOException, ReportException {
		Project project = new Project(key);

		LOGGER.info("Retrieving project info for " + project.getKey());

		ShowWsRequest showWsReq = new ShowWsRequest();
		showWsReq.setKey(project.getKey());
		ShowWsResponse showWsRes = wsClient.components().show(showWsReq);
		
		if (showWsRes != null) {
			initFromNode(project, showWsRes);
			initMeasures(project);
			//initMostViolatedRules(project);
			//initMostViolatedFiles(project);
			//initMostComplexElements(project);
			//initMostDuplicatedFiles(project);
			LOGGER.debug("Accessing Sonar: getting child projects");

			TreeWsRequest treeWsReq = new TreeWsRequest();
			treeWsReq.setBaseComponentKey(project.getKey());
			treeWsReq.setQualifiers(Arrays.asList("BRC"));
			//resourceQueryChild.setDepth(1);
			//List<InputComponent> childNodes = wsClient.findAll(treeWsReq);
			TreeWsResponse treeWsRes = wsClient.components().tree(treeWsReq);
			

			Iterator<Component> it = treeWsRes.getComponentsList().iterator(); 
			project.setSubprojects(new ArrayList<Project>(0));
			if (!it.hasNext()) {
				LOGGER.debug(project.getKey() + " project has no childs");
			}
			while (it.hasNext()) {
				Component childNode = it.next();
				Project childProject = initializeProject(childNode.getKey());
				project.getSubprojects().add(childProject);
			}
		} else {
			LOGGER.info("Can't retrieve project info. Have you set username/password in Sonar settings?");
			throw new ReportException("Can't retrieve project info. Parent project node is empty. Authentication?");
		}

		return project;
	}

	/**
	 * Initialize project object and his childs (except categories violations).
	 */
	private void initFromNode(final Project project, final ShowWsResponse resourceNode) {
		project.setName(resourceNode.getComponent().getName());
		project.setDescription(resourceNode.getComponent().getDescription());
		//project.setLinks(new LinkedList<String>());
		project.setSubprojects(new LinkedList<Project>());
		//project.setMostViolatedRules(new LinkedList<Rule>());
		//project.setMostComplexFiles(new LinkedList<FileInfo>());
		//project.setMostDuplicatedFiles(new LinkedList<FileInfo>());
		//project.setMostViolatedFiles(new LinkedList<FileInfo>());
	}

	private void initMeasures(final Project project) throws IOException {
		LOGGER.info("    Retrieving measures");
		MeasuresBuilder measuresBuilder = MeasuresBuilder.getInstance(wsClient);
		Measures measures = measuresBuilder.initMeasuresByProjectKey(project.getKey());
		project.setMeasures(measures);
	}
	
/*
	private void initMostViolatedRules(final Project project) throws IOException, ReportException {
		LOGGER.info("    Retrieving most violated rules");
		LOGGER.debug("Accessing Sonar: getting most violated rules");
		String[] priorities = Priority.getPrioritiesArray();

		// Reverse iteration to get violations with upper level first
		int limit = 10;
		for (int i = priorities.length - 1; i >= 0 && limit > 0; i--) {

			ResourceQuery query = ResourceQuery.create(project.getKey());
			query.setDepth(0);
			query.setLimit(limit);
			query.setMetrics(UrlPath.getViolationsLevelPath(priorities[i]));

			// "&filter_rules=false&filter_rules_cats=true" ??
			query.setExcludeRules(false);
			// query.setExcludeRuleCategories(true);

			InputComponent mostViolatedRulesByLevel = wsClient.find(query);
			if (mostViolatedRulesByLevel != null) {
				int count = initMostViolatedRulesFromNode(project, mostViolatedRulesByLevel);
				LOGGER.debug("\t " + count + " " + priorities[i] + " violations");
				limit = limit - count;
			} else {
				LOGGER.debug("There is not result on select //resources/resource");
				LOGGER.debug("There are no violations with level " + priorities[i]);
			}
		}
	}

	private void initMostViolatedFiles(final Project project) throws IOException {
		LOGGER.info("    Retrieving most violated files");
		LOGGER.debug("Accessing Sonar: getting most violated files");

		ResourceQuery resourceQuery = ResourceQuery.createForMetrics(project.getKey(), MetricKeys.VIOLATIONS);
		resourceQuery.setScopes("FIL");
		resourceQuery.setDepth(-1);
		resourceQuery.setLimit(5);
		List<InputComponent> resources = wsClient.findAll(resourceQuery);
		List<FileInfo> fileInfoList = FileInfoBuilder.initFromDocument(resources, FileInfo.VIOLATIONS_CONTENT);
		project.setMostViolatedFiles(fileInfoList);

	}

	private void initMostComplexElements(final Project project) throws IOException {
		LOGGER.info("    Retrieving most complex elements");
		LOGGER.debug("Accessing Sonar: getting most complex elements");

		ResourceQuery resourceQuery = ResourceQuery.createForMetrics(project.getKey(), MetricKeys.COMPLEXITY);
		resourceQuery.setScopes("FIL");
		resourceQuery.setDepth(-1);
		resourceQuery.setLimit(5);
		List<InputComponent> resources = wsClient.findAll(resourceQuery);
		project.setMostComplexFiles(FileInfoBuilder.initFromDocument(resources, FileInfo.CCN_CONTENT));
	}

	private void initMostDuplicatedFiles(final Project project) throws IOException {
		LOGGER.info("    Retrieving most duplicated files");
		LOGGER.debug("Accessing Sonar: getting most duplicated files");

		ResourceQuery resourceQuery = ResourceQuery.createForMetrics(project.getKey(), MetricKeys.DUPLICATED_LINES);
		resourceQuery.setScopes("FIL");
		resourceQuery.setDepth(-1);
		resourceQuery.setLimit(5);
		List<InputComponent> resources = wsClient.findAll(resourceQuery);
		project.setMostDuplicatedFiles(FileInfoBuilder.initFromDocument(resources, FileInfo.DUPLICATIONS_CONTENT));
	}

	private int initMostViolatedRulesFromNode(final Project project, final InputComponent mostViolatedNode)
			throws ReportException, IOException {

		RuleBuilder ruleBuilder = RuleBuilder.getInstance(credentials, wsClient);

		List<org.sonar.wsclient.services.Measure> measuresNode = mostViolatedNode.getMeasures();
		Iterator<org.sonar.wsclient.services.Measure> it = measuresNode.iterator();
		if (!it.hasNext()) {
			LOGGER.warn("There is not result on select //resources/resource/msr");
		}
		int count = 0;
		while (it.hasNext()) {
			org.sonar.wsclient.services.Measure measureNode = it.next();
			String formattedValueNode = measureNode.getFormattedValue();
			if (!formattedValueNode.equals("0")) {
				Rule rule = ruleBuilder.initFromNode(measureNode);
				if ("workbook".equals(pdfRefporter.getReportType())) {
					ruleBuilder.loadViolatedResources(rule, rule.getKey(), project.getKey());
				}
				project.getMostViolatedRules().add(rule);
				count++;
			}
		}
		return count;
	}
*/
}
