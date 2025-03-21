package dev.xkmc.chunkloadingprofiler.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.xkmc.chunkloadingprofiler.modules.ChunkModules;
import dev.xkmc.chunkloadingprofiler.modules.ChunkProfiler;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkStatusTasks.class)
public class ChunkStatusTaskMixin {

	@Inject(method = "generateStructureStarts", at = @At("HEAD"))
	private static void chunkloadingprofiler$generateStructureStarts$start(
			WorldGenContext ctx, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk,
			CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.init.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "generateStructureStarts", at = @At("TAIL"))
	private static void chunkloadingprofiler$generateStructureStarts$finish(
			WorldGenContext ctx, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk,
			CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

	@Inject(method = "generateStructureReferences", at = @At("HEAD"))
	private static void chunkloadingprofiler$generateStructureReferences$start(
			WorldGenContext ctx, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk,
			CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.init.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "generateStructureReferences", at = @At("TAIL"))
	private static void chunkloadingprofiler$generateStructureReferences$finish(
			WorldGenContext ctx, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk,
			CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

	@Inject(method = "lambda$full$1", at = @At("HEAD"))
	private static void chunkloadingprofiler$full$start(
			ServerLevel serverlevel, ProtoChunk protochunk, LevelChunk chunk, CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.finalize.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "lambda$full$1", at = @At("TAIL"))
	private static void chunkloadingprofiler$full$finish(
			ServerLevel serverlevel, ProtoChunk protochunk, LevelChunk chunk, CallbackInfo ci, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

	@Inject(method = "generateSpawn", at = @At("HEAD"))
	private static void chunkloadingprofiler$generateSpawn$start(
			WorldGenContext ctx, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk,
			CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		ChunkModules current = ChunkModules.getCurrent();
		if (current == null) return;
		var profiler = current.spawn.getProfiler();
		profiler.start(chunk.getPos());
		holder.set(profiler);
	}

	@Inject(method = "generateSpawn", at = @At("TAIL"))
	private static void chunkloadingprofiler$generateSpawn$finish(
			WorldGenContext ctx, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk,
			CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Share("profiler") LocalRef<ChunkProfiler> holder) {
		var profiler = holder.get();
		if (profiler == null) return;
		profiler.stop();
	}

}
