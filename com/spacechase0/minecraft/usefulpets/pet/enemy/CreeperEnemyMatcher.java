package com.spacechase0.minecraft.usefulpets.pet.enemy;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;

public class CreeperEnemyMatcher implements IEnemyMatcher
{
	@Override
	public boolean matches( EntityLivingBase entity )
	{
		return ( entity instanceof EntityCreeper );
	}
}
