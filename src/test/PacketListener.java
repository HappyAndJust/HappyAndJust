package test;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;

public class PacketListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		injectPlayer(e.getPlayer());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		removePlayer(e.getPlayer());
	}

	private void removePlayer(Player p) {
		Channel channel = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(p.getName());
			return null;
		});
	}

	private void injectPlayer(Player p) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
				if (packet instanceof PacketPlayInUseEntity && !Main.PVP) {
					PacketPlayInUseEntity newPacket = (PacketPlayInUseEntity) packet;
					p.sendMessage(ChatColor.AQUA + "PACKET BLOCKED: " + ChatColor.GREEN + newPacket.toString());
					return;
				}
				super.channelRead(channelHandlerContext, packet);
			}

			@Override
			public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise)
					throws Exception {
				super.write(channelHandlerContext, packet, channelPromise);
			}
		};
		ChannelPipeline pipeline = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel.pipeline();
		pipeline.addBefore("packet_handler", p.getName(), channelDuplexHandler);

	}
}