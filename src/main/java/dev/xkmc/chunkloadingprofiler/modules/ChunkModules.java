package dev.xkmc.chunkloadingprofiler.modules;

import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
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

	public final ProfilingModule decoration = new ProfilingModule();
	public final Consumer<String> callback;
	public int time;

	private ChunkModules(int time, Consumer<String> callback) {
		this.time = time;
		this.callback = callback;
	}

	public void write(PrintStream ps) {
		decoration.write(ps);
	}

}
