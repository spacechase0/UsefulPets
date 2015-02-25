package com.spacechase0.minecraft.usefulpets.network;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import com.spacechase0.minecraft.spacecore.network.Packet;
import com.spacechase0.minecraft.spacecore.util.Vector3i;
import com.spacechase0.minecraft.usefulpets.PetTrackingDataHandler.PetData;
import com.spacechase0.minecraft.usefulpets.UsefulPets;

import cpw.mods.fml.common.network.ByteBufUtils;

public class TrackingDataPacket extends Packet
{
	public TrackingDataPacket()
	{
	}
	
	public TrackingDataPacket( Map< UUID, PetData > theData )
	{
		data = theData;
	}
	
	@Override
	public byte getId()
	{
		return PacketCodec.ID_TRACKING_DATA;
	}

	@Override
	public void processClient( EntityPlayer player )
	{
		UsefulPets.proxy.setTrackingData( data );
	}
	
	@Override
	public void write( ByteBuf buffer )
	{
		buffer.writeInt( data.size() );
		
		Iterator< Entry< UUID, PetData > > it = data.entrySet().iterator();
		while ( it.hasNext() )
		{
			Entry< UUID, PetData > entry = it.next();
			
			UUID id = entry.getKey();
			PetData pet = entry.getValue();
			
			buffer.writeLong( id.getMostSignificantBits() );
			buffer.writeLong( id.getLeastSignificantBits() );
			
			buffer.writeInt( pet.dim );
			buffer.writeInt( pet.pos.x );
			buffer.writeInt( pet.pos.y );
			buffer.writeInt( pet.pos.z );

			ByteBufUtils.writeUTF8String( buffer, pet.name );
			ByteBufUtils.writeUTF8String( buffer, pet.tex );
			buffer.writeInt( pet.level );
		}
	}

	@Override
	public void read( ByteBuf buffer )
	{
		data.clear();
		
		int count = buffer.readInt();
		for ( int i = 0; i < count; ++i )
		{
			UUID id = new UUID( buffer.readLong(), buffer.readLong() );
			
			int dim = buffer.readInt();
			Vector3i pos = new Vector3i( buffer.readInt(), buffer.readInt(), buffer.readInt() );
			
			
        	String name = ByteBufUtils.readUTF8String( buffer );
        	String tex = ByteBufUtils.readUTF8String( buffer );;
        	int level = buffer.readInt();
        	
        	PetData pet = new PetData();
        	pet.dim = dim;
        	pet.pos = pos;
        	pet.name = name;
        	pet.tex = tex;
        	pet.level = level;
        	
        	data.put( id, pet );
		}
	}
	
	public Map< UUID, PetData > data = new HashMap< UUID, PetData >();
}
