package com.spacechase0.minecraft.usefulpets.pet.enemy;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;

public class AttributeEnemyMatcher implements IEnemyMatcher
{
	public AttributeEnemyMatcher( EnumCreatureAttribute theAttr )
	{
		attr = theAttr;
	}
	
	@Override
	public boolean matches( EntityLivingBase entity )
	{
		if ( !( entity instanceof EntityLiving ) )
		{
			return false;
		}
		EntityLiving living = ( EntityLiving ) entity;
		
		return living.getCreatureAttribute().equals( attr );
	}
	
	public final EnumCreatureAttribute attr;
}
