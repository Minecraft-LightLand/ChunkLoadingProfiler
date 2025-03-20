package dev.xkmc.chunkloadingprofiler.modules;

import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class ChunkProfiler {

	private final ProfilingModule parent;

	private long startTime;
	private long stopTime;
	private long subTask;

	private @Nullable String itemName;
	private long itemStartTime;

	private LinkedHashMap<String, Long> map = new LinkedHashMap<>();

	public ChunkProfiler(ProfilingModule module) {
		this.parent = module;
	}

	public void start(ChunkPos pos) {
		startTime = System.nanoTime();
	}

	public void stop() {
		stopTime = System.nanoTime();
		parent.report(stopTime - startTime, map);
	}

	public void startItem(String s) {
		if (itemName != null) {
			throw new IllegalStateException("Instance " + itemName + " did not close properly");
		}
		itemName = s;
		itemStartTime = System.nanoTime();
	}

	public void finishItem() {
		if (itemName == null) {
			throw new IllegalStateException("No instance present");
		}
		long stop = System.nanoTime();
		long diff = stop - itemStartTime;
		subTask += diff;
		map.compute(itemName, (k, v) -> (v == null ? 0 : v) + diff);
		itemName = null;
		itemStartTime = 0;
	}

}
