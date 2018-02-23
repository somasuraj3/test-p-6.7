package com.cybage.sonar.report.pdf.builder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.HttpDownloader.HttpException;
import org.sonarqube.ws.WsComponents.ShowWsResponse;
import org.sonarqube.ws.WsQualityGates.ProjectStatusWsResponse;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.component.ShowWsRequest;
import org.sonarqube.ws.client.qualitygate.ProjectStatusWsRequest;

import com.cybage.sonar.report.pdf.entity.FileInfo;
import com.cybage.sonar.report.pdf.entity.Issue;
import com.cybage.sonar.report.pdf.entity.Measures;
import com.cybage.sonar.report.pdf.entity.Project;
import com.cybage.sonar.report.pdf.entity.ProjectStatus;
import com.cybage.sonar.report.pdf.entity.QualityProfile;
import com.cybage.sonar.report.pdf.entity.Rule;
import com.cybage.sonar.report.pdf.entity.exception.ReportException;
import com.itextpdf.text.DocumentException;

public class ProjectBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBuilder.class);

	private static ProjectBuilder builder;

	private WsClient wsClient;

	public ProjectBuilder(final WsClient wsClient) {
		this.wsClient = wsClient;
	}

	public static ProjectBuilder getInstance(final WsClient wsClient) {
		if (builder == null) {
			return new ProjectBuilder(wsClient);
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
	public Project initializeProject(final String key, final String version, final List<String> sonarLanguage,
			final Set<String> otherMetrics, final Set<String> typesOfIssue) throws IOException, ReportException {
		Project project = new Project(key, version, sonarLanguage);

		try {

			LOGGER.info("Retrieving project info for " + project.getKey());

			ShowWsRequest showWsReq = new ShowWsRequest();
			showWsReq.setKey(project.getKey());
			ShowWsResponse showWsRes = wsClient.components().show(showWsReq);

			ProjectStatusWsRequest projectStatusWsReq = new ProjectStatusWsRequest();
			projectStatusWsReq.setProjectKey(key);
			ProjectStatusWsResponse projectStatusWsRes = wsClient.qualityGates().projectStatus(projectStatusWsReq);

			if (showWsRes != null) {
				initFromNode(project, showWsRes, projectStatusWsRes);
				initMeasures(project, otherMetrics);
				initMostViolatedRules(project);
				initMostViolatedFiles(project);
				initMostComplexFiles(project);
				initMostDuplicatedFiles(project);
				if (typesOfIssue.size() > 0) {
					initIssueDetails(project, typesOfIssue);
				}
			} else {
				LOGGER.info("Can't retrieve project info. Have you set username/password in Sonar settings?");
				throw new ReportException("Can't retrieve project info. Parent project node is empty. Authentication?");
			}
		} catch (Exception ex) {
			LOGGER.error("Exception in initializeProject()");
			ex.printStackTrace();
		}
		return project;
	}

	/**
	 * Initialize project object and his childs (except categories violations).
	 * 
	 * @throws IOException
	 */
	private void initFromNode(final Project project, final ShowWsResponse resourceNode,
			final ProjectStatusWsResponse projectStatusWsRes) throws IOException {

		// Set Project Name
		project.setName(resourceNode.getComponent().getName());

		// Set Project Description
		project.setDescription(resourceNode.getComponent().getDescription());
		// project.setLinks(new LinkedList<String>());
		project.setSubprojects(new LinkedList<Project>());

		// Set Project Status
		initProjectStatus(project);
		initQualityProfiles(project);

		project.setMostViolatedRules(new LinkedList<Rule>());
		project.setMostComplexFiles(new LinkedList<FileInfo>());
		project.setMostDuplicatedFiles(new LinkedList<FileInfo>());
		project.setMostViolatedFiles(new LinkedList<FileInfo>());
	}

	private void initMeasures(final Project project, final Set<String> otherMetrics)
			throws IOException, HttpException, ReportException {
		LOGGER.info("Retrieving measures");
		MeasuresBuilder measuresBuilder = MeasuresBuilder.getInstance(wsClient);
		Measures measures = measuresBuilder.initMeasuresByProjectKey(project.getKey(), otherMetrics);
		project.setMeasures(measures);
	}

	private void initProjectStatus(final Project project) throws IOException {
		LOGGER.info("Retrieving project status");
		ProjectStatusBuilder projectStatusBuilder = ProjectStatusBuilder.getInstance(wsClient);
		ProjectStatus projectStatus = projectStatusBuilder.initProjectStatusByProjectKey(project.getKey());
		project.setProjectStatus(projectStatus);
	}

	private void initQualityProfiles(final Project project) throws IOException {
		LOGGER.info("Retrieving quality profile information");
		QualityProfileBuilder qualityProfileBuilder = QualityProfileBuilder.getInstance(wsClient);
		List<QualityProfile> qualityProfiles = qualityProfileBuilder
				.initProjectQualityProfilesByProjectKey(project.getKey());
		project.setQualityProfiles(qualityProfiles);
	}

	private void initMostViolatedRules(final Project project) throws IOException, ReportException {
		LOGGER.info("Retrieving most violated rules");
		RuleBuilder ruleBuilder = RuleBuilder.getInstance(wsClient);
		List<Rule> rules = ruleBuilder.initProjectMostViolatedRulesByProjectKey(project.getKey());
		project.setMostViolatedRules(rules);
	}

	private void initMostViolatedFiles(final Project project) throws IOException, ReportException {
		LOGGER.info("Retrieving most violated files");
		FileInfoBuilder fileInfoBuilder = FileInfoBuilder.getInstance(wsClient);
		List<FileInfo> filesInfo = fileInfoBuilder.initProjectMostViolatedFilesByProjectKey(project.getKey());
		project.setMostViolatedFiles(filesInfo);
	}

	private void initMostComplexFiles(final Project project) throws IOException, ReportException {
		LOGGER.info("Retrieving most complex files");
		FileInfoBuilder fileInfoBuilder = FileInfoBuilder.getInstance(wsClient);
		List<FileInfo> filesInfo = fileInfoBuilder.initProjectMostComplexFilesByProjectKey(project.getKey());
		project.setMostComplexFiles(filesInfo);
	}

	private void initMostDuplicatedFiles(final Project project) throws IOException, ReportException {
		LOGGER.info("Retrieving most duplicated files");
		FileInfoBuilder fileInfoBuilder = FileInfoBuilder.getInstance(wsClient);
		List<FileInfo> filesInfo = fileInfoBuilder.initProjectMostDuplicatedFilesByProjectKey(project.getKey());
		project.setMostDuplicatedFiles(filesInfo);
	}

	private void initIssueDetails(final Project project, final Set<String> typesOfIssue)
			throws IOException, ReportException {
		LOGGER.info("Retrieving issue details");
		IssueBuilder issueBuilder = IssueBuilder.getInstance(wsClient);
		List<Issue> issues = issueBuilder.initIssueDetailsByProjectKey(project.getKey(), typesOfIssue);
		project.setIssues(issues);
	}
}
