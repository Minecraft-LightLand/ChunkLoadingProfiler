package dev.xkmc.chunkloadingprofiler.modules;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChunkModules {

	private static volatile ChunkModules CURRENT;

	public static synchronized @Nullable ChunkModules getCurrent() {
		return CURRENT;
	}

	public static synchronized void stop() {
		CURRENT = null;
	}

	public static synchronized void start(int time, Consumer<String> callback) {
		CURRENT = new ChunkModules(time, callback);
	}

	public final ProfilingModule init = new SimpleModule("chunk initialized", 2);
	public final ProfilingModule biome = new SimpleModule("chunk biomes filled");
	public final ProfilingModule noise = new SimpleModule("chunk terrains filled");
	public final ProfilingModule surface = new DatapackModule("chunk surfaces built", "Chunk surface building", "biomes for surface building");
	public final ProfilingModule carver = new DatapackModule("chunks carved", "Chunk carving", "carvers");
	public final ProfilingModule decoration = new DecorationModule();
	public final ProfilingModule spawn = new SimpleModule("chunks of mobs spawned");
	public final ProfilingModule finalize = new SimpleModule("chunk finalized");

	public final Consumer<String> callback;
	public int time;

	private ChunkModules(int time, Consumer<String> callback) {
		this.time = time;
		this.callback = callback;
	}

	public void write(PrintStream ps) {
		List<ProfilingModule> modules = List.of(init, biome, noise, surface, carver, decoration, spawn, finalize);
		for (var e : modules) e.writeBrief(ps);
		ps.println("------------------------------");
		writeMods(ps, modules);
		for (var e : modules) e.write(ps);
	}

	private void writeMods(PrintStream ps, List<ProfilingModule> modules) {
		Object2LongMap<String> map = new Object2LongLinkedOpenHashMap<>();
		for (var e : modules) e.getModCost(map);

		List<Map.Entry<String, Long>> mods = new ArrayList<>(map.object2LongEntrySet());
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
	}

}
