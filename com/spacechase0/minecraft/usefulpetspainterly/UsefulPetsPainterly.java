package com.spacechase0.minecraft.usefulpetspainterly;

import com.spacechase0.minecraft.spacecore.BaseMod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

// 1.0.4 - Added silverfish support.
// 1.0.3 - Updated for SpaceCore 0.7.9.
// 1.0.2 - Added slime support.

@Mod( modid = "SC0_UsefulPetsPainterly", useMetadata = true, dependencies="required-after:SC0_SpaceCore;required-after:SC0_UsefulPets" )
public class UsefulPetsPainterly extends BaseMod
{
	public UsefulPetsPainterly()
	{
		super( "usefulpetspainterly" );
	}

	@Instance( "SC0_UsefulPetsPainterly" )
	public static UsefulPetsPainterly instance;
	
	@SidedProxy( serverSide = "com.spacechase0.minecraft.usefulpetspainterly.CommonProxy",
			     clientSide = "com.spacechase0.minecraft.usefulpetspainterly.client.ClientProxy" )
	public static CommonProxy proxy;

	@Override
	@EventHandler
	public void postInit( FMLPostInitializationEvent event )
	{
		super.postInit( event );
		
		proxy.postInit();
	}
}
