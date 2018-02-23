package com.cybage.sonar.report.pdf.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sonarqube.ws.Common.Metric;
import org.sonarqube.ws.WsMeasures.PeriodValue;

import com.cybage.sonar.report.pdf.entity.Measure;
import com.cybage.sonar.report.pdf.entity.Period;
import com.cybage.sonar.report.pdf.entity.Period_;

public class MeasureBuilder {

	/**
	 * Init measure from XML node. The root node must be "msr".
	 * 
	 * @param measureNode
	 * @return
	 */
	public static Measure initFromNode(final org.sonarqube.ws.WsMeasures.Measure measureNode, List<Period_> periods_,
			Optional<Metric> metric) {
		
		List<Period> periods = new ArrayList<>();
		
		for (int i = 0; i < periods_.size(); i++) {
			PeriodValue periodValue = measureNode.getPeriods().getPeriodsValue(i);
			periods.add(new Period(periodValue.getIndex(), periodValue.getValue()));
		}

		return new Measure(measureNode.getMetric(), measureNode.getValue(), metric.get().getName(),
				metric.get().getType(), metric.get().getDomain(), metric.get().getHigherValuesAreBetter(), periods);

	}
}
