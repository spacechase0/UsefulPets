package com.spacechase0.minecraft.usefulpets;

import java.util.Map;
import java.util.UUID;

import com.spacechase0.minecraft.usefulpets.PetTrackingDataHandler.PetData;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

import cpw.mods.fml.relauncher.Side;

public class CommonProxy
{
	public void init()
	{
	}
	
	public void setTrackingData( Map< UUID, PetData > data )
	{
	}
	
	public PetEntity getPendingPetForGui( Side side )
	{
		return pendingGui[ side.ordinal() ];
	}
	
	public void setPendingPetForGui( Side side, PetEntity entity )
	{
		pendingGui[ side.ordinal() ] = entity;
	}
	
	private PetEntity[] pendingGui = new PetEntity[ 2 ];
}
