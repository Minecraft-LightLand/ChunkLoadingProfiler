package dev.xkmc.chunkloadingprofiler.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.xkmc.chunkloadingprofiler.modules.ChunkModules;
import dev.xkmc.chunkloadingprofiler.modules.ChunkProfiler;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

	@Inject(method = "doFill", at = @At("HEAD"))
	public void chunkloadingprofiler$doFill$start(
			Blender blender, StructureManager structureManager, RandomState random, ChunkAccess chunk, int minCellY, int cellCountY,
			CallbackInfoReturnable<ChunkAccess> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.noise.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "doFill", at = @At("TAIL"))
	public void chunkloadingprofiler$doFill$finish(
			Blender blender, StructureManager structureManager, RandomState random, ChunkAccess chunk, int minCellY, int cellCountY,
			CallbackInfoReturnable<ChunkAccess> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

	@Inject(method = "applyCarvers", at = @At("HEAD"))
	public void chunkloadingprofiler$applyCarvers$start(
			WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager,
			StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.carver.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}


	@WrapOperation(method = "applyCarvers", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;"))
	public <T> T chunkloadingprofiler$applyCarvers$item(
			Holder<T> instance, Operation<T> original, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		T ans = original.call(instance);
		var profiler = holder.get();
		if (profiler == null) return ans;
		if (ans instanceof ConfiguredWorldCarver<?>)
			profiler.startItem(instance.getRegisteredName());
		return ans;
	}

	@Inject(method = "applyCarvers", at = @At("TAIL"))
	public void chunkloadingprofiler$applyCarvers$finish(
			WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager,
			StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}


}
