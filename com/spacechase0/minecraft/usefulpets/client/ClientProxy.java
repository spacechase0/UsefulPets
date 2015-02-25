package com.spacechase0.minecraft.usefulpets.client;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;

import com.spacechase0.minecraft.usefulpets.CommonProxy;
import com.spacechase0.minecraft.usefulpets.PetTrackingDataHandler.PetData;
import com.spacechase0.minecraft.usefulpets.client.gui.PetTrackingGui;
import com.spacechase0.minecraft.usefulpets.client.render.PetEntityRenderer;
import com.spacechase0.minecraft.usefulpets.client.tick.PetJumpTicker;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		RenderingRegistry.registerEntityRenderingHandler( PetEntity.class, new PetEntityRenderer() );
		
		System.out.println("Client proxy - registering ticker" );
		FMLCommonHandler.instance().bus().register( new PetJumpTicker() );
	}
	
	@Override
	public void setTrackingData( Map< UUID, PetData > data )
	{
		Minecraft mc = Minecraft.getMinecraft();
		if ( !( mc.currentScreen instanceof PetTrackingGui ) )
		{
			return;
		}
		PetTrackingGui gui = ( PetTrackingGui ) mc.currentScreen;
		
		gui.data = data;
	}
}
