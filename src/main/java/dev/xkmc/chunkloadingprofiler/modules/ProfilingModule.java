package dev.xkmc.chunkloadingprofiler.modules;

import com.mojang.datafixers.util.Pair;

import java.io.PrintStream;
import java.util.*;

public class ProfilingModule {

	private int totalCount = 0;
	private long totalTime = 0;
	private final LinkedHashMap<String, Long> map = new LinkedHashMap<>();

	public synchronized ChunkProfiler getProfiler() {
		return new ChunkProfiler(this);
	}

	public synchronized void report(long time, LinkedHashMap<String, Long> sub) {
		totalCount++;
		totalTime += time;
		for (var e : sub.entrySet()) {
			map.compute(e.getKey(), (k, v) -> (v == null ? 0 : v) + e.getValue());
		}
	}

	public void write(PrintStream ps) {
		if (totalCount == 0) {
			ps.println("No chunks generated");
			return;
		}
		long total = totalTime / 1000000 / totalCount;

		ps.printf("%d Chunk generated, taking %d ms per chunk in average.\n", totalCount, total);

		List<Pair<String, Long>> structures = new ArrayList<>(), features = new ArrayList<>();
		Map<String, Long> modMap = new LinkedHashMap<>();

		for (var ent : map.entrySet()) {
			String str = ent.getKey();
			if (str.startsWith("ResourceKey[")) {
				String actual = str.substring(12, str.length() - 1);
				String[] ids = actual.split(" / ");
				if (ids.length == 2) {
					if (ids[0].endsWith("structure")) {
						structures.add(Pair.of(ids[1], ent.getValue()));
					}
					if (ids[0].endsWith("feature")) {
						features.add(Pair.of(ids[1], ent.getValue()));
					}
					String[] rl = ids[1].split(":");
					if (rl.length == 2) {
						modMap.compute(rl[0], (k, v) -> (v == null ? 0 : v) + ent.getValue());
					}
				}
			}
		}
		List<Map.Entry<String, Long>> mods = new ArrayList<>(modMap.entrySet());

		structures.sort(Comparator.comparing(e -> -e.getSecond()));
		features.sort(Comparator.comparing(e -> -e.getSecond()));
		mods.sort(Comparator.comparing(e -> -e.getValue()));

		{
			int n = Math.min(10, mods.size());
			ps.printf("Top %d mods:\n", n);
			for (int i = 0; i < n; i++) {
				var e = mods.get(i);
				long time = e.getValue() / 1000000;
				if (time <= 0) continue;
				ps.printf("- %s: %d ms\n", e.getKey(), time);
			}
		}
		ps.println("------------------------------");
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
		ps.println("------------------------------");
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
