package dev.xkmc.chunkloadingprofiler.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.xkmc.chunkloadingprofiler.modules.ChunkModules;
import dev.xkmc.chunkloadingprofiler.modules.ChunkProfiler;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin {

	@Shadow
	public abstract ChunkPos getPos();

	@Inject(method = "fillBiomesFromNoise", at = @At("HEAD"))
	public void chunkloadingprofiler$fillBiomesFromNoise$start(
			BiomeResolver resolver, Climate.Sampler sampler,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.biome.getProfiler();
		profiler.start(getPos());
		holder.set(profiler);
	}

	@Inject(method = "fillBiomesFromNoise", at = @At("TAIL"))
	public void chunkloadingprofiler$fillBiomesFromNoise$finish(
			BiomeResolver resolver, Climate.Sampler sampler,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

}
