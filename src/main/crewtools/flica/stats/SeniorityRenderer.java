/**
 * Copyright 2018 Iron City Software LLC
 *
 * This file is part of CrewTools.
 *
 * CrewTools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CrewTools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CrewTools.  If not, see <http://www.gnu.org/licenses/>.
 */

package crewtools.flica.stats;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.joda.time.YearMonth;

import com.google.common.base.Preconditions;

import crewtools.flica.Proto.Domicile;
import crewtools.flica.stats.BaseList.Member;

public class SeniorityRenderer {
  private static final String OUTPUT_PATH = "/tmp/bl.html";
  private final List<BaseList> lists;
  private final Map<YearMonth, LineCount> lineCounts;
  private final YearMonth startingYearMonth;
  private final Domicile domicile;

  private static final String CSS = "html, body {\n"
      + "  font-family: \"Trebuchet MS, Helvetica, sans-serif\";\n"
      + "}\n"
      + "table {\n"
      + "  border-collapse: collapse;\n"
      + "  margin: 0;\n"
      + "  padding: 0;\n"
      + "}\n"
      + "table td {\n"
      + "  vertical-align: top;\n"
      + "  white-space: nowrap;\n"
      + "}\n"
      + "table.inner td {\n"
      + "  padding: 0.3em;\n"
      + "  border: 1px solid black;\n"
      + "}\n"
      + "td.arrived {\n"
      + "  background-color: #addfff;\n"
      + "}\n"
      + "td.departed {\n"
      + "  color: gray;\n"
      + "}\n"
      + "td.interesting {\n"
      + "  background-color: yellow;\n"
      + "}\n"
      + "td.heading {\n"
      + "  font-variant: small-caps;\n"
      + "  text-align: center;\n"
      + "  background-color: black;\n"
      + "  color: white;\n"
      + "  margin: 0;\n"
      + "  padding: 0;\n"
      + "}\n";

  public SeniorityRenderer(
      List<BaseList> lists,
      Map<YearMonth, LineCount> lineCounts,
      YearMonth startingYearMonth,
      Domicile domicile) {
    this.lists = lists;
    this.lineCounts = lineCounts;
    this.startingYearMonth = startingYearMonth;
    this.domicile = domicile;
  }

  public void render() throws FileNotFoundException {
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(
        new FileOutputStream(OUTPUT_PATH)));
    writer.println("<html><head><style>");
    writer.println(CSS);
    writer.println("</style></head><body>");
    writer.println(String.format("<h2>Seniority Predictions &bull; %s &bull; %s</h2>",
        startingYearMonth, domicile));
    writer.println("<p><table class=\"inner\">"
        + "<tr><td class=\"departed\">Last month in base</td>"
        + "<td class=\"arrived\">New to base this month</td></tr></table>");
    writer.println("<p><table><tr>");
    for (BaseList list : lists) {
      writer.println("<td>");
      writer.println("  <table class=\"inner\"><tr><td colspan=2>");
      writer.print(list.getYearMonth() + " " + list.getHeader());
      writer.println("</td></tr>");
      List<Member> members = list.getMembers();
      for (int i = 0; i < members.size(); ++i) {
        int employeeId = members.get(i).employeeId;
        String cssClass = "";
        if (list.hasCssClass(employeeId)) {
          cssClass = String.format(" class=\"%s\"", list.getCssClass(employeeId));
        }
        writer.printf("<tr><td%s>%d</td><td%s>%s</td></tr>\n",
            cssClass,
            i + 1,
            cssClass,
            members.get(i).format());
        LineCount lineCount = Preconditions.checkNotNull(
            lineCounts.get(list.getYearMonth()),
            "Missing line counts for " + list.getYearMonth());
        if (i + 1 == lineCount.getNumRoundOne()) {
          writer.printf("<tr><td colspan=2 class=\"heading\" "
              + ">Round Two</td></tr>\n");
        } else if (i + 1 == lineCount.getNumRoundOne() + lineCount.getNumRoundTwo()) {
          writer.printf("<tr><td colspan=2 class=\"heading\" "
              + ">LCR</td></tr>\n");
        } else if (i + 1 == lineCount.getNumRoundOne() + lineCount.getNumRoundTwo()
            + lineCount.getNumLongCall()) {
          writer.printf("<tr><td colspan=2 class=\"heading\" "
              + ">SCR</td></tr>\n");
        }
      }
      writer.println("</table></td>");
    }
    writer.println("</table>");
    writer.println("</body></html>");
    writer.close();
    System.err.println("Wrote to " + OUTPUT_PATH);
  }
}
