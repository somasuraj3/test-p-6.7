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
package com.cybage.sonar.report.pdf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;

public class MetricKeys {

	// Reliability Metric Keys
	public static final String BUGS = "bugs";
	public static final String NEW_BUGS = "new_bugs";
	public static final String RELIABILITY_RATING = "reliability_rating";
	public static final String RELIABILITY_REMEDIATION_EFFORT = "reliability_remediation_effort";
	public static final String NEW_RELIABILITY_REMEDIATION_EFFORT = "new_reliability_remediation_effort";

	// SECURITY
	public static final String VULNERABILITIES = "vulnerabilities";
	public static final String NEW_VULNERABILITIES = "new_vulnerabilities";
	public static final String SECURITY_RATING = "security_rating";
	public static final String SECURITY_REMEDIATION_EFFORT = "security_remediation_effort";
	public static final String NEW_SECURITY_REMEDIATION_EFFORT = "new_security_remediation_effort";

	// Maintainability
	public static final String CODE_SMELLS = "code_smells";
	public static final String NEW_CODE_SMELLS = "new_code_smells";
	public static final String SQALE_RATING = "sqale_rating";
	public static final String SQALE_INDEX = "sqale_index";
	public static final String NEW_TECHNICAL_DEBT = "new_technical_debt";
	public static final String SQALE_DEBT_RATIO = "sqale_debt_ratio";
	public static final String NEW_SQALE_DEBT_RATIO = "new_sqale_debt_ratio";
	public static final String EFFORT_TO_REACH_MAINTAINABILITY_RATING_A = "effort_to_reach_maintainability_rating_a";

	// Duplications
	public static final String DUPLICATED_LINES_DENSITY = "duplicated_lines_density";
	public static final String DUPLICATED_BLOCKS = "duplicated_blocks";
	public static final String DUPLICATED_LINES = "duplicated_lines";
	public static final String DUPLICATED_FILES = "duplicated_files";

	// SIZE
	public static final String NCLOC = "ncloc";
	public static final String LINES = "lines";
	public static final String STATEMENTS = "statements";
	public static final String FUNCTIONS = "functions";
	public static final String CLASSES = "classes";
	public static final String FILES = "files";
	public static final String DIRECTORIES = "directories";

	// Complexity
	public static final String COMPLEXITY = "complexity";
	public static final String FUNCTION_COMPLEXITY = "function_complexity";
	public static final String FILE_COMPLEXITY = "file_complexity";
	public static final String CLASS_COMPLEXITY = "class_complexity";

	// Documentations
	public static final String COMMENT_LINES_DENSITY = "comment_lines_density";
	public static final String COMMENT_LINES = "comment_lines";

	// Issues
	public static final String VIOLATIONS = "violations";
	public static final String NEW_VIOLATIONS = "new_violations";
	public static final String OPEN_ISSUES = "open_issues";
	public static final String REOPENED_ISSUES = "reopened_issues";
	public static final String CONFIRMED_ISSUES = "confirmed_issues";
	public static final String FALSE_POSITIVE_ISSUES = "false_positive_issues";
	public static final String WONT_FIX_ISSUES = "wont_fix_issues";

	// Others
	public static final String PROFILE = "profile";

	// Get all metric keys as list
	public static List<String> getAllMetricKeys() {
		List<String> allMetricKeys = new ArrayList<String>();
		allMetricKeys.addAll(Arrays.asList(MetricKeys.BUGS, MetricKeys.NEW_BUGS, MetricKeys.RELIABILITY_RATING,
				MetricKeys.NEW_RELIABILITY_REMEDIATION_EFFORT, MetricKeys.RELIABILITY_REMEDIATION_EFFORT,
				MetricKeys.VULNERABILITIES, MetricKeys.NEW_VULNERABILITIES, MetricKeys.SECURITY_RATING,
				MetricKeys.SECURITY_REMEDIATION_EFFORT, MetricKeys.NEW_SECURITY_REMEDIATION_EFFORT,
				MetricKeys.CODE_SMELLS, MetricKeys.NEW_CODE_SMELLS, MetricKeys.SQALE_RATING, MetricKeys.SQALE_INDEX,
				MetricKeys.NEW_TECHNICAL_DEBT, MetricKeys.SQALE_DEBT_RATIO, MetricKeys.NEW_SQALE_DEBT_RATIO,
				MetricKeys.EFFORT_TO_REACH_MAINTAINABILITY_RATING_A, MetricKeys.DUPLICATED_LINES_DENSITY,
				MetricKeys.DUPLICATED_BLOCKS, MetricKeys.DUPLICATED_LINES, MetricKeys.DUPLICATED_FILES,
				MetricKeys.NCLOC, MetricKeys.LINES, MetricKeys.STATEMENTS, MetricKeys.FUNCTIONS, MetricKeys.CLASSES,
				MetricKeys.FILES, MetricKeys.DIRECTORIES, MetricKeys.COMPLEXITY, MetricKeys.FUNCTION_COMPLEXITY,
				MetricKeys.FILE_COMPLEXITY, MetricKeys.CLASS_COMPLEXITY, MetricKeys.COMMENT_LINES_DENSITY,
				MetricKeys.COMMENT_LINES, MetricKeys.VIOLATIONS, MetricKeys.NEW_VIOLATIONS, MetricKeys.OPEN_ISSUES,
				MetricKeys.REOPENED_ISSUES, MetricKeys.CONFIRMED_ISSUES, MetricKeys.FALSE_POSITIVE_ISSUES,
				MetricKeys.WONT_FIX_ISSUES));
		return allMetricKeys;
	}

	// Get list of strings as single string separated by comma
	public static String getAllMetricKeysAsString(List<String> metricKeys) {
		return Joiner.on(",").join(metricKeys);
	}
}
