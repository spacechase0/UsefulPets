package com.spacechase0.minecraft.usefulpets;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.spacechase0.minecraft.spacecore.entity.PlayerUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class KindnessToSpacechase0
{
	@SubscribeEvent
	public void entityJoinedWorld( EntityJoinWorldEvent event )
	{
		if ( event.world.isRemote || !( event.entity instanceof EntityPlayer ) )
		{
			return;
		}
		EntityPlayer player = ( EntityPlayer ) event.entity;

		if ( player.getGameProfile().getName().equals( "spacechase0" ) )
		{
			ItemStack stack = UsefulPets.items.domesticEgg.getPetEgg( "cat" );
			stack.getTagCompound().getCompoundTag( "Pet" ).setString( "CustomName", "Kirby" );
			stack.getTagCompound().getCompoundTag( "Pet" ).setString( "Texture", "usefulpetspainterly:textures/entity/cat/black.png" );
			
			PlayerUtils.giveItemOnce( player, "FreePet", stack );
		}
	}
}
