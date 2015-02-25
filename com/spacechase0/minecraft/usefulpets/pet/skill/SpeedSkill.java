package com.spacechase0.minecraft.usefulpets.pet.skill;

import net.minecraft.item.ItemStack;

public class SpeedSkill extends Skill
{
	private SpeedSkill( int theId, String theName, int theLevelReq, ItemStack theIcon )
	{
		super( theId, ( theName.equals( "" ) ? "travel" : ( "travel." + theName ) ), getPosX( theId, theName ), getPosY( theId, theName ), theIcon );
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
	
	public SpeedSkill( int theId, String theName, int theLevelReq, float thePercent, ItemStack theIcon )
	{
		this( theId, theName, theLevelReq, theIcon );
		percent = thePercent;
	}
	
	private static float getPosX( int id, String name )
	{
		float base = -0.5f;
		if ( name.equals( "" ) )
		{
			return base;
		}
		
		int diff = id - ( mainId + 1 );
		
		return base - 1.f + ( ( diff / 2 ) * 2.f );
	}
	
	private static float getPosY( int id, String name )
	{
		float base = 7.5f;
		if ( name.equals( "" ) )
		{
			return base;
		}
		
		int diff = id - ( mainId + 1 );
		
		return base + 1.5f + ( ( diff % 2 ) * 1.25f );
	}
	
	public float percent = 0.f;
	private static int mainId;
	private static int prevId;
}
