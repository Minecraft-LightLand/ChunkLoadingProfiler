package dev.xkmc.chunkloadingprofiler.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.xkmc.chunkloadingprofiler.modules.ChunkModules;
import dev.xkmc.chunkloadingprofiler.modules.ChunkProfiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SurfaceSystem.class)
public class SurfaceSystemMixin {

	@Inject(method = "buildSurface", at = @At("HEAD"))
	public void chunkloadingprofiler$buildSurface$start(
			RandomState randomState, BiomeManager biomeManager, Registry<Biome> biomes, boolean useLegacyRandomSource,
			WorldGenerationContext context, ChunkAccess chunk, NoiseChunk noiseChunk, SurfaceRules.RuleSource ruleSource,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.surface.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}


	@WrapOperation(method = "buildSurface", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/BiomeManager;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
	public Holder<Biome> chunkloadingprofiler$buildSurface$getBiome(
			BiomeManager instance, BlockPos flag1, Operation<Holder<Biome>> original,
			@Share("profiler") LocalRef<ChunkProfiler> holder) {
		var ans = original.call(instance, flag1);
		var profiler = holder.get();
		if (profiler == null) return ans;
		profiler.startItem(ans.getRegisteredName());
		return ans;
	}

	@Inject(method = "buildSurface", at = @At("TAIL"))
	public void chunkloadingprofiler$buildSurface$finish(
			RandomState randomState, BiomeManager biomeManager, Registry<Biome> biomes, boolean useLegacyRandomSource,
			WorldGenerationContext context, ChunkAccess chunk, NoiseChunk noiseChunk, SurfaceRules.RuleSource ruleSource,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

}
