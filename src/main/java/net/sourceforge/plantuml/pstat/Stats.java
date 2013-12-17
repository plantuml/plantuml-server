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
package net.sourceforge.plantuml.pstat;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.plantuml.pstat.event.StatEvent;
import net.sourceforge.plantuml.pstat.event.StatEventAdminStart;
import net.sourceforge.plantuml.pstat.event.StatEventAdminStop;
import net.sourceforge.plantuml.pstat.event.StatEventFileIterator;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlCreate;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlLoadImage;
import net.sourceforge.plantuml.pstat.event.StatEventHtmlLoadPage;
import net.sourceforge.plantuml.pstat.event.StatEventImageGeneration;
import net.sourceforge.plantuml.pstat.graph.Curve;
import net.sourceforge.plantuml.pstat.tick.GraphData;
import net.sourceforge.plantuml.pstat.tick.GraphDataLongTerm;
import net.sourceforge.plantuml.pstat.tick.TickDatabase;

public class Stats implements LogCallback {

	public static final boolean USE_STATS = true;

	static class Mem {
		public Mem(StatEvent ev) {
			totalMemory = (int) (ev.getTotalMemory() / 1024L / 1024L);
			freeMemory = (int) (ev.getFreeMemory() / 1024L / 1024L);
			activeCount = ev.getActiveCount();
			time = ev.getStartServerTime();

		}

		final private long time;
		final private int freeMemory;
		final private int totalMemory;
		final private int activeCount;
	}

	private final static Stats singleton = new Stats();

	private final LinkedBlockingDeque<StatEvent> events = new LinkedBlockingDeque<StatEvent>();
	private final LinkedBlockingDeque<Mem> memories = new LinkedBlockingDeque<Mem>();
	private final TickDatabase tickDatabase = USE_STATS ? new TickDatabase() : null;

	public GraphData getGraphData() {
		return tickDatabase == null ? new GraphData() : tickDatabase.getGraphData();
	}

	public GraphDataLongTerm getGraphDataLongTerm() {
		return tickDatabase == null ? new GraphDataLongTerm() : tickDatabase.getGraphDataLongTerm();
	}

	private static final Random rnd = new Random();

	public String getNewToken(long now) {
		final String t = Integer.toString((int) (now / 1000L), 36)
				+ Long.toString(Math.abs(rnd.nextLong()), 36).substring(1);
		return t;
	}

	public static File getSavedFile() {
		return new File(getSavedFileName() + ".pat");
	}

	public static String getSavedFileName() {
		final DateFormat df = new SimpleDateFormat("yyyy_MM", Locale.US);
		return "pdata" + df.format(new Date());
	}

	public static String getPreviousFileName() {
		final DateFormat df = new SimpleDateFormat("yyyy_MM", Locale.US);
		final String cname = df.format(new Date());
		int y = Integer.parseInt(cname.substring(0, 4));
		int m = Integer.parseInt(cname.substring(5, 7));
		m--;
		if (m == 0) {
			y--;
			m = 12;
		}
		return "pdata" + String.format("%04d", y) + "_" + String.format("%02d", m);
	}

	private Stats() {
		final Thread th = new Thread() {
			public void run() {
				fileAccess.lock();
				try {
					reload();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					fileAccess.unlock();
				}
			}
		};
		addEvent(new StatEventAdminStart());
		th.start();
	}

	private final Lock fileAccess = new ReentrantLock();

	private void reload() throws IOException {
		reload(getSavedFile());
		final String cur = getSavedFileName();
		for (char c = 'a'; c < 'z'; c++) {
			reload(cur + c);
		}
		final String prev = getPreviousFileName();
		reload(prev);
		for (char c = 'a'; c < 'z'; c++) {
			reload(prev + c);
		}
		reload("legacy2000");
	}

	private void reload(final String name) throws IOException {
		File file = new File(name + ".pat");
		if (file.exists() == false) {
			file = new File(name + ".pat.gz");
		}
		if (file.exists() == false) {
			file = new File(name + ".zip");
		}
		reload(file);
	}

	private void reload(final File file) throws IOException {
		if (file.exists() == false) {
			return;
		}
		StatEventFileIterator it = null;
		try {
			it = StatEventFileIterator.fromFile(file);
			reload(it);
		} finally {
			if (it != null) {
				it.close();
			}
		}
	}

	private void reload(StatEventFileIterator it) {
		while (it.hasNext()) {
			final StatEvent ev = it.next();
			updateStats(ev);
			Thread.yield();
		}
	}

	public static Stats getInstance() {
		return singleton;
	}

	private void addEvent(final StatEvent ev) {
		updateStats(ev);
		events.add(ev);
		memories.add(new Mem(ev));
		while (memories.size() > 100) {
			final Iterator<Mem> it = memories.iterator();
			it.next();
			it.remove();
		}

		if (fileAccess.tryLock()) {
			try {
				saveEvents();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				fileAccess.unlock();
			}
		}
	}

	public void destroy() {
		System.err.println("CALLING DESTROY");
		addEvent(new StatEventAdminStop());
		if (fileAccess.tryLock()) {
			try {
				saveEvents();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				fileAccess.unlock();
			}
		}
	}

	private void saveEvents() throws IOException {
		final File saveFile = getSavedFile();
		// final long startSave = System.currentTimeMillis();
		DataOutputStream oos = null;
		try {
			oos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile, true), 1024 * 50));
			for (Iterator<StatEvent> it = events.iterator(); it.hasNext();) {
				final StatEvent event = it.next();
				event.save(oos);
				it.remove();
				// nbSave++;
			}
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
	}

	public void logHtmlCreate(String token, long date0, long date1, String encoded, HttpServletRequest request) {
		final StatEvent event = new StatEventHtmlCreate(request, date0, date1, token, encoded);
		addEvent(event);
	}

	public void logHtmlLoadPage(HttpServletRequest request, long date0, String start, String end, String token,
			String width, String height) {
		final long start_ = parseSecureLong(start);
		final long end_ = parseSecureLong(end);
		final int width_ = parseSecureInt(width);
		final int height_ = parseSecureInt(height);
		final StatEventHtmlLoadPage event = new StatEventHtmlLoadPage(request, date0, start_, end_, token, width_,
				height_);
		addEvent(event);

	}

	public void logHtmlLoadImage(HttpServletRequest request, long date0, String start, String end, String token,
			String width, String height) {
		final long start_ = parseSecureLong(start);
		final long end_ = parseSecureLong(end);
		final int width_ = parseSecureInt(width);
		final int height_ = parseSecureInt(height);
		final StatEventHtmlLoadImage event = new StatEventHtmlLoadImage(request, date0, start_, end_, token, width_,
				height_);
		addEvent(event);
	}

	public void logImageGeneration(HttpServletRequest request, long date0, long date1, String type) {
		final StatEventImageGeneration event = new StatEventImageGeneration(request, date0, date1, type);
		addEvent(event);
	}

	private int parseSecureInt(String s) {
		if (s != null && s.matches("\\d+")) {
			return Integer.parseInt(s);
		}
		return 0;
	}

	private long parseSecureLong(String s) {
		if (s != null && s.matches("\\d+")) {
			return Long.parseLong(s);
		}
		return 0;
	}

	private void updateStats(final StatEvent ev) {
		if (USE_STATS) {
			tickDatabase.updateStats(ev);
		}
	}

	public Curve memories(double height, double singleBarWidth, int fontSize) {
		final Curve result = new Curve(height, singleBarWidth, fontSize);
		for (Mem mem : memories) {
			result.addData(new Date(mem.time).toString(), mem.totalMemory, mem.freeMemory);
		}
		return result;
	}

	public Curve threads(double height, double singleBarWidth, int fontSize) {
		final Curve result = new Curve(height, singleBarWidth, fontSize);
		for (Mem mem : memories) {
			result.addData(new Date(mem.time).toString(), mem.activeCount);
		}
		return result;
	}
}
