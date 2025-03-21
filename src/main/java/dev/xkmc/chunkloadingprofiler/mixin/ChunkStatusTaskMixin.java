package dev.xkmc.chunkloadingprofiler.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.xkmc.chunkloadingprofiler.modules.ChunkModules;
import dev.xkmc.chunkloadingprofiler.modules.ChunkProfiler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public class ChunkStatusTaskMixin {

	@Inject(method = "lambda$static$2", at = @At("HEAD"))
	private static void chunkloadingprofiler$generateStructureStarts$start(
			ChunkStatus p_289514_, Executor p_289515_, ServerLevel p_289516_, ChunkGenerator p_289517_, StructureTemplateManager p_289518_,
			ThreadedLevelLightEngine p_289519_, Function p_289520_, List p_289521_, ChunkAccess chunk,
			CallbackInfoReturnable<CompletableFuture> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.init.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "lambda$static$2", at = @At("TAIL"))
	private static void chunkloadingprofiler$generateStructureStarts$finish(
			ChunkStatus p_289514_, Executor p_289515_, ServerLevel p_289516_, ChunkGenerator p_289517_, StructureTemplateManager p_289518_,
			ThreadedLevelLightEngine p_289519_, Function p_289520_, List p_289521_, ChunkAccess chunk,
			CallbackInfoReturnable<CompletableFuture> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

	@Inject(method = "lambda$static$4", at = @At("HEAD"))
	private static void chunkloadingprofiler$generateStructureReferences$start(
			ChunkStatus p_196843_, ServerLevel p_196844_, ChunkGenerator p_196845_, List p_196846_, ChunkAccess chunk,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.init.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "lambda$static$4", at = @At("TAIL"))
	private static void chunkloadingprofiler$generateStructureReferences$finish(
			ChunkStatus p_196843_, ServerLevel p_196844_, ChunkGenerator p_196845_, List p_196846_, ChunkAccess chunk,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

	@Inject(method = "lambda$static$16", at = @At("HEAD"))
	private static void chunkloadingprofiler$generateSpawn$start(
			ChunkStatus p_196758_, ServerLevel p_196759_, ChunkGenerator p_196760_, List p_196761_, ChunkAccess chunk,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.spawn.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "lambda$static$16", at = @At("TAIL"))
	private static void chunkloadingprofiler$generateSpawn$finish(
			ChunkStatus p_196758_, ServerLevel p_196759_, ChunkGenerator p_196760_, List p_196761_, ChunkAccess chunk,
			CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

}
