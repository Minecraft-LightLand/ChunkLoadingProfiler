package dev.xkmc.chunkloadingprofiler.modules;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DecorationModule extends ProfilingModule {

	@Override
	public void getModCost(Object2LongMap<String> collector) {
		for (var ent : map.object2LongEntrySet()) {
			String str = ent.getKey();
			if (str.startsWith("ResourceKey[")) {
				String actual = str.substring(12, str.length() - 1);
				String[] ids = actual.split(" / ");
				if (ids.length == 2) {
					String[] rl = ids[1].split(":");
					if (rl.length == 2) {
						collector.mergeLong(rl[0], ent.getLongValue(), Long::sum);
					}
				}
			}
		}
	}

	public void writeBrief(PrintStream ps) {
		long total = totalTime / 1000000 / totalCount;
		ps.printf("%d chunks decorated, taking %d ms per chunk in average.\n", totalCount, total);
	}

	public void write(PrintStream ps) {
		ps.println("------------------------------");
		if (totalCount == 0) {
			ps.println("No chunks decorated");
			return;
		}
		ps.printf("Chunk decoration takes %d ms for %d chunks.\n", totalTime / 1000000, totalCount);
		List<Pair<String, Long>> structures = new ArrayList<>(), features = new ArrayList<>();

		for (var ent : map.object2LongEntrySet()) {
			String str = ent.getKey();
			if (str.startsWith("ResourceKey[")) {
				String actual = str.substring(12, str.length() - 1);
				String[] ids = actual.split(" / ");
				if (ids.length == 2) {
					if (ids[0].endsWith("structure")) {
						structures.add(Pair.of(ids[1], ent.getLongValue()));
					}
					if (ids[0].endsWith("feature")) {
						features.add(Pair.of(ids[1], ent.getLongValue()));
					}
				}
			}
		}

		structures.sort(Comparator.comparing(e -> -e.getSecond()));
		features.sort(Comparator.comparing(e -> -e.getSecond()));

		{
			int n = Math.min(10, structures.size());
			ps.printf("Top %d structures:\n", n);
			for (int i = 0; i < n; i++) {
				var e = structures.get(i);
				long time = e.getSecond() / 1000000;
				if (time <= 0) continue;
				ps.printf("- %s: %d ms\n", e.getFirst(), time);
			}
		}
		ps.println();
		{
			int n = Math.min(10, features.size());
			ps.printf("Top %d features:\n", n);
			for (int i = 0; i < n; i++) {
				var e = features.get(i);
				long time = e.getSecond() / 1000000;
				if (time <= 0) continue;
				ps.printf("- %s: %d ms\n", e.getFirst(), time);
			}
		}
	}


}
