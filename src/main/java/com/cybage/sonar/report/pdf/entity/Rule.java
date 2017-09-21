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

public class Rule {

	// Rule key
	private String key;

	// Rule name
	private String name;

	// Rule count
	private Long count;

	// Language Name
	private String languageName;

	// Severity
	private String severity;

	public Rule() {
		super();
		this.key = "";
		this.name = "";
		this.count = 0L;
		this.languageName = "";
		this.severity = "";
	}

	public Rule(String key, String name, Long count, String languageName, String severity) {
		super();
		this.key = key;
		this.name = name;
		this.count = count;
		this.languageName = languageName;
		this.severity = severity;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	@Override
	public String toString() {
		return "Rule [key=" + key + ", name=" + name + ", count=" + count + ", languageName=" + languageName
				+ ", severity=" + severity + "]";
	}

}
