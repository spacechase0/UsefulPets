package com.spacechase0.minecraft.usefulpets.network;

public class PacketCodec extends com.spacechase0.minecraft.spacecore.network.PacketCodec
{
	public PacketCodec()
	{
		addPacket( new ChangeSkillPacket() );
		addPacket( new ChangeTexturePacket() );
		addPacket( new TrackingDataPacket() );
	}

	protected static final byte ID_CHANGE_SKILL = 0;
	protected static final byte ID_CHANGE_TEXTURE = 1;
	protected static final byte ID_TRACKING_DATA = 2;
}
