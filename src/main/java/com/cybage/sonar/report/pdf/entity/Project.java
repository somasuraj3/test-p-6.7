package com.cybage.sonar.report.pdf.entity;

import java.util.Iterator;
import java.util.List;

public class Project {

	  // Project info
	  private short id;
	  private String key;
	  private String name;
	  private String description;
	  private List<String> links;

	  // Measures
	  private Measures measures;

	  // Child projects
	  private List<Project> subprojects;

	  // Most violated rules
	  private List<Rule> mostViolatedRules;

	  // Most complex elements
	  private List<FileInfo> mostComplexFiles;

	  // Most violated files
	  private List<FileInfo> mostViolatedFiles;

	  // Most duplicated files
	  private List<FileInfo> mostDuplicatedFiles;

	  public Project(final String key) {
	    this.key = key;
	  }

	  public Measure getMeasure(final String measureKey) {
	    if (measures.containsMeasure(measureKey)) {
	      return measures.getMeasure(measureKey);
	    } else {
	      return new Measure(null, "N/A");
	    }
	  }

	  public Project getChildByKey(final String key) {
	    Iterator<Project> it = this.subprojects.iterator();
	    while (it.hasNext()) {
	      Project child = it.next();
	      if (child.getKey().equals(key)) {
	        return child;
	      }
	    }
	    return null;
	  }

	  public void setId(final short id) {
	    this.id = id;
	  }

	  public void setKey(final String key) {
	    this.key = key;
	  }

	  public void setName(final String name) {
	    this.name = name;
	  }

	  public void setDescription(final String description) {
	    this.description = description;
	  }

	  public void setLinks(final List<String> links) {
	    this.links = links;
	  }

	  public short getId() {
	    return id;
	  }

	  public String getKey() {
	    return key;
	  }

	  public String getName() {
	    return name;
	  }

	  public String getDescription() {
	    return description;
	  }

	  public List<String> getLinks() {
	    return links;
	  }

	  public List<Project> getSubprojects() {
	    return subprojects;
	  }

	  public void setSubprojects(final List<Project> subprojects) {
	    this.subprojects = subprojects;
	  }

	  public Measures getMeasures() {
	    return measures;
	  }

	  public void setMeasures(final Measures measures) {
	    this.measures = measures;
	  }

	  public List<Rule> getMostViolatedRules() {
	    return mostViolatedRules;
	  }

	  public List<FileInfo> getMostViolatedFiles() {
	    return mostViolatedFiles;
	  }

	  public void setMostViolatedRules(final List<Rule> mostViolatedRules) {
	    this.mostViolatedRules = mostViolatedRules;
	  }

	  public void setMostViolatedFiles(final List<FileInfo> mostViolatedFiles) {
	    this.mostViolatedFiles = mostViolatedFiles;
	  }

	  public void setMostComplexFiles(final List<FileInfo> mostComplexFiles) {
	    this.mostComplexFiles = mostComplexFiles;
	  }

	  public List<FileInfo> getMostComplexFiles() {
	    return mostComplexFiles;
	  }

	  public List<FileInfo> getMostDuplicatedFiles() {
	    return mostDuplicatedFiles;
	  }

	  public void setMostDuplicatedFiles(final List<FileInfo> mostDuplicatedFiles) {
	    this.mostDuplicatedFiles = mostDuplicatedFiles;
	  }
	}

