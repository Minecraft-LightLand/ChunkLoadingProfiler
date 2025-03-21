package dev.xkmc.chunkloadingprofiler.modules;

import dev.xkmc.chunkloadingprofiler.init.ChunkLoadingProfiler;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class ReportGenerator {

	public static String generate(ChunkModules modules) {
		String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String log = "logs/profiler/chunk-" + time + ".txt";
		Path path = FMLPaths.GAMEDIR.get().resolve(log);
		write(path, modules::write);
		return log;
	}

	private static void write(Path path, Consumer<PrintStream> cons) {
		PrintStream stream = null;
		try {
			stream = getStream(path);
			cons.accept(stream);
		} catch (Exception e) {
			ChunkLoadingProfiler.LOGGER.throwing(Level.ERROR, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					ChunkLoadingProfiler.LOGGER.throwing(Level.FATAL, e);
				}
			}
		}
	}

	private static PrintStream getStream(Path path) throws IOException {
		File file = path.toFile();
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				if (!file.getParentFile().mkdirs()) {
					throw new IOException("failed to create directory " + file.getParentFile());
				}
			}
			if (!file.createNewFile()) {
				throw new IOException("failed to create file " + file);
			}
		}
		return new PrintStream(file);
	}

}
