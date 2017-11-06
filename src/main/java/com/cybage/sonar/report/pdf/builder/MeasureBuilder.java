package com.cybage.sonar.report.pdf.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sonarqube.ws.Common.Metric;
import org.sonarqube.ws.WsMeasures.PeriodValue;

import com.cybage.sonar.report.pdf.entity.Measure;
import com.cybage.sonar.report.pdf.entity.Period;

public class MeasureBuilder {

	/**
	 * Init measure from XML node. The root node must be "msr".
	 * 
	 * @param measureNode
	 * @return
	 */
	public static Measure initFromNode(final org.sonarqube.ws.WsMeasures.Measure measureNode, Optional<Metric> metric) {

		if (measureNode == null) {
			System.out.println("measureNode is null");
		}

		PeriodValue periodValue1 = measureNode.getPeriods().getPeriodsValue(0);
		PeriodValue periodValue2 = measureNode.getPeriods().getPeriodsValue(1);
		PeriodValue periodValue3 = measureNode.getPeriods().getPeriodsValue(2);

		List<Period> periods = new ArrayList<>();
		periods.add(new Period(periodValue1.getIndex(), periodValue1.getValue()));
		periods.add(new Period(periodValue2.getIndex(), periodValue2.getValue()));
		periods.add(new Period(periodValue3.getIndex(), periodValue3.getValue()));

		return new Measure(measureNode.getMetric(), measureNode.getValue(), metric.get().getName(),
				metric.get().getType(), metric.get().getDomain(), metric.get().getHigherValuesAreBetter(), periods);

	}
}
