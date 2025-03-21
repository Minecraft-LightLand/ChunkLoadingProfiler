package dev.xkmc.chunkloadingprofiler.init;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChunkLoadingProfiler.MODID)
public class ChunkLoadingProfiler {

	public static final String MODID = "chunkloadingprofiler";
	public static final Logger LOGGER = LogManager.getLogger();

	public ChunkLoadingProfiler() {
	}

}
