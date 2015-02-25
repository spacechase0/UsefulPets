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

public class ChangeSkillPacket extends Packet
{
	public ChangeSkillPacket()
	{
	}
	
	public ChangeSkillPacket( int theEntity, int theAction, int theSkill )
	{
		entity = theEntity;
		action = theAction;
		skill = theSkill;
	}
	
	@Override
	public byte getId()
	{
		return PacketCodec.ID_CHANGE_SKILL;
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
		
		switch ( action )
		{
			case ACTION_ADD: pet.addSkill( skill ); break;
			case ACTION_REMOVE: pet.removeSkill( skill ); break;
			case ACTION_RESET: pet.resetSkills(); break;
		}
	}
	
	@Override
	public void write( ByteBuf buffer )
	{
		buffer.writeInt( entity );
		buffer.writeInt( action );
		buffer.writeInt( skill );
	}

	@Override
	public void read( ByteBuf buffer )
	{
		entity = buffer.readInt();
		action = buffer.readInt();
		skill = buffer.readInt();
	}
	
	public int entity;
	public int action;
	public int skill;

	public static final int ACTION_ADD = 0;
	public static final int ACTION_REMOVE = 1;
	public static final int ACTION_RESET = 2;
}
