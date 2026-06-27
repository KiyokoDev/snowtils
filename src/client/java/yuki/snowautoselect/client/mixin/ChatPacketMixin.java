package yuki.snowautoselect.client.mixin;

import yuki.snowautoselect.client.SkyblockDialogueHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ChatPacketMixin {
	@Inject(method = "handleSystemChat", at = @At("HEAD"))
	private void onHandleSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
		SkyblockDialogueHandler.handleDialogue(packet.content());
	}

	@Inject(method = "handleDisguisedChat", at = @At("HEAD"))
	private void onHandleDisguisedChat(ClientboundDisguisedChatPacket packet, CallbackInfo ci) {
		SkyblockDialogueHandler.handleDialogue(packet.message());
	}
}
