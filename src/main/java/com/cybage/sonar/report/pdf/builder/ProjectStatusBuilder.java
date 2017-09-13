package com.cybage.sonar.report.pdf.builder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonarqube.ws.WsQualityGates.ProjectStatusWsResponse;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.qualitygate.ProjectStatusWsRequest;

import com.cybage.sonar.report.pdf.entity.Condition;
import com.cybage.sonar.report.pdf.entity.ProjectStatus;
import com.cybage.sonar.report.pdf.entity.StatusPeriod;

public class ProjectStatusBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStatusBuilder.class);

	private static ProjectStatusBuilder builder;

	private WsClient wsClient;

	public ProjectStatusBuilder(final WsClient wsClient) {
		this.wsClient = wsClient;
	}

	public static ProjectStatusBuilder getInstance(final WsClient wsClient) {
		if (builder == null) {
			return new ProjectStatusBuilder(wsClient);
		}

		return builder;
	}
	
	public ProjectStatus initProjectStatusByProjectKey(final String key) {
		
		LOGGER.info("Retrieving project status info for " + key);
		
		ProjectStatusWsRequest projectStatusWsReq = new ProjectStatusWsRequest();
		projectStatusWsReq.setProjectKey(key);
		ProjectStatusWsResponse projectStatusWsRes = wsClient.qualityGates().projectStatus(projectStatusWsReq);
		
		List<Condition> conditions = new ArrayList<>();
		for (org.sonarqube.ws.WsQualityGates.ProjectStatusWsResponse.Condition condition : projectStatusWsRes
				.getProjectStatus().getConditionsList()) {
			Condition cond = new Condition(condition.getStatus().toString(), condition.getMetricKey(),
					condition.getComparator().toString(), condition.getPeriodIndex(), condition.getErrorThreshold(),
					condition.getActualValue(), condition.getWarningThreshold());
			conditions.add(cond);

		}

		List<StatusPeriod> statusPeriods = new ArrayList<>();
		for (org.sonarqube.ws.WsQualityGates.ProjectStatusWsResponse.Period period : projectStatusWsRes
				.getProjectStatus().getPeriodsList()) {
			StatusPeriod statusPeriod = new StatusPeriod();
			statusPeriod.setIndex(period.getIndex());
			statusPeriod.setMode(period.getMode());
			if (period.getDate() != null) {
				statusPeriod.setDate(period.getDate());
			}
			if (period.getParameter() != null) {
				statusPeriod.setParameter(period.getParameter());
			}
			statusPeriods.add(statusPeriod);
		}
		
		
		return new ProjectStatus(projectStatusWsRes.getProjectStatus().getStatus().toString(), conditions, statusPeriods);
		
	}
}
