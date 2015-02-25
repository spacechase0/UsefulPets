package com.spacechase0.minecraft.usefulpets.pet.skill;

import net.minecraft.item.ItemStack;

public class DefenseSkill extends Skill
{
	private DefenseSkill( int theId, String theName, int theLevelReq, ItemStack theIcon )
	{
		super( theId, ( theName.equals( "" ) ? "defense" : ( "defense." + theName ) ), getPosX( theId, theName ), getPosY( theId, theName ), theIcon );
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
	
	public DefenseSkill( int theId, String theName, int theLevelReq, float thePercent, ItemStack theIcon )
	{
		this( theId, theName, theLevelReq, theIcon );
		types = null;
		percent = thePercent;
	}
	
	public DefenseSkill( int theId, String theName, int theLevelReq, String[] theTypes, ItemStack theIcon )
	{
		this( theId, theName, theLevelReq, theIcon );
		types = theTypes;
		percent = 100.f;
	}
	
	private static float getPosX( int id, String name )
	{
		float base = 2.5f;
		if ( name.equals( "" ) || name.equals( "featherFall" ) )
		{
			return base;
		}
		
		int diff = id - ( mainId + 1 );
		
		return base - 1.f + ( ( diff / 2 ) * 2.f );
	}
	
	private static float getPosY( int id, String name )
	{
		float base = 4.5f;
		if ( name.equals( "" ) )
		{
			return base;
		}
		else if ( name.equals( "featherFall" ) )
		{
			return base + 3.5f;
		}
		
		int diff = id - ( mainId + 1 );
		
		return base + 1.5f + ( diff % 2 );
	}
	
	public String[] types = null;
	public float percent = 0.f;
	private static int mainId;
	private static int prevId;
}
