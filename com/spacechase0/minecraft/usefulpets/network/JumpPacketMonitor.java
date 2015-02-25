package com.spacechase0.minecraft.usefulpets.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import com.spacechase0.minecraft.spacecore.network.IPacketMonitor;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

public class JumpPacketMonitor implements IPacketMonitor
{
	@Override
	public void monitorIncoming( INetHandler net, net.minecraft.network.Packet packet )
	{
		if ( !( net instanceof NetHandlerPlayServer ) || !( packet instanceof C0BPacketEntityAction ) )
		{
			return;
		}
		
		NetHandlerPlayServer server = ( NetHandlerPlayServer ) net;
		C0BPacketEntityAction action = ( C0BPacketEntityAction ) packet;
		
		if ( action.func_149513_d() != 6 || !( server.playerEntity.ridingEntity instanceof PetEntity ) )
		{
			return;
		}
		PetEntity pet = ( PetEntity ) server.playerEntity.ridingEntity;

		pet.setJumpingState( action.func_149512_e() );
	}

	@Override
	public void monitorOutgoing( NetworkManager net, net.minecraft.network.Packet packet )
	{
	}
}
