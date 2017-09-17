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
package com.cybage.sonar.report.pdf.entity;

import java.util.List;

public class Measure {

	private String metric;
	private String value;
	private String metricTitle;
	private String dataType;
	private String domain;
	private Boolean higherValuesAreBetter;
	private List<Period> periods = null;

	public Measure(String metric, String value, String metricTitle, String dataType, String domain,
			Boolean higherValuesAreBetter, List<Period> periods) {
		super();
		this.metric = metric;
		this.value = value;
		this.metricTitle = metricTitle;
		this.dataType = dataType;
		this.domain = domain;
		this.higherValuesAreBetter = higherValuesAreBetter;
		this.periods = periods;
	}

	public Measure() {
		this.metric = null;
		this.value = null;
		this.metricTitle = null;
		this.dataType = null;
		this.domain = null;
		this.higherValuesAreBetter = null;
		this.periods = null;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMetricTitle() {
		return metricTitle;
	}

	public void setMetricTitle(String metricTitle) {
		this.metricTitle = metricTitle;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Boolean getHigherValuesAreBetter() {
		return higherValuesAreBetter;
	}

	public void setHigherValuesAreBetter(Boolean higherValuesAreBetter) {
		this.higherValuesAreBetter = higherValuesAreBetter;
	}

	public List<Period> getPeriods() {
		return periods;
	}

	public void setPeriods(List<Period> periods) {
		this.periods = periods;
	}

	@Override
	public String toString() {
		return "Measure [metric=" + metric + ", value=" + value + ", metricTitle=" + metricTitle + ", dataType="
				+ dataType + ", domain=" + domain + ", higherValuesAreBetter=" + higherValuesAreBetter + ", periods="
				+ periods + "]";
	}

}