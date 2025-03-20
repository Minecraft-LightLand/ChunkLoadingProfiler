package dev.xkmc.chunkloadingprofiler.modules;

import dev.xkmc.chunkloadingprofiler.init.ChunkLoadingProfiler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;

public class ProfilerRunner {

	public static void startProfiling(int time, CommandSourceStack source) {
		var current = ChunkModules.getCurrent();
		if (current != null) {
			ChunkModules.stop();
			String str = ReportGenerator.generate(current);
			ChunkLoadingProfiler.LOGGER.error("Chunk profiler already exists. Terminating. Saved at {}", str);
		}
		ChunkModules.start(time, e -> source.sendSuccess(() -> Component.literal(e), true));
	}

	public static void onServerTick() {
		var current = ChunkModules.getCurrent();
		if (current != null && current.time > 0) {
			current.time--;
			if (current.time == 0) {
				ChunkModules.stop();
				String str = ReportGenerator.generate(current);
				current.callback.accept("Profiling Complete, saved at" + str);
			}
		}
	}

}
