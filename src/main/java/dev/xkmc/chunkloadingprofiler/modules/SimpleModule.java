package dev.xkmc.chunkloadingprofiler.modules;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.io.PrintStream;

public class SimpleModule extends ProfilingModule {

	private final String msg;
	private final int div;

	public SimpleModule(String msg) {
		this.msg = msg;
		this.div = 1;
	}


	public SimpleModule(String msg, int div) {
		this.msg = msg;
		this.div = div;
	}

	@Override
	public void getModCost(Object2LongMap<String> collector) {

	}

	@Override
	public void writeBrief(PrintStream ps) {
		long total = totalTime / 1000000 / (totalCount / div);
		ps.printf("%d %s, taking %d ms per chunk in average.\n", (totalCount / div), msg, total);
	}

	@Override
	public void write(PrintStream ps) {

	}

}
