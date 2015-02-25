package com.spacechase0.minecraft.usefulpets.ai;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class FindItemsAi extends EntityAIBase
{

    private PetEntity thePet;
    private EntityLivingBase theOwner;
    World theWorld;
    private double field_75336_f;
    private PathNavigate petPathfinder;
    private int field_75343_h;
    private boolean field_75344_i;

    public FindItemsAi(PetEntity par1EntityTameable, double par2)
    {
        this.thePet = par1EntityTameable;
        this.theWorld = par1EntityTameable.worldObj;
        this.field_75336_f = par2;
        this.petPathfinder = par1EntityTameable.getNavigator();
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = (EntityLivingBase) this.thePet.getOwner();
        
        if (entitylivingbase == null)
        {
            return false;
        }
        else if (this.thePet.isSitting())
        {
            return false;
        }
        else if ( !thePet.hasSkill( Skill.INVENTORY_PICKUP.id ) )
        {
        	return false;
        }/*
        else if (this.thePet.getDistanceSqToEntity(entitylivingbase) < (double)(this.minDist * this.minDist))
        {
            return false;
        }*/
        else
        {
            this.theOwner = entitylivingbase;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.petPathfinder.noPath() &&  !this.thePet.isSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_75343_h = 0;
        this.field_75344_i = this.thePet.getNavigator().getAvoidsWater();
        this.thePet.getNavigator().setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        this.thePet.getNavigator().setAvoidsWater(this.field_75344_i);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (!this.thePet.isSitting())
        {
            if (--this.field_75343_h <= 0)
            {
                this.field_75343_h = 10;
                
                final double R = 15;
                List nearby = theWorld.getEntitiesWithinAABB( EntityItem.class, AxisAlignedBB.getBoundingBox( thePet.posX - R, thePet.posY - R, thePet.posZ - R, thePet.posX + R, thePet.posY + R, thePet.posZ + R ) );
                EntityItem closest = null;
                for ( Object obj : nearby )
                {
                	EntityItem item = ( EntityItem ) obj;
                	if ( thePet.hasRoomForItem( item.getEntityItem() ) <= 0 )
                	{
                		continue;
                	}
                	
                	if ( closest == null ) closest = item;
                	else if ( thePet.getDistanceSqToEntity( item ) < thePet.getDistanceSqToEntity( closest ) )
                	{
                		closest = item;
                	}
                }
                
                
                if (closest != null && !this.petPathfinder.tryMoveToXYZ(closest.posX,closest.posY,closest.posZ, this.field_75336_f))
                {
                    if (!this.thePet.getLeashed())
                    {
                        if (this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0D || thePet.posY < 0 )
                        {
                            int i = MathHelper.floor_double(this.theOwner.posX) - 2;
                            int j = MathHelper.floor_double(this.theOwner.posZ) - 2;
                            int k = MathHelper.floor_double(this.theOwner.boundingBox.minY);

                            for (int l = 0; l <= 4; ++l)
                            {
                                for (int i1 = 0; i1 <= 4; ++i1)
                                {
                                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.theWorld.doesBlockHaveSolidTopSurface(theWorld,i + l, k - 1, j + i1) && !this.theWorld.isBlockNormalCubeDefault(i + l, k, j + i1, false) && !this.theWorld.isBlockNormalCubeDefault(i + l, k + 1, j + i1, false))
                                    {
                                        this.thePet.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.thePet.rotationYaw, this.thePet.rotationPitch);
                                        this.petPathfinder.clearPathEntity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
