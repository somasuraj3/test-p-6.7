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
