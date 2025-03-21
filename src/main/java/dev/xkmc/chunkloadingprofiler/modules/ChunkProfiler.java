package dev.xkmc.chunkloadingprofiler.modules;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class ChunkProfiler {

	private final ProfilingModule parent;

	private long startTime, accumulated;
	private @Nullable String itemName;
	private long itemStartTime;

	private final Object2LongMap<String> map = new Object2LongLinkedOpenHashMap<>();

	public ChunkProfiler(ProfilingModule module) {
		this.parent = module;
	}

	public void start(ChunkPos pos) {
		startTime = System.nanoTime();
	}

	public void stop() {
		if (itemName != null) {
			finishItem();
		}
		long stopTime = System.nanoTime();
		parent.report(startTime > 0 ? stopTime - startTime : accumulated, map);
	}

	public void startItem(String s) {
		if (itemName != null) {
			finishItem();
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
		accumulated += diff;
		map.mergeLong(itemName, diff, Long::sum);
		itemName = null;
		itemStartTime = 0;
	}

}
