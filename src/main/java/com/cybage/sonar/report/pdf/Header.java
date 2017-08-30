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
package com.cybage.sonar.report.pdf;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cybage.sonar.report.pdf.entity.Project;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfLayer;
import com.itextpdf.text.pdf.events.PdfPageEventForwarder;


public class Header extends PdfPageEventForwarder {

  private static final Logger LOG = LoggerFactory.getLogger(Header.class);

  private URL logo;
  private Project project;

  public Header(final URL logo, final Project project) {
    this.logo = logo;
    this.project = project;
  }
  
  /*@Override
  public void onEndPage(final PdfLayer writer, final Document document) {
    try {
      Image logoImage = Image.getInstance(logo);
      Rectangle page = document.getPageSize();
      PdfPTable head = new PdfPTable(4);
      head.getDefaultCell().setVerticalAlignment(PdfCell.ALIGN_MIDDLE);
      head.getDefaultCell().setHorizontalAlignment(PdfCell.ALIGN_CENTER);
      head.addCell(logoImage);
      Phrase projectName = new Phrase(project.getName(), FontFactory.getFont(
          FontFactory.COURIER, 12, Font.NORMAL, Color.GRAY));
      Phrase phrase = new Phrase("Sonar PDF Report", FontFactory.getFont(
          FontFactory.COURIER, 12, Font.NORMAL, Color.GRAY));
      head.getDefaultCell().setColspan(2);
      head.addCell(phrase);
      head.getDefaultCell().setColspan(1);
      head.addCell(projectName);
      head.setTotalWidth(page.getWidth() - document.leftMargin()
          - document.rightMargin());
      head.writeSelectedRows(0, -1, document.leftMargin(),
          page.getHeight() - 20, writer.getDirectContent());
      head.setSpacingAfter(10);
    } catch (BadElementException e) {
      LOG.error("Can not generate PDF header", e);
    } catch (MalformedURLException e) {
      LOG.error("Can not generate PDF header", e);
    } catch (IOException e) {
      LOG.error("Can not generate PDF header", e);
    }
  }*/

}
