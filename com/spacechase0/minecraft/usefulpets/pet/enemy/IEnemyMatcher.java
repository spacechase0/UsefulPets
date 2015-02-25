package com.spacechase0.minecraft.usefulpets.pet.enemy;

import net.minecraft.entity.EntityLivingBase;

public interface IEnemyMatcher
{
	public boolean matches( EntityLivingBase entity );
}
