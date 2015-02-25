package com.spacechase0.minecraft.usefulpets.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

public class SitAI extends EntityAIBase
{
    public SitAI( PetEntity thePet )
    {
        pet = thePet;
        setMutexBits( 5 ); // HUH?
    }
    
    @Override
    public boolean shouldExecute()
    {
       if ( pet.isInWater() )
        {
            return false;
        }
        else if ( !pet.onGround )
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase) pet.getOwner();
            return entitylivingbase == null ? true : ( pet.getDistanceSqToEntity(entitylivingbase) < 144.0D && entitylivingbase.getAITarget() != null ? false : sitting);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	pet.getNavigator().clearPathEntity();
        //pet.setSitting( true );
    }

    @Override
    public void resetTask()
    {
       //pet.setSitting( false );
    }

    
    public void setSitting( boolean par1 )
    {
        this.sitting = par1;
    }
	
    private PetEntity pet;
	private boolean sitting = false;
}
