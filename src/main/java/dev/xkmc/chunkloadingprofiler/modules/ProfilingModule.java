package dev.xkmc.chunkloadingprofiler.modules;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.io.PrintStream;

public abstract class ProfilingModule {

	protected int totalCount = 0;
	protected long totalTime = 0;
	protected final Object2LongMap<String> map = new Object2LongLinkedOpenHashMap<>();

	public ChunkProfiler getProfiler() {
		return new ChunkProfiler(this);
	}

	public synchronized void report(long time, Object2LongMap<String> sub) {
		totalCount++;
		totalTime += time;
		for (var e : sub.object2LongEntrySet()) {
			map.mergeLong(e.getKey(), e.getLongValue(), Long::sum);
		}
	}

	public abstract void getModCost(Object2LongMap<String> collector);

	public abstract void writeBrief(PrintStream ps);

	public abstract void write(PrintStream ps);

}
