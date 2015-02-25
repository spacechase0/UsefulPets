package com.spacechase0.minecraft.usefulpets.ai;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.skill.RepellantSkill;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class AvoidPetEntityAI extends EntityAIBase
{
	// Why is their's not public? :(
	private static class Selector implements IEntitySelector
	{
	    final AvoidPetEntityAI entityAvoiderAI;

	    Selector(AvoidPetEntityAI par1EntityAIAvoidEntity)
	    {
	        this.entityAvoiderAI = par1EntityAIAvoidEntity;
	    }

	    /**
	     * Return whether the specified entity is applicable to this filter.
	     */
	    public boolean isEntityApplicable(Entity par1Entity)
	    {
	        return par1Entity.isEntityAlive() && AvoidPetEntityAI.func_98217_a(this.entityAvoiderAI).getEntitySenses().canSee(par1Entity);
	    }
	}
	
	public AvoidPetEntityAI( EntityCreature creature )
	{
		this( creature, PetEntity.class, 10.f, 1.f, 1.2f );
	}
	
	private float getRangeOf( PetEntity pet )
	{
		float range = 0.f;
		for ( int id : Skill.skills.keySet() )
		{
			Skill skill = Skill.forId( id );
			if ( !( skill instanceof RepellantSkill ) || !pet.hasSkill( id ) )
			{
				continue;
			}
			RepellantSkill repellant = ( RepellantSkill ) skill;
			
			range += repellant.radius;
		}
		
		return range;
	}
	
	private boolean canChaseMe( PetEntity pet )
	{
		for ( int id : Skill.skills.keySet() )
		{
			Skill skill = Skill.forId( id );
			if ( !( skill instanceof RepellantSkill ) || !pet.hasSkill( id ) )
			{
				continue;
			}
			RepellantSkill repellant = ( RepellantSkill ) skill;

			if ( repellant.type != null && repellant.type.matches( theEntity ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
    private AvoidPetEntityAI(EntityCreature par1EntityCreature, Class par2Class, float par3, double par4, double par6)
    {
        this.theEntity = par1EntityCreature;
        this.targetEntityClass = par2Class;
        this.distanceFromEntity = par3;
        this.farSpeed = par4;
        this.nearSpeed = par6;
        this.entityPathNavigate = par1EntityCreature.getNavigator();
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
    	{
            List list = this.theEntity.worldObj.selectEntitiesWithinAABB(this.targetEntityClass, this.theEntity.boundingBox.expand((double)this.distanceFromEntity, 3.0D, (double)this.distanceFromEntity), this.field_98218_a);

            if (list.isEmpty())
            {
                return false;
            }

            closestLivingEntity = null;
            for ( int i = 0; i < list.size(); ++i )
            {
            	PetEntity pet = ( PetEntity ) list.get( i );
            	if ( theEntity.getDistanceToEntity( pet ) <= getRangeOf( pet ) && canChaseMe( pet ) )
            	{
            		closestLivingEntity = pet;
            		break;
            	}
            }
            
            if ( closestLivingEntity == null )
            {
            	return false;
            }
        }

        Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, Vec3.createVectorHelper(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

        if (vec3 == null)
        {
            return false;
        }
        else if (this.closestLivingEntity.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity))
        {
            return false;
        }
        else
        {
            this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(vec3);
        }
    }

    @Override
    public boolean continueExecuting()
    {
        return !this.entityPathNavigate.noPath();
    }

    @Override
    public void startExecuting()
    {
        this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
    }

    @Override
    public void resetTask()
    {
        this.closestLivingEntity = null;
    }

    @Override
    public void updateTask()
    {
        if (this.theEntity.getDistanceSqToEntity(this.closestLivingEntity) < 49.0D)
        {
            this.theEntity.getNavigator().setSpeed(this.nearSpeed);
        }
        else
        {
            this.theEntity.getNavigator().setSpeed(this.farSpeed);
        }
    }

    static EntityCreature func_98217_a(AvoidPetEntityAI par0EntityAIAvoidEntity)
    {
        return par0EntityAIAvoidEntity.theEntity;
    }

    public final IEntitySelector field_98218_a = new Selector(this);
    private EntityCreature theEntity;
    private double farSpeed;
    private double nearSpeed;
    private Entity closestLivingEntity;
    private float distanceFromEntity;
    private PathEntity entityPathEntity;
    private PathNavigate entityPathNavigate;
    private Class targetEntityClass;
}
