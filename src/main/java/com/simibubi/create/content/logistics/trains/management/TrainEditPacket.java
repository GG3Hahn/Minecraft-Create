package com.simibubi.create.content.logistics.trains.management;

import java.util.UUID;
import java.util.function.Supplier;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.entity.TrainIconType;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

public class TrainEditPacket extends SimplePacketBase {

	private String name;
	private UUID id;
	private ResourceLocation iconType;
	private boolean heldForAssembly;

	public TrainEditPacket(UUID id, String name, boolean heldForAssembly, ResourceLocation iconType) {
		this.name = name;
		this.id = id;
		this.heldForAssembly = heldForAssembly;
		this.iconType = iconType;
	}

	public TrainEditPacket(FriendlyByteBuf buffer) {
		id = buffer.readUUID();
		name = buffer.readUtf(256);
		iconType = buffer.readResourceLocation();
		heldForAssembly = buffer.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(id);
		buffer.writeUtf(name);
		buffer.writeResourceLocation(iconType);
		buffer.writeBoolean(heldForAssembly);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get()
			.enqueueWork(() -> {
				Train train = Create.RAILWAYS.trains.get(id);
				if (train == null)
					return;
				if (!name.isBlank())
					train.name = new TextComponent(name);
				train.icon = TrainIconType.byId(iconType);
				train.heldForAssembly = heldForAssembly;
			});
		context.get()
			.setPacketHandled(true);
	}

}
