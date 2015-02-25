package com.spacechase0.minecraft.usefulpets.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class OwnerHurtTargetAI extends EntityAITarget
{
	PetEntity theEntityTameable;
    EntityLivingBase theTarget;
    private int field_142050_e;

    public OwnerHurtTargetAI(PetEntity par1EntityTameable)
    {
        super(par1EntityTameable, false);
        this.theEntityTameable = par1EntityTameable;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if ( !theEntityTameable.hasSkill( Skill.COMBAT.id ) )
    	{
    		return false;
    	}
    	
    	/*
        if ( false )
        {
            return false;
        }
        else
        */
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase) this.theEntityTameable.getOwner();

            if (entitylivingbase == null)
            {
                return false;
            }
            else
            {
                this.theTarget = entitylivingbase.getAITarget();
                int i = entitylivingbase.func_142015_aE();
                return i != this.field_142050_e && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.isValidTarget(this.theTarget);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.theTarget);
        EntityLivingBase entitylivingbase = (EntityLivingBase) this.theEntityTameable.getOwner();

        if (entitylivingbase != null)
        {
            this.field_142050_e = entitylivingbase.func_142015_aE();
        }

        super.startExecuting();
    }
}
