package com.spacechase0.minecraft.usefulpets.pet.skill;

import net.minecraft.item.ItemStack;

public class AttackSkill extends Skill
{
	public AttackSkill( int theId, String theName, int theDamage, ItemStack theIcon )
	{
		super( theId, ( theName.equals( "" ) ? "combat" : ( "combat." + theName ) ), getPosX( theId, theName ), getPosY( theId, theName ), theIcon );
		damage = theDamage;
		
		if ( theName.equals( "" ) )
		{
			mainId = id;
		}
		else
		{
			skillReqs = new int[] { prevId };
		}
		prevId = id;
	}
	
	public AttackSkill( int theId, String theName, int theLevelReq, int theDamage, ItemStack theIcon )
	{
		this( theId, theName, theDamage, theIcon );
		levelReq = theLevelReq;
	}
	
	private static float getPosX( int id, String name )
	{
		if ( name.equals( "" ) )
		{
			return 1.5f;
		}
		
		return 0.f;
	}
	
	private static float getPosY( int id, String name )
	{
		if ( name.equals( "" ) )
		{
			return 2.4f;
		}
		
		int diff = id - ( mainId + 1 );
		
		return 1.2f + ( diff * 1.2f );
	}
	
	public final int damage;
	private static int mainId;
	private static int prevId;
}
