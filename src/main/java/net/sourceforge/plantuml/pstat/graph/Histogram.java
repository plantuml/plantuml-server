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
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.pstat.WriterOutputStream;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.svg.UGraphicSvg;

public class Histogram {

	static class Data {
		final String label;
		final String title;
		final int value;

		Data(String label, String title, int value) {
			this.label = label;
			this.title = title;
			this.value = value;
		}
	}

	private final HtmlColor backColor = HtmlColorUtils.getColorIfValid("#AFE0D0");
	private final HtmlColor lineColor = HtmlColorUtils.getColorIfValid("DimGray");
	private final UFont fontLegend;
	private final List<Data> datas = new ArrayList<Data>();
	private int max = 1;
	private final double heightGraph;
	private final double singleBarWidth;

	public Histogram(double height, double singleBarWidth, int fontSize) {
		this.heightGraph = height;
		this.singleBarWidth = singleBarWidth;
		this.fontLegend = new UFont("", Font.BOLD, fontSize);
	}

	public void addData(String label, int value, String title) {
		datas.add(new Data(label, title, value));
		max = Math.max(max, value);
	}

	public void printSvg(Writer writer) throws IOException {
		UGraphic ug = new UGraphicSvg(new ColorMapperIdentity(), false, 1.0);
		ug = ug.apply(new UChangeColor(null));
		ug = ug.apply(new UChangeBackColor(backColor));

		final double coef = heightGraph / max;

		int step = getRound(max / 10.0);
		if (step == 0) {
			step = 1;
		}
		final double margeY1 = 10;
		printScale(ug.apply(new UTranslate(0, heightGraph + margeY1)), step, coef, datas.size() * singleBarWidth + 30);

		final int marginX = 8 + ("" + max).length() * 8;
		UGraphic ug2 = ug.apply(new UTranslate(marginX, heightGraph + margeY1));
		final FontConfiguration fontConfiguration = new FontConfiguration(fontLegend, HtmlColorUtils.BLACK);
		for (Data data : datas) {
			final double v = data.value * coef;
			ug2.startUrl(new Url("", data.title));
			ug2.apply(new UTranslate(0, -v)).draw(new URectangle(singleBarWidth, v));
			ug2.closeAction();
			final double textHeight = ug.getStringBounder().calculateDimension(fontLegend, data.label).getHeight();
			final double textWidth = ug.getStringBounder().calculateDimension(fontLegend, data.label).getWidth();
			final double xtext = (singleBarWidth - textWidth) / 2;
			ug2.apply(new UTranslate(xtext, textHeight - 2)).draw(new UText(data.label, fontConfiguration));
			ug2 = ug2.apply(new UTranslate(singleBarWidth, 0));
		}

		ug = ug.apply(new UChangeColor(lineColor)).apply(new UStroke(2)).apply(new UChangeBackColor(null));
		ug2 = ug.apply(new UTranslate(marginX, heightGraph + margeY1));
		double lastV = 0;
		for (Data data : datas) {
			final double v = data.value * coef;
			drawLine(ug2, 0, -lastV, 0, -v);
			drawLine(ug2, 0, -v, singleBarWidth, -v);
			lastV = v;
			ug2 = ug2.apply(new UTranslate(singleBarWidth, 0));
		}
		drawLine(ug2, 0, -lastV, 0, 0);

		OutputStream os = new WriterOutputStream(writer);
		ug.writeImage(os, null, 96);
		os.flush();

	}

	private void drawLine(UGraphic ug, double x1, double y1, double x2, double y2) {
		ug = ug.apply(new UTranslate(x1, y1));
		ug.draw(new ULine(x2 - x1, y2 - y1));
	}

	private int getRound(double value) {
		final int step = (int) Math.ceil(value / 5.0);
		return NiceNumber.getNicer(step * 5);
	}

	private void printScale(UGraphic ug, int step, double coef, double totalWidth) {
		final FontConfiguration fontConfiguration = new FontConfiguration(fontLegend, HtmlColorUtils.BLACK);
		final ULine hline = new ULine(totalWidth, 0);
		ug = ug.apply(new UChangeColor(HtmlColorUtils.GRAY));
		for (int i = 0; i <= max; i += step) {
			final double v = i * coef;
			String label = "" + i;
			final UText text = new UText(label, fontConfiguration);
			ug.apply(new UTranslate(0, -v)).draw(text);
			ug.apply(new UTranslate(10, -v)).apply(new UStroke(2, 2, 1)).draw(hline);
		}

	}
}
