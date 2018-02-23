package com.cybage.sonar.report.pdf.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonarqube.ws.Common.FacetValue;
import org.sonarqube.ws.Issues.Component;
import org.sonarqube.ws.Issues.SearchWsResponse;
import org.sonarqube.ws.WsMeasures.ComponentTreeWsResponse;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.issue.SearchWsRequest;
import org.sonarqube.ws.client.measure.ComponentTreeWsRequest;

import com.cybage.sonar.report.pdf.entity.FileInfo;
import com.cybage.sonar.report.pdf.util.MetricKeys;

public class FileInfoBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStatusBuilder.class);

	private static FileInfoBuilder builder;

	private WsClient wsClient;

	public FileInfoBuilder(final WsClient wsClient) {
		this.wsClient = wsClient;
	}

	public static FileInfoBuilder getInstance(final WsClient wsClient) {
		if (builder == null) {
			return new FileInfoBuilder(wsClient);
		}

		return builder;
	}

	public List<FileInfo> initProjectMostViolatedFilesByProjectKey(final String key) {

		// LOGGER.info("Retrieving most violated files info for " + key);
		List<FileInfo> files = new ArrayList<>();

		// Reverse iteration to get violations with upper level first

		SearchWsRequest searchWsReq = new SearchWsRequest();
		searchWsReq.setComponentKeys(Arrays.asList(key));
		searchWsReq.setFacets(Arrays.asList("fileUuids"));
		searchWsReq.setPageSize(500);
		SearchWsResponse searchWsRes = wsClient.issues().search(searchWsReq);

		if (searchWsRes.getFacets().getFacets(0) != null) {
			int limit = 5;
			limit = searchWsRes.getFacets().getFacets(0).getValuesCount() > limit ? limit
					: searchWsRes.getFacets().getFacets(0).getValuesCount();

			int j = 0;
			while (j < limit) {
				FacetValue facetValue = searchWsRes.getFacets().getFacets(0).getValues(j);
				Optional<Component> component = searchWsRes.getComponentsList().stream()
						.filter(c -> c.getUuid().equals(facetValue.getVal()) && c.getQualifier().equals("FIL"))
						.findFirst();
				if (component.isPresent()) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setKey(facetValue.getVal());
					fileInfo.setName(component.get().getName());
					fileInfo.setPath(component.get().getPath());
					fileInfo.setViolations(String.valueOf(facetValue.getCount()));
					fileInfo.setComplexity("0");
					fileInfo.setDuplicatedLines("0");
					files.add(fileInfo);
					j++;
				}
			}
		} else {
			LOGGER.debug("There are no violated files");
		}
		return files;
	}

	public List<FileInfo> initProjectMostComplexFilesByProjectKey(final String key) {

		// LOGGER.info("Retrieving most complex files info for " + key);

		List<FileInfo> files = new ArrayList<>();

		int limit = 5;
		ComponentTreeWsRequest compTreeWsReq = new ComponentTreeWsRequest();
		compTreeWsReq.setComponent(key);
		compTreeWsReq.setMetricKeys(Arrays.asList(MetricKeys.COMPLEXITY));
		compTreeWsReq.setMetricSort(MetricKeys.COMPLEXITY);
		compTreeWsReq.setSort(Arrays.asList("metric"));
		compTreeWsReq.setMetricSortFilter("withMeasuresOnly");
		compTreeWsReq.setQualifiers(Arrays.asList("FIL"));
		ComponentTreeWsResponse componentTreeWsRes = wsClient.measures().componentTree(compTreeWsReq);

		if (componentTreeWsRes.getComponentsList() != null) {
			limit = componentTreeWsRes.getComponentsCount() > limit ? limit : componentTreeWsRes.getComponentsCount();
			for (int j = componentTreeWsRes.getComponentsCount() - 1; j >= componentTreeWsRes.getComponentsCount()
					- limit; j--) {
				org.sonarqube.ws.WsMeasures.Component component = componentTreeWsRes.getComponents(j);

				/*
				 * LOGGER.info("File Info : Measures Count : " +
				 * String.valueOf(component.getMeasuresCount()));
				 * LOGGER.info("Measure Values List : "); for (Measure measure :
				 * component.getMeasuresList()) {
				 * LOGGER.info(measure.toString()); }
				 */

				FileInfo fileInfo = new FileInfo();
				fileInfo.setKey(component.getId());
				fileInfo.setName(component.getName());
				fileInfo.setPath(component.getPath());
				fileInfo.setViolations("0");
				fileInfo.setComplexity(String.valueOf(component.getMeasures(0).getValue()));
				fileInfo.setDuplicatedLines("0");
				files.add(fileInfo);
			}
		} else {
			LOGGER.debug("There are no complex files");
		}
		return files;
	}

	public List<FileInfo> initProjectMostDuplicatedFilesByProjectKey(final String key) {

		// LOGGER.info("Retrieving most duplicated files info for " + key);

		List<FileInfo> files = new ArrayList<>();

		int limit = 5;
		ComponentTreeWsRequest compTreeWsReq = new ComponentTreeWsRequest();
		compTreeWsReq.setComponent(key);
		compTreeWsReq.setMetricKeys(Arrays.asList(MetricKeys.DUPLICATED_LINES));
		compTreeWsReq.setMetricSort(MetricKeys.DUPLICATED_LINES);
		compTreeWsReq.setSort(Arrays.asList("metric"));
		compTreeWsReq.setMetricSortFilter("withMeasuresOnly");
		compTreeWsReq.setQualifiers(Arrays.asList("FIL"));
		ComponentTreeWsResponse componentTreeWsRes = wsClient.measures().componentTree(compTreeWsReq);

		if (componentTreeWsRes.getComponentsList() != null) {
			limit = componentTreeWsRes.getComponentsCount() > limit ? limit : componentTreeWsRes.getComponentsCount();
			for (int j = componentTreeWsRes.getComponentsCount() - 1; j >= componentTreeWsRes.getComponentsCount()
					- limit; j--) {
				org.sonarqube.ws.WsMeasures.Component component = componentTreeWsRes.getComponents(j);

				FileInfo fileInfo = new FileInfo();
				fileInfo.setKey(component.getId());
				fileInfo.setName(component.getName());
				fileInfo.setPath(component.getPath());
				fileInfo.setViolations("0");
				fileInfo.setComplexity("0");
				fileInfo.setDuplicatedLines(String.valueOf(component.getMeasures(0).getValue()));
				files.add(fileInfo);
			}
		} else {
			LOGGER.debug("There are no duplicated files");
		}
		return files;

	}

}
