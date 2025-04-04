package dev.xkmc.chunkloadingprofiler.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.xkmc.chunkloadingprofiler.modules.ProfilerRunner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = ChunkLoadingProfiler.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CLPCommands {

	@SubscribeEvent
	public static void register(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("profilechunk");
		base.requires(e -> e.hasPermission(2))
				.then(Commands.argument("time", IntegerArgumentType.integer(1, 20 * 60 * 60 * 24))
						.executes(ctx -> onStart(ctx)));
		event.getDispatcher().register(base);
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		ProfilerRunner.onServerTick();
	}

	protected static int onStart(CommandContext<CommandSourceStack> ctx) {
		int time = ctx.getArgument("time", Integer.class);
		ProfilerRunner.startProfiling(time, ctx.getSource());
		int sec = time / 20;
		int min = sec / 60;
		int hrs = min / 60;
		String str = String.format("%02d:%02d:%02d", hrs % 24, min % 60, sec % 60);
		ctx.getSource().sendSuccess(() -> Component.literal("Start profiling chunk with time " + str), true);
		return 1;
	}

}
