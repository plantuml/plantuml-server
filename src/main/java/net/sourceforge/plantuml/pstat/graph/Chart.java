/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * Project Info:  http://plantuml.sourceforge.net
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */
package net.sourceforge.plantuml.pstat.graph;

import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.pstat.WriterOutputStream;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.svg.UGraphicSvg;

public class Chart {

	class Data {
		final private String label;
		final private int value;

		Data(String label, int value) {
			this.label = label;
			this.value = value;
		}

		private double getAngle() {
			return 360.0 * value / sum;
		}

		private String getFullLabel() {
			final long pour = 100L * value / sum;
			return label + " [" + pour + "%]  [" + value + "/" + sum + "]";
		}

		public String getLabelShort() {
			if (label.length() > 40) {
				return "...";
			}
			return label;
		}
	}

	private final List<Data> datas = new ArrayList<Data>();
	private long sum;
	private final UFont fontComment = new UFont("", Font.BOLD, 12);

	public void compact() {
		final List<Data> newList = new ArrayList<Data>();
		final StringBuilder relicat = new StringBuilder();
		int relicatValue = 0;
		final int limit = (int) (sum / 100);
		for (Data d : datas) {
			if (d.value > limit) {
				newList.add(d);
			} else {
				if (relicat.length() > 0) {
					relicat.append(" - ");
				}
				relicat.append(d.label);
				relicatValue += d.value;
			}
		}
		if (relicatValue > 0) {
			newList.add(new Data(relicat.toString(), relicatValue));
		}
		datas.clear();
		datas.addAll(newList);
	}

	public void removeBig(double pour) {
		final List<Data> newList = new ArrayList<Data>();
		final int limit = (int) (sum * pour);
		for (Data d : datas) {
			if (d.value <= limit) {
				newList.add(d);
			} else {
				sum -= d.value;
			}
		}
		datas.clear();
		datas.addAll(newList);
	}

	private final double heightGraph;
	private final List<HtmlColor> colors = Arrays.asList(HtmlColorUtils.getColorIfValid("CadetBlue"),
			HtmlColorUtils.getColorIfValid("Sienna"), HtmlColorUtils.getColorIfValid("Khaki"),
			HtmlColorUtils.getColorIfValid("GreenYellow"), HtmlColorUtils.getColorIfValid("Olive"),
			HtmlColorUtils.GRAY, HtmlColorUtils.getColorIfValid("HotPink"),
			HtmlColorUtils.getColorIfValid("LightBlue"), HtmlColorUtils.getColorIfValid("Orange"),
			HtmlColorUtils.getColorIfValid("OrangeRed"), HtmlColorUtils.getColorIfValid("Chocolate"),
			HtmlColorUtils.getColorIfValid("Coral"));

	public Chart(double height) {
		this.heightGraph = height;
	}

	public void addData(String label, int value) {
		datas.add(new Data(label, value));
		this.sum += value;
	}

	private void drawPart(UGraphic ug, double v1, double v2, HtmlColor color, String title) {
		if (v2 < 180) {
			drawPartInternal(ug, v1, v2, color, title);
		} else {
			drawPartInternal(ug, v1, v2 / 2, color, title);
			drawPartInternal(ug, v1 + v2 / 2, v2 / 2, color, title);
		}
	}

	private void drawPartInternal(UGraphic ug, double v1, double v2, HtmlColor color, String title) {
		// System.err.println("v1=" + v1 + " v2=" + v2 + " v1+v2=" + (v1 + v2));
		ug = ug.apply(new UStroke(0));
		ug = ug.apply(new UChangeBackColor(color)).apply(new UChangeColor(color));
		ug.startUrl(new Url("", title));
		ug.apply(new UStroke(2)).draw(new UEllipse(heightGraph, heightGraph, v1, v2));

		final double cx = heightGraph / 2;
		final double cy = heightGraph / 2;

		final double start = v1 + 90;
		final double extend = v2;

		final double x1 = cx + Math.sin(start * Math.PI / 180.) * heightGraph / 2;
		final double y1 = cy + Math.cos(start * Math.PI / 180.) * heightGraph / 2;
		final double x2 = cx + Math.sin((start + extend) * Math.PI / 180.) * heightGraph / 2;
		final double y2 = cy + Math.cos((start + extend) * Math.PI / 180.) * heightGraph / 2;

		final UPolygon poly = new UPolygon();
		poly.addPoint(heightGraph / 2, heightGraph / 2);
		poly.addPoint(x1, y1);
		poly.addPoint(x2, y2);
		ug.apply(new UStroke(1.5)).draw(poly);
		ug.closeAction();

	}

	private void drawPartComment(UGraphic ug, double v1, double v2, String title) {
		final double cx = heightGraph / 2;
		final double cy = heightGraph / 2;

		final double start = v1 + 90;
		final double extend = v2;

		final double middle = (start + extend / 2) * Math.PI / 180.;
		final double x3 = cx + Math.sin(middle) * (heightGraph / 2 * 4 / 5);
		final double y3 = cy + Math.cos(middle) * (heightGraph / 2 * 4 / 5);
		final double x4 = cx + Math.sin(middle) * (heightGraph / 2 + 20);
		final double y4 = cy + Math.cos(middle) * (heightGraph / 2 + 20);

		ug = ug.apply(new UStroke(2));
		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));

		drawLine(ug, x3, y3, x4, y4);

		final UText text = new UText(title, new FontConfiguration(fontComment, HtmlColorUtils.BLACK));
		final double textWidth = ug.getStringBounder().calculateDimension(fontComment, title).getWidth();
		final double xText;
		if (x4 < cx) {
			xText = x4 - textWidth - 3;
		} else {
			xText = x4 + 3;
		}
		ug.apply(new UTranslate(xText, y4)).draw(text);

	}

	private void drawPartLimit(UGraphic ug, double v1, double v2) {
		ug = ug.apply(new UStroke(1.5));

		final double cx = heightGraph / 2;
		final double cy = heightGraph / 2;

		final double start = v1 + 90;
		final double extend = v2;

		final double x1 = cx + Math.sin(start * Math.PI / 180.) * heightGraph / 2;
		final double y1 = cy + Math.cos(start * Math.PI / 180.) * heightGraph / 2;

		drawLine(ug, cx, cy, x1, y1);

	}

	private void drawLine(UGraphic ug, double x1, double y1, double x2, double y2) {
		ug = ug.apply(new UTranslate(x1, y1));
		ug.draw(new ULine(x2 - x1, y2 - y1));
	}

	private double getMaxTitleWidth(StringBounder stringBounder) {
		double result = 0;
		for (Data data : datas) {
			if (data.value == 0) {
				continue;
			}
			final double textWidth = stringBounder.calculateDimension(fontComment, data.getLabelShort()).getWidth();
			result = Math.max(result, textWidth);
		}
		return result * 1.1 + 20;
	}

	public void printSvg(Writer writer) throws IOException {

		UGraphic ug = new UGraphicSvg(new ColorMapperIdentity(), false, 1.0);
		double wmargin = getMaxTitleWidth(ug.getStringBounder());
		ug = ug.apply(new UTranslate(wmargin, 40));
		ug = ug.apply(new UChangeColor(HtmlColorUtils.WHITE));
		ug = ug.apply(new UChangeBackColor(HtmlColorUtils.WHITE));
		ug.apply(new UTranslate(heightGraph + wmargin, 0)).draw(new URectangle(1, 1));
		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
		ug = ug.apply(new UChangeBackColor(HtmlColorUtils.BLUE));

		double s = 0;
		ug = ug.apply(new UChangeBackColor(HtmlColorUtils.GREEN));
		Iterator<HtmlColor> cols = colors.iterator();
		for (Data data : datas) {
			if (data.value == 0) {
				continue;
			}
			if (cols.hasNext() == false) {
				cols = colors.iterator();
			}
			drawPart(ug, s, data.getAngle(), cols.next(), data.getFullLabel());
			s += data.getAngle();
		}
		s = 0;
		for (Data data : datas) {
			if (data.value == 0) {
				continue;
			}
			drawPartLimit(ug, s, data.getAngle());
			drawPartComment(ug, s, data.getAngle(), data.getLabelShort());
			s += data.getAngle();
		}
		ug = ug.apply(new UStroke(2));
		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
		ug = ug.apply(new UChangeBackColor(null));
		ug.draw(new UEllipse(heightGraph, heightGraph));

		OutputStream os = new WriterOutputStream(writer);
		ug.writeImage(os, null, 96);
		os.flush();

	}

}
