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
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.svg.UGraphicSvg;

public class Curve {

	static class Data {
		final String label;
		final int[] value;

		Data(String label, int[] value) {
			this.label = label;
			this.value = value;
		}
	}

	private final HtmlColor backColor = HtmlColorUtils.getColorIfValid("#AFE0D0");
	// private final HtmlColor lineColor = HtmlColorUtils.getColorIfValid("DimGray");
	private final UFont fontLegend;
	private final List<Data> datas = new ArrayList<Data>();
	private int max;
	private final double heightGraph;
	private final double singleBarWidth;

	public Curve(double height, double singleBarWidth, int fontSize) {
		this.heightGraph = height;
		this.singleBarWidth = singleBarWidth;
		this.fontLegend = new UFont("", Font.BOLD, fontSize);
	}

	public void addData(String label, int... value) {
		datas.add(new Data(label, value));
		for (Data data : datas) {
			for (int v : data.value) {
				max = Math.max(max, v);
			}
		}
	}

	public void printSvg(Writer writer) throws IOException {
		UGraphic ug = new UGraphicSvg(new ColorMapperIdentity(), false, 1.0);
		ug = ug.apply(new UChangeColor(backColor));
		ug = ug.apply(new UChangeBackColor(backColor));

		final double coef = heightGraph / max;

		int step = getRound(max / 10.0);
		if (step == 0) {
			step = 1;
		}
		final double margeY1 = 10;

		traceInfo(ug, coef, margeY1);
		printScale(ug.apply(new UTranslate(0, heightGraph + margeY1)), step, coef, datas.size() * singleBarWidth + 30);

		for (int i = 0; i < datas.get(0).value.length; i++) {
			traceCurve(ug, coef, margeY1, i);
		}

		OutputStream os = new WriterOutputStream(writer);
		ug.writeImage(os, null, 96);
		os.flush();

	}

	private void traceInfo(UGraphic ug, final double coef, final double margeY1) {
		ug = ug.apply(new UTranslate(20, heightGraph + margeY1));
		ug = ug.apply(new UChangeColor(HtmlColorUtils.WHITE)).apply(new UChangeBackColor(HtmlColorUtils.WHITE));
		for (Data data : datas) {
			ug.startUrl(new Url("", data.label));
			ug.apply(new UTranslate(-3, -heightGraph - margeY1)).draw(
					new URectangle(singleBarWidth, heightGraph + margeY1));
			ug.closeAction();
			ug = ug.apply(new UTranslate(singleBarWidth, 0));
		}
	}

	private void traceCurve(UGraphic ug, final double coef, final double margeY1, int i) {
		ug = ug.apply(new UTranslate(20, heightGraph + margeY1));
		Data lastData = null;
		for (Data data : datas) {
			ug.apply(new UTranslate(-3, -(data.value[i] * coef) - 3)).draw(new UEllipse(5, 5));
			if (lastData != null) {
				final ULine line = new ULine(-singleBarWidth, (data.value[i] - lastData.value[i]) * coef);
				ug.apply(new UTranslate(0, -(data.value[i] * coef))).apply(new UStroke(2)).draw(line);
			}
			ug = ug.apply(new UTranslate(singleBarWidth, 0));
			lastData = data;
		}
	}

	private int getRound(double value) {
		final int step = (int) Math.ceil(value / 5.0);
		return step * 5;
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
