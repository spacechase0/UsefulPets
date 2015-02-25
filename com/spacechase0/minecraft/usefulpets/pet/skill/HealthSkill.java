package com.spacechase0.minecraft.usefulpets.pet.skill;

import net.minecraft.item.ItemStack;

public class HealthSkill extends Skill
{
	private HealthSkill( int theId, String theName, int theLevelReq, ItemStack theIcon )
	{
		super( theId, ( theName.equals( "" ) ? "health" : ( "health." + theName ) ), getPosX( theId, theName ), getPosY( theId, theName ), theIcon );
		levelReq = theLevelReq;
		
		if ( theName.equals( "" ) )
		{
			mainId = id;
		}
		else if ( theName.startsWith( "upgrade" ) )
		{
			skillReqs = new int[] { prevId };
		}
		else
		{
			skillReqs = new int[] { mainId };
		}
		prevId = id;
	}
	
	public HealthSkill( int theId, String theName, int theLevelReq, int theExtra, ItemStack theIcon )
	{
		this( theId, theName, theLevelReq, theIcon );
		extra = theExtra;
	}
	
	private static float getPosX( int id, String name )
	{
		float base = -1.f;
		if ( name.equals( "" ) )
		{
			return base;
		}
		
		int diff = id - mainId;
		
		return base + ( diff * 1.5f );
	}
	
	private static float getPosY( int id, String name )
	{
		return -3.f;
	}
	
	public int extra = 10;
	private static int mainId;
	private static int prevId;
}
