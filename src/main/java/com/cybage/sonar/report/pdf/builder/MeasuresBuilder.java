package com.cybage.sonar.report.pdf.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.HttpDownloader.HttpException;
import org.sonarqube.ws.Common.Metric;
import org.sonarqube.ws.WsMeasures.ComponentWsResponse;
import org.sonarqube.ws.WsMeasures.Metrics;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.measure.ComponentWsRequest;

import com.cybage.sonar.report.pdf.entity.Measure;
import com.cybage.sonar.report.pdf.entity.Measures;
import com.cybage.sonar.report.pdf.entity.Period_;
import com.cybage.sonar.report.pdf.entity.exception.ReportException;
import com.cybage.sonar.report.pdf.util.MetricKeys;

public class MeasuresBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(MeasuresBuilder.class);

	private static MeasuresBuilder builder;

	private WsClient wsClient;

	private static Set<String> measuresKeys = null;

	private static Integer DEFAULT_SPLIT_LIMIT = 20;

	public MeasuresBuilder(final WsClient wsClient) {
		this.wsClient = wsClient;
	}

	public static MeasuresBuilder getInstance(final WsClient wsClient) {
		if (builder == null) {
			return new MeasuresBuilder(wsClient);
		}

		return builder;
	}

	public Measures initMeasuresByProjectKey(final String projectKey, final Set<String> otherMetrics)
			throws HttpException, IOException, ReportException {

		Measures measures = new Measures();
		if (measuresKeys == null) {
			measuresKeys = MetricKeys.getAllMetricKeys();
			if (otherMetrics != null) {
				measuresKeys.addAll(otherMetrics);
			}
		}

		// Avoid "Post too large"
		if (measuresKeys.size() > DEFAULT_SPLIT_LIMIT) {
			initMeasuresSplittingRequests(measures, projectKey);
		} else {
			this.addMeasures(measures, measuresKeys, projectKey);
		}

		return measures;

	}

	/**
	 * This method does the required requests to get all measures from Sonar,
	 * but taking care to avoid too large requests (measures are taken by 20).
	 * @throws ReportException 
	 */
	private void initMeasuresSplittingRequests(final Measures measures, final String projectKey)
			throws HttpException, IOException, ReportException {
		Iterator<String> it = measuresKeys.iterator();
		// LOGGER.debug("Getting " + measuresKeys.size() + " metric measures from Sonar by splitting requests");
		Set<String> twentyMeasures = new HashSet<String>(20);
		int i = 0;
		while (it.hasNext()) {
			twentyMeasures.add(it.next());
			i++;
			if (i % DEFAULT_SPLIT_LIMIT == 0) {
				// LOGGER.debug("Split request for: " + twentyMeasures);
				addMeasures(measures, twentyMeasures, projectKey);
				i = 0;
				twentyMeasures.clear();
			}
		}
		if (i != 0) {
			// LOGGER.debug("Split request for remain metric measures: " + twentyMeasures);
			addMeasures(measures, twentyMeasures, projectKey);
		}
	}

	/**
	 * Add measures to this.
	 * @throws ReportException 
	 */
	private void addMeasures(final Measures measures, final Set<String> measuresAsString, final String projectKey)
			throws HttpException, IOException, ReportException {

		ComponentWsRequest compWsReq = new ComponentWsRequest();
		compWsReq.setComponent(projectKey);
		compWsReq.setAdditionalFields(Arrays.asList("metrics", "periods"));
		compWsReq.setMetricKeys(new ArrayList<String>(measuresAsString));

		ComponentWsResponse compWsRes = wsClient.measures().component(compWsReq);

		if (compWsRes.getComponent().getMeasuresCount() != 0) {
			this.addAllMeasuresFromDocument(measures, compWsRes);
		} else {
			LOGGER.debug("Empty response when looking for measures: " + measuresAsString.toString());
		}
	}

	private void addAllMeasuresFromDocument(final Measures measures, final ComponentWsResponse compWsRes) throws ReportException {
		List<org.sonarqube.ws.WsMeasures.Measure> allNodes = compWsRes.getComponent().getMeasuresList();
		Metrics metrics = compWsRes.getMetrics();
		List<org.sonarqube.ws.WsMeasures.Period> periods = compWsRes.getPeriods().getPeriodsList();
		
		if(periods.size() == 0){
			throw new ReportException("Invalid leak period. Please set appropriate leak period.");
		}
		measures.setPeriods(
				periods.stream().map(p -> new Period_(p.getIndex(), p.getMode(), p.getDate(), p.getParameter()))
						.collect(Collectors.toList()));
		for (org.sonarqube.ws.WsMeasures.Measure measure : allNodes) {
			addMeasureFromNode(measures, measure,
					metrics.getMetricsList().stream().filter(m -> m.getKey().equals(measure.getMetric())).findFirst());
		}
	}

	private void addMeasureFromNode(final Measures measures, final org.sonarqube.ws.WsMeasures.Measure measureNode,
			Optional<Metric> metric) {
		Measure measure = MeasureBuilder.initFromNode(measureNode, measures.getPeriods(), metric);
		measures.addMeasure(measure.getMetric(), measure);
	}
}
