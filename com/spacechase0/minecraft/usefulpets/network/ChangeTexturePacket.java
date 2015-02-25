package com.spacechase0.minecraft.usefulpets.network;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import com.spacechase0.minecraft.spacecore.network.Packet;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

import cpw.mods.fml.common.network.ByteBufUtils;

public class ChangeTexturePacket extends Packet
{
	public ChangeTexturePacket()
	{
	}
	
	public ChangeTexturePacket( int theEntity, String theTex )
	{
		entity = theEntity;
		tex = theTex;
	}
	
	@Override
	public byte getId()
	{
		return PacketCodec.ID_CHANGE_TEXTURE;
	}
	
	@Override
	public void processServer( EntityPlayerMP player )
	{
		Entity entity = player.worldObj.getEntityByID( this.entity );
		if ( entity == null || !( entity instanceof PetEntity ) )
		{
			return;
		}
		PetEntity pet = ( PetEntity ) entity;
		
		if ( !UUID.fromString( pet.func_152113_b() ).equals( player.getUniqueID() ) )
		{
			return;
		}
		
		pet.setTexture( tex );
	}
	
	@Override
	public void write( ByteBuf buffer )
	{
		buffer.writeInt( entity );
		
		ByteBufUtils.writeUTF8String( buffer, tex );
	}

	@Override
	public void read( ByteBuf buffer )
	{
		entity = buffer.readInt();
		tex = ByteBufUtils.readUTF8String( buffer );
	}
	
	public int entity;
	public String tex;
}
