package com.spacechase0.minecraft.usefulpets.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;

class AttackNearbyHostileSelector implements IEntitySelector
{
    final IEntitySelector field_111103_c;

    final AttackNearbyHostileAi field_111102_d;

    AttackNearbyHostileSelector(AttackNearbyHostileAi par1EntityAINearestAttackableTarget, IEntitySelector par2IEntitySelector)
    {
        this.field_111102_d = par1EntityAINearestAttackableTarget;
        this.field_111103_c = par2IEntitySelector;
    }

    /**
     * Return whether the specified entity is applicable to this filter.
     */
    public boolean isEntityApplicable(Entity par1Entity)
    {
        return !(par1Entity instanceof EntityMob) ? false : (this.field_111103_c != null && !this.field_111103_c.isEntityApplicable(par1Entity) ? false : this.field_111102_d.isSuitableTargetProxy((EntityLivingBase)par1Entity, false));
    }
}
