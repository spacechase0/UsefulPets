package com.spacechase0.minecraft.usefulpets.ai;

import java.util.Collections;
import java.util.List;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;

public class AttackNearbyHostileAi extends EntityAITarget
{
    /** Instance of EntityAINearestAttackableTargetSorter. */
    private EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;

    /**
     * This filter is applied to the Entity search.  Only matching entities will be targetted.  (null -> no
     * restrictions)
     */
    private EntityLivingBase targetEntity;
    private IEntitySelector targetEntitySelector;
    
    private PetEntity pet;

    public AttackNearbyHostileAi(PetEntity thePet)
    {
        super(thePet, true, true);
        //this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTargetSorter(pet);
        this.setMutexBits(1);
        this.targetEntitySelector = new AttackNearbyHostileSelector( this, null );
        pet = thePet;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	
        if ( !pet.hasSkill( Skill.COMBAT_HOSTILE.id ) )
        {
            return false;
        }
        else
        {
            double d0 = 10;//this.getTargetDistance();
            List list = this.taskOwner.worldObj.selectEntitiesWithinAABB(EntityMob.class, this.taskOwner.boundingBox.expand(d0, 4.0D, d0), this.targetEntitySelector);
            if (!list.isEmpty())
            {
            	if ( pet.getOwner() != null )
            	{
                	theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter( pet.getOwner() );
            	}
            	else
            	{
                	theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter( pet );
            	}
            }
            Collections.sort(list, this.theNearestAttackableTargetSorter);

            if (list.isEmpty())
            {
                return false;
            }
            else
            {
                this.targetEntity = (EntityLivingBase)list.get(0);
                return true;
            }
        }
    }

    public boolean isSuitableTargetProxy(EntityLivingBase par1EntityLivingBase, boolean par2)
    {
    	return isSuitableTarget( par1EntityLivingBase, par2 );
    }
    
    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}
