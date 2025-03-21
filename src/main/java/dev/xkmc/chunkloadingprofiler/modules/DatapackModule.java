package dev.xkmc.chunkloadingprofiler.modules;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DatapackModule extends ProfilingModule {

	private final String msg, work, top;

	public DatapackModule(String msg, String work, String top) {
		this.msg = msg;
		this.work = work;
		this.top = top;
	}

	@Override
	public void getModCost(Object2LongMap<String> collector) {
		for (var ent : map.object2LongEntrySet()) {
			String str = ent.getKey();
			String[] rl = str.split(":");
			if (rl.length == 2) {
				collector.mergeLong(rl[0], ent.getLongValue(), Long::sum);
			}
		}
	}

	@Override
	public void writeBrief(PrintStream ps) {
		long total = totalTime / 1000000 / totalCount;
		ps.printf("%d %s, taking %d ms per chunk in average.\n", totalCount, msg, total);
	}

	@Override
	public void write(PrintStream ps) {
		ps.println("------------------------------");
		if (totalCount == 0) {
			ps.printf("No %s\n", msg);
			return;
		}
		ps.printf("%s takes %d ms for %d chunks.\n", work, totalTime / 1000000, totalCount);
		List<Map.Entry<String, Long>> all = new ArrayList<>(map.object2LongEntrySet());
		all.sort(Comparator.comparing(e -> -e.getValue()));
		{
			int n = Math.min(10, all.size());
			ps.printf("Top %d %s:\n", n, top);
			for (int i = 0; i < n; i++) {
				var e = all.get(i);
				long time = e.getValue() / 1000000;
				if (time <= 0) continue;
				ps.printf("- %s: %d ms\n", e.getKey(), time);
			}
		}
	}

}
