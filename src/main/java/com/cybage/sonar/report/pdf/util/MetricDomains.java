package com.cybage.sonar.report.pdf.util;

import static com.cybage.sonar.report.pdf.util.MetricKeys.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricDomains {

	public static final String RELIABILITY = "reliability";
	public static final String SECURITY = "security";
	public static final String MAINTAINAILITY = "maintainability";
	public static final String DUPLICATIONS = "duplications";
	public static final String SIZE = "size";
	public static final String COMPLEXITY = "complexity";
	public static final String DOCUMENTATION = "documentation";
	public static final String ISSUES = "issues";

	private static final Map<String, List<String>> metricMap;

	static {
		metricMap = new HashMap<>();
		metricMap.put(RELIABILITY, Arrays.asList(BUGS, NEW_BUGS, RELIABILITY_RATING, RELIABILITY_REMEDIATION_EFFORT,
				NEW_RELIABILITY_REMEDIATION_EFFORT));
		metricMap.put(SECURITY, Arrays.asList(VULNERABILITIES, NEW_VULNERABILITIES, SECURITY_RATING,
				SECURITY_REMEDIATION_EFFORT, NEW_SECURITY_REMEDIATION_EFFORT));
		metricMap.put(MAINTAINAILITY, Arrays.asList(CODE_SMELLS, NEW_CODE_SMELLS, SQALE_RATING, SQALE_INDEX,
				NEW_TECHNICAL_DEBT, SQALE_DEBT_RATIO, NEW_SQALE_DEBT_RATIO, EFFORT_TO_REACH_MAINTAINABILITY_RATING_A));
		metricMap.put(DUPLICATIONS,
				Arrays.asList(DUPLICATED_LINES_DENSITY, DUPLICATED_BLOCKS, DUPLICATED_LINES, DUPLICATED_FILES));
		metricMap.put(SIZE, Arrays.asList(NCLOC, LINES, STATEMENTS, FUNCTIONS, CLASSES, FILES, DIRECTORIES));
		metricMap.put(COMPLEXITY, Arrays.asList(COMPLEXITY, FUNCTION_COMPLEXITY, FILE_COMPLEXITY, CLASS_COMPLEXITY));
		metricMap.put(DOCUMENTATION, Arrays.asList(COMMENT_LINES_DENSITY, COMMENT_LINES));
		metricMap.put(ISSUES, Arrays.asList(VIOLATIONS, NEW_VIOLATIONS, OPEN_ISSUES, REOPENED_ISSUES, CONFIRMED_ISSUES,
				FALSE_POSITIVE_ISSUES, WONT_FIX_ISSUES));
	}

	public static List<String> getMetricKeys(String domain) {
		return metricMap.get(domain);
	}

}
