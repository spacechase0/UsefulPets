package com.spacechase0.minecraft.usefulpets.pet.skill;

import net.minecraft.item.ItemStack;

import com.spacechase0.minecraft.usefulpets.pet.food.FoodType;

public class FoodSkill extends Skill
{
	public FoodSkill( int theId, String theName, FoodType theType, ItemStack theIcon )
	{
		super( theId, ( theName.equals( "" ) ? "hunger" : ( "hunger." + theName ) ), getPosX( theId, theName ), getPosY( theId, theName ), theIcon );
		type = theType;
		
		if ( theName.equals( "" ) )
		{
			mainId = id;
		}
		else
		{
			skillReqs = new int[] { mainId };
		}
	}
	
	public FoodSkill( int theId, String theName, int theLevelReq, FoodType theType, ItemStack theIcon )
	{
		this( theId, theName, theType, theIcon );
		levelReq = theLevelReq;
	}
	
	private static float getPosX( int id, String name )
	{
		float base = -3;
		if ( name.equals( "" ) )
		{
			return base;
		}
		
		int diff = id - ( mainId + 1 );
		
		return base - 1.f + ( ( diff % 2 ) * 2.f );
	}
	
	private static float getPosY( int id, String name )
	{
		if ( name.equals( "" ) )
		{
			return 0;
		}
		
		int diff = id - ( mainId + 1 );
		
		return 1.5f + ( diff / 2 );
	}
	
	public final FoodType type;
	private static int mainId;
}
