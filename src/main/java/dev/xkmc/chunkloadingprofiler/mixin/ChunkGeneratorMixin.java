package dev.xkmc.chunkloadingprofiler.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.xkmc.chunkloadingprofiler.modules.ChunkModules;
import dev.xkmc.chunkloadingprofiler.modules.ChunkProfiler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

	@Inject(method = "applyBiomeDecoration", at = @At("HEAD"))
	public void chunkloadingprofiler$applyBiomeDecoration$start(
			WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci,
			@Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.decoration.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}


	@WrapOperation(method = "applyBiomeDecoration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setCurrentlyGenerating(Ljava/util/function/Supplier;)V"))
	public void chunkloadingprofiler$applyBiomeDecoration$setName(
			WorldGenLevel instance, Supplier<String> currentlyGenerating, Operation<Void> original,
			@Share("profiler") LocalRef<ChunkProfiler> holder) {
		original.call(instance, currentlyGenerating);
		if (currentlyGenerating == null) return;
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.startItem(currentlyGenerating.get());
	}

	@WrapOperation(method = "applyBiomeDecoration", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
	public void chunkloadingprofiler$applyBiomeDecoration$structure(
			List instance, Consumer consumer, Operation<Void> original,
			@Share("profiler") LocalRef<ChunkProfiler> holder) {
		original.call(instance, consumer);
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.finishItem();
	}

	@WrapOperation(method = "applyBiomeDecoration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/placement/PlacedFeature;placeWithBiomeCheck(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"))
	public boolean chunkloadingprofiler$applyBiomeDecoration$biome(
			PlacedFeature instance, WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos, Operation<Boolean> original,
			@Share("profiler") LocalRef<ChunkProfiler> holder) {
		original.call(instance, level, generator, random, pos);
		var profiler = holder.get();
		if (profiler == null) return false;
		profiler.finishItem();
		return false;
	}

	@Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
	public void chunkloadingprofiler$applyBiomeDecoration$finish(
			WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci,
			@Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

}
