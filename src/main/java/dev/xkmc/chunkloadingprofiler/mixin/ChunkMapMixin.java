package dev.xkmc.chunkloadingprofiler.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.xkmc.chunkloadingprofiler.modules.ChunkModules;
import dev.xkmc.chunkloadingprofiler.modules.ChunkProfiler;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

	@Inject(method = "lambda$protoChunkToFullChunk$34", at = @At("HEAD"))
	private void chunkloadingprofiler$full$start(
			ChunkHolder p_214855_, ChunkAccess chunk, CallbackInfoReturnable<ChunkAccess> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.finalize.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "lambda$protoChunkToFullChunk$34", at = @At("TAIL"))
	private void chunkloadingprofiler$full$finish(
			ChunkHolder p_214855_, ChunkAccess chunk, CallbackInfoReturnable<ChunkAccess> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

}
