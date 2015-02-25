package com.spacechase0.minecraft.usefulpets;

import net.minecraft.entity.EntityCreature;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.spacechase0.minecraft.usefulpets.ai.AvoidPetEntityAI;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AvoidanceEventHandler
{
	@SubscribeEvent
	public void entityJoinedWorld( EntityJoinWorldEvent event )
	{
		if ( !( event.entity instanceof EntityCreature ) || event.entity.getClass().equals( PetEntity.class ) )
		{
			return;
		}
		EntityCreature creature = ( EntityCreature ) event.entity;
		
		creature.tasks.addTask( 3, new AvoidPetEntityAI( creature ) );
	}
}
