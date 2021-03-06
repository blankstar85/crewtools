/**
 * Copyright 2019 Iron City Software LLC
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

/** "Fix" last data column being empty by appending a delimiter. */
public class WorkaroundColumn<T> extends Column<T> {
  public WorkaroundColumn(String label) {
    super(label);
  }

  @Override
  public String getDatum(int index) {
    String datum = super.getDatum(index);
    return datum.isEmpty() ? "," : datum;
  }
}
