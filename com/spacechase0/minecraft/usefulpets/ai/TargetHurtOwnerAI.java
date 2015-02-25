package com.spacechase0.minecraft.usefulpets.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class TargetHurtOwnerAI extends EntityAITarget
{
    PetEntity theDefendingTameable;
    EntityLivingBase theOwnerAttacker;
    private int field_142051_e;

    public TargetHurtOwnerAI( PetEntity par1EntityTameable )
    {
        super(par1EntityTameable, false);
        this.theDefendingTameable = par1EntityTameable;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if ( !theDefendingTameable.hasSkill( Skill.COMBAT.id ) )
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
            EntityLivingBase entitylivingbase = ( EntityLivingBase ) this.theDefendingTameable.getOwner();

            if (entitylivingbase == null)
            {
                return false;
            }
            else
            {
                this.theOwnerAttacker = entitylivingbase.getAITarget();
                int i = entitylivingbase.func_142015_aE();
                return i != this.field_142051_e && this.isSuitableTarget(this.theOwnerAttacker, false) && this.theDefendingTameable.isValidTarget(this.theOwnerAttacker);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.theOwnerAttacker);
        EntityLivingBase entitylivingbase = ( EntityLivingBase ) this.theDefendingTameable.getOwner();

        if (entitylivingbase != null)
        {
            this.field_142051_e = entitylivingbase.func_142015_aE();
        }

        super.startExecuting();
    }
}
