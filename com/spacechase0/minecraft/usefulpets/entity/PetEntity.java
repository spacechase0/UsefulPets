package com.spacechase0.minecraft.usefulpets.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.spacechase0.minecraft.spacecore.util.TranslateUtils;
import com.spacechase0.minecraft.spacecore.util.Vector3i;
import com.spacechase0.minecraft.usefulpets.PetTrackingDataHandler.PetData;
import com.spacechase0.minecraft.usefulpets.UsefulPets;
import com.spacechase0.minecraft.usefulpets.ai.AttackNearbyHostileAi;
import com.spacechase0.minecraft.usefulpets.ai.FindItemsAi;
import com.spacechase0.minecraft.usefulpets.ai.FollowOwnerAI;
import com.spacechase0.minecraft.usefulpets.ai.OwnerHurtTargetAI;
import com.spacechase0.minecraft.usefulpets.ai.SitAI;
import com.spacechase0.minecraft.usefulpets.ai.TargetHurtOwnerAI;
import com.spacechase0.minecraft.usefulpets.inventory.PetInventory;
import com.spacechase0.minecraft.usefulpets.item.ClawItem;
import com.spacechase0.minecraft.usefulpets.item.PetWandItem;
import com.spacechase0.minecraft.usefulpets.pet.Level;
import com.spacechase0.minecraft.usefulpets.pet.PetType;
import com.spacechase0.minecraft.usefulpets.pet.skill.AttackSkill;
import com.spacechase0.minecraft.usefulpets.pet.skill.DefenseSkill;
import com.spacechase0.minecraft.usefulpets.pet.skill.FoodSkill;
import com.spacechase0.minecraft.usefulpets.pet.skill.HealthSkill;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;
import com.spacechase0.minecraft.usefulpets.pet.skill.SpeedSkill;

import cpw.mods.fml.relauncher.Side;

public class PetEntity extends EntityAnimal implements IEntityOwnable
{
	// MINE! :P
	public PetEntity( World world )
	{
		super( world );
		setSize( type.sizeX, type.sizeY );
		
		getNavigator().setAvoidsWater( true );
        tasks.addTask( 1, new EntityAISwimming( this ) );
        tasks.addTask( 2, aiSit );
        tasks.addTask( 3, new EntityAILeapAtTarget( this, 0.4F ) );
        tasks.addTask( 4, new EntityAIAttackOnCollide( this, 1.0D, true ) );
        tasks.addTask( 5, new FollowOwnerAI( this, 1.0D, 10.0F, 3.5F ) );
        tasks.addTask( 6, new FindItemsAi( this, 1.0D ) );
        tasks.addTask( 7, new EntityAIWander(this, 1.0D));
        tasks.addTask( 9, new EntityAIWatchClosest( this, EntityPlayer.class, 9.0F ) );
        targetTasks.addTask(1, new TargetHurtOwnerAI( this ) );
        targetTasks.addTask(3, new OwnerHurtTargetAI( this ) );
        targetTasks.addTask(2, new EntityAIHurtByTarget( this, true ) );
        targetTasks.addTask(4, new AttackNearbyHostileAi( this ) );
	}
	
	public int getLevel()
	{
		return dataWatcher.getWatchableObjectInt( DATA_LEVEL );
	}
	
	public void setLevel( int level )
	{
		dataWatcher.updateObject( DATA_LEVEL, level );
	}
	
	public void levelUp()
	{
		if ( getLevel() >= Level.MAX_LEVEL )
		{
			return;
		}
		
		setLevel( getLevel() + 1 );
		setFreeSkillPoints( getFreeSkillPoints() + 1 );
	}
	
	public int getFreeSkillPoints()
	{
		return dataWatcher.getWatchableObjectInt( DATA_FREE_POINTS );
	}
	
	public void setFreeSkillPoints( int points )
	{
		dataWatcher.updateObject( DATA_FREE_POINTS, points );
	}
	
	public boolean hasSkill( int id )
	{
		//System.out.println("has: "+id+" "+skills.contains(id));
		return skills.contains( id );
	}
	
	public boolean hasSkillRequirements( int skillId )
	{
		Skill skill = Skill.forId( skillId );
		if ( skill.levelReq > getLevel() )
		{
			return false;
		}
		
		if ( skill.skillReqs == null )
		{
			return true;
		}
		
		for ( int is = 0; is < skill.skillReqs.length; ++is )
		{
			Skill parent = Skill.forId( skill.skillReqs[ is ] );
			if ( !hasSkill( parent.id ) )
			{
				return false;
			}
		}
		
		return true;
	}
	
	public void addSkill( int id )
	{
		if ( hasSkill( id ) || getFreeSkillPoints() < 1 || !hasSkillRequirements( id ) )
		{
			return;
		}
		
		skills.add( id );
		setFreeSkillPoints( getFreeSkillPoints() - 1 );
		dataWatcher.updateObject( DATA_SKILLS, getSkillsStack() );
	}
	
	public void removeSkill( int id )
	{
		if ( hasSkill( id ) && !type.defaultSkills.contains( id ) )
		{
			for ( Skill skill : Skill.skills.values() )
			{
				for ( int i = 0; skill.skillReqs != null && i < skill.skillReqs.length; ++i )
				{
					int reqId = skill.skillReqs[ i ];
					if ( reqId == id )
					{
						removeSkill( skill.id );
						break;
					}
				}
			}
			
			skills.remove( new Integer( id ) );
			setFreeSkillPoints( getFreeSkillPoints() + 1 );
		}
		dataWatcher.updateObject( DATA_SKILLS, getSkillsStack() );
	}
	
	private ItemStack getSkillsStack()
	{
		if ( skills == null || skills.size() == 0 )
		{
			return new ItemStack( Items.arrow );
		}
		
		int[] theSkills = new int[ skills.size() ];
		for ( int i = 0; i < skills.size(); ++i )
		{
			theSkills[ i ] = skills.get( i );
		}
		
		ItemStack stack = new ItemStack( Items.stick );
		stack.setTagCompound( new NBTTagCompound() );
		stack.getTagCompound().setIntArray( "Skills", theSkills );
		
		return stack;
	}
	
	private void updateSpeed()
	{
		IAttributeInstance attr = getEntityAttribute( SharedMonsterAttributes.movementSpeed );
		
		float percent = 1.f;
		for ( Skill skill : Skill.skills.values() )
		{
			if ( !hasSkill( skill.id ) || !( skill instanceof SpeedSkill ) )
			{
				continue;
			}
			SpeedSkill speed = ( SpeedSkill ) skill;
			percent += speed.percent;
		}
		
		attr.setBaseValue( 0.3 * percent );
	}
	
	private void updateMaxHealth()
	{
		IAttributeInstance attr = getEntityAttribute( SharedMonsterAttributes.maxHealth );
		
		int max = 20;
		for ( Skill skill : Skill.skills.values() )
		{
			if ( !hasSkill( skill.id ) || !( skill instanceof HealthSkill ) )
			{
				continue;
			}
			HealthSkill health = ( HealthSkill ) skill;
			max += health.extra;
		}
		
		attr.setBaseValue( max );
	}
	
	// TODO: Test me
	public void resetSkills()
	{
		for ( int id : type.defaultSkills )
		{
			skills.remove( id );
		}
		
		setFreeSkillPoints( getFreeSkillPoints() + skills.size() );
		setPetType( type ); // Resets skills to default
	}
	
	public PetType getPetType()
	{
		return type;
	}
	
	public void setPetType( PetType theType )
	{
		type = theType;
		setSize( type.sizeX, type.sizeY );
		skills.clear();
		skills.addAll( type.defaultSkills );
		dataWatcher.updateObject( DATA_TYPE, type.name );
		dataWatcher.updateObject( DATA_SKILLS, getSkillsStack() );
		setTexture( type.textures.get( 0 ) );
	}
	
	public boolean isSitting()
	{
		return ( dataWatcher.getWatchableObjectByte( DATA_SITTING ) != 0 );
	}
	
	public void setSitting( boolean sitting )
	{
		dataWatcher.updateObject( DATA_SITTING, ( byte )( sitting ? 1 : 0 ) );
		aiSit.setSitting( isSitting() );
		if ( sitting )
		{
			this.setTarget( null );
			this.setAttackTarget( null );
		}
	}
	
	public float getHunger()
	{
		return dataWatcher.getWatchableObjectFloat( DATA_HUNGER );
	}
	
	public void useHunger( float amount )
	{
		float satDiff = Math.min( amount, saturation );
		saturation -= satDiff;
		if ( satDiff != amount )
		{
			setHunger( getHunger() - ( amount - satDiff ) );
		}
	}
	
	public void setHunger( float hunger )
	{
		if ( hunger < 0.f )
		{
			hunger = 0.f;
		}
		else if ( hunger > MAX_HUNGER )
		{
			hunger = MAX_HUNGER;
		}
		
		dataWatcher.updateObject( DATA_HUNGER, hunger );
	}
	
	public float getSaturation()
	{
		return saturation;
	}
	
	public void setSaturation( float theSaturation )
	{
		saturation = theSaturation;
	}
	
	public boolean isValidTarget( EntityLivingBase target )
	{
		return !( target instanceof EntityCreeper );
	}
	
	public IInventory getInventory()
	{
		return inv;
	}
	
	public int hasRoomForItem( ItemStack stack )
	{
		boolean empty = false;
		ItemStack found = null;
		for ( int i = 3; i < inv.getSizeInventory(); ++i )
		{
			ItemStack slot = inv.getStackInSlot( i );
			if ( slot == null )
			{
				empty = true;
			}
			else if ( stack.getItem() == slot.getItem() && stack.getItemDamage() == slot.getItemDamage() && ItemStack.areItemStackTagsEqual( stack, slot ) )
			{
				found = slot;
			}
		}
		
		     if ( found == null ) return 0;
		else if ( empty )         return stack.stackSize;
		else                      return found.getMaxStackSize() - found.stackSize;
	}
	
	public boolean hasSaddle()
	{
		return ( inv.getStackInSlot( 0 ) != null && inv.getStackInSlot( 0 ).getItem() == Items.saddle );
	}
	
	public String getTexture()
	{
		return dataWatcher.getWatchableObjectString( DATA_TEXTURE );
	}
	
	public void setTexture( String tex )
	{
		if ( tex == null )
		{
			return;
		}
		
		dataWatcher.updateObject( DATA_TEXTURE, tex );
	}
	
	public int getDisplayFlags()
	{
		return dataWatcher.getWatchableObjectInt( DATA_DISPLAY_FLAGS );
	}
	
	public void setDisplayFlags( int flags )
	{
		dataWatcher.updateObject( DATA_DISPLAY_FLAGS, flags );
	}
	
	// Entity
    @Override
    public void writeEntityToNBT( NBTTagCompound tag)
    {
        super.writeEntityToNBT( tag );
        
        tag.setString( "OwnerID", func_152113_b() );
        tag.setString( "Type", getPetType().name );
        tag.setInteger( "Level", getLevel() );
        tag.setInteger( "FreeSkillPoints", getFreeSkillPoints() );
        int[] theSkills = new int[ skills.size() ];
        for ( int i = 0; i < skills.size(); ++i )
        {
        	theSkills[ i ] = skills.get( i );
        }
        tag.setTag( "Skills", new NBTTagIntArray( theSkills ) );
        {
        	NBTTagList list = new NBTTagList();
        	for ( int i = 0; i < 12; ++i )
        	{
        		ItemStack stack = inv.getStackInSlot( i );
        		
        		NBTTagCompound compound = new NBTTagCompound();
        		if ( stack != null )
        		{
        			stack.writeToNBT( compound );
        		}
        		
        		list.appendTag( compound );
        	}
        	tag.setTag( "Inventory", list );
        }
        
        tag.setBoolean( "Sitting", isSitting() );
        tag.setFloat( "Hunger", getHunger() );
        tag.setFloat( "Saturation", getSaturation() );
        tag.setString( "Texture", getTexture() );
    }

    @Override
    public void readEntityFromNBT( NBTTagCompound tag )
    {
        super.readEntityFromNBT( tag );
        
        String str;
        if ( ( str = tag.getString( "Owner" ) ) != null )
        {
        	setOwnerUUID( PreYggdrasilConverter.func_152719_a( str ) );
        }
        if ( ( str = tag.getString( "OwnerID" ) ) != null )
        {
        	setOwnerUUID( tag.getString( "OwnerID" ) );
        }
        setPetType( PetType.forName( tag.getString( "Type" ) ) );
        setLevel( tag.getInteger( "Level" ) );
        setFreeSkillPoints( tag.getInteger( "FreeSkillPoints" ) );
        skills.clear();
        int[] theSkills = ( ( NBTTagIntArray ) tag.getTag( "Skills" ) ).func_150302_c();
        for ( int id : theSkills )
        {
        	skills.add( id );
        }
        dataWatcher.updateObject( DATA_SKILLS, getSkillsStack() );
        if ( tag.getTag( "Inventory" ) != null )
        {
        	NBTTagList list = ( NBTTagList ) tag.getTag( "Inventory" );
        	for ( int i = 0; i < Math.min( 12, list.tagCount() ); ++i )
        	{
        		NBTTagCompound compound = ( NBTTagCompound ) list.getCompoundTagAt( i );
        		if ( compound.hasNoTags() )
        		{
        			continue;
        		}
        		
        		ItemStack stack = ItemStack.loadItemStackFromNBT( compound );
        		inv.setInventorySlotContents( i, stack );
        	}
        }
        
        setSitting( tag.getBoolean( "Sitting" ) );
        setHunger( tag.getFloat( "Hunger" ) );
        setSaturation( tag.getFloat( "Saturation" ) );
        
        // New with 1.0
        if ( tag.hasKey( "Texture" ) )
        {
        	setTexture( tag.getString( "Texture" ) );
        }
        else
        {
        	setTexture( type.textures.get( 0 ) );
        }
    }
    
    @Override
    protected void entityInit()
    {
        super.entityInit();
        
        dataWatcher.addObject( DATA_OWNER, "" );
        dataWatcher.addObject( DATA_TYPE, "cat" );
        dataWatcher.addObject( DATA_SITTING, ( byte ) 0 );
        dataWatcher.addObject( DATA_HUNGER, 20.f );
        dataWatcher.addObject( DATA_LEVEL, 1 );
        dataWatcher.addObject( DATA_FREE_POINTS, 1 );
        dataWatcher.addObject( DATA_SKILLS, getSkillsStack() );
        dataWatcher.addObject( DATA_TEXTURE, "textures/entity/cat/red.png" );
        dataWatcher.addObject( DATA_DISPLAY_FLAGS, 0 );
    }
    
    @Override
    public void onUpdate()
    {
    	if ( !hasSkill( Skill.COMBAT.id ) || getHealth() <= 0 || getEntityToAttack() == getOwner() || getAttackTarget() == getOwner() )
    	{
    		setTarget( null );
    		setAttackTarget( null );
    	}
    	
    	if ( riddenByEntity != null && ( !hasSkill( Skill.TRAVEL_MOUNTABLE.id ) || getHealth() <= 0 ) )
    	{
    		riddenByEntity.mountEntity( null );
    	}
    	
    	updateSpeed();
    	updateMaxHealth();
    	
    	super.onUpdate();
    	
    	if ( posY < -4.f )
    	{
    		setPosition( posX, -4.f, posZ );
    	}
    	
    	if ( ++syncTimer >= 20 )
    	{
    		if ( worldObj.isRemote )
    		{
	    		setOwnerUUID( dataWatcher.getWatchableObjectString( DATA_OWNER ) );
	        	type = PetType.forName( dataWatcher.getWatchableObjectString( DATA_TYPE ) );
	
	        	skills.clear();
	        	ItemStack skillStack = dataWatcher.getWatchableObjectItemStack( DATA_SKILLS );
	        	if ( skillStack.getItem() == Items.stick )
	        	{
	            	int[] newSkills = skillStack.getTagCompound().getIntArray( "Skills" );
	            	for ( int id : newSkills )
	            	{
	            		skills.add( id );
	            	}
	        	}
    		}
    		else
    		{
    			PetData data = new PetData();
    			data.dim = dimension;
    			data.pos = new Vector3i( ( int ) posX, ( int ) posY, ( int ) posZ );
    			
    			data.name = getCommandSenderName(); // getEntityName(); ?
    			data.tex = getTexture();
    			data.level = getLevel();
    			
    			UsefulPets.instance.petData.setPetData( func_152113_b(), getPersistentID(), data );
    			
    			int flags = 0;
    			if ( inv.getStackInSlot( 0 ) != null && inv.getStackInSlot( 0 ).getItem() == Items.saddle )
    			{
    				flags |= FLAG_SADDLE;
    			}
    			setDisplayFlags( flags );
    		}
        	
        	syncTimer = 0;
    	}
    	//else
    	{
    		if ( !isSitting() ) useHunger( ( riddenByEntity != null ) ? 0.0005f : 0.0002f );
    		
    		if ( getHunger() >= MAX_HUNGER / 2 && getHealth() < getMaxHealth() )
    		{
    			if ( ++regenTicks >= 35 )
    			{
    				setHealth( getHealth() + 1 );
    				useHunger( 0.2f );
    				regenTicks = 0;
    			}
    		}
    		
    		if ( getHunger() < MAX_HUNGER && hasSkill( Skill.INVENTORY_FEEDING.id ) )
    		{
    			for ( int i = 3; i < inv.getSizeInventory(); ++i )
    			{
    				ItemStack stack = inv.getStackInSlot( i );
	    	    	if ( stack != null && stack.getItem() instanceof ItemFood )
	    	    	{
	    	    		ItemFood food = ( ItemFood ) stack.getItem();
	    	    		
	    	    		boolean canEat = false;
	    	    		for ( int id : skills )
	    	    		{
	    	    			Skill skill = Skill.forId( id );
	    	    			if ( !( skill instanceof FoodSkill ) )
	    	    			{
	    	    				continue;
	    	    			}
	    	    			FoodSkill foodSkill = ( FoodSkill ) skill;
	    	    			
	    	    			if ( foodSkill.type.doesMatch( type, stack ) )
	    	    			{
	    	    				canEat = true;
	    	    				break;
	    	    			}
	    	    		}

	    	    		/*
	    	    		System.out.println(getHunger());
	    	    		System.out.println(food.getHealAmount());
	    	    		System.out.println("");
	    	    		//*/
	    	    		//System.out.println( getHunger() + food.getHealAmount() );
	    	    		if ( canEat && getHunger() + food.func_150905_g( stack ) <= MAX_HUNGER )
	    	    		{
	    	    			setHunger( getHunger() + food.func_150905_g( stack ) );
	    	    			setSaturation( getSaturation() + food.func_150906_h( stack ) );
	    	    			inv.decrStackSize( i, 1 );
	    	    		}
	    	    	}
    			}
    		}

    		if ( !worldObj.isRemote && hasSkill( Skill.INVENTORY_PICKUP.id ) )
    		{
	            final double R = 1;
	            List nearby = worldObj.getEntitiesWithinAABB( EntityItem.class, AxisAlignedBB.getBoundingBox( posX - R, posY - R, posZ - R, posX + R, posY + R, posZ + R ) );
	            for ( Object obj : nearby )
	            {
	            	EntityItem item = ( EntityItem ) obj;
	            	ItemStack stack = item.getEntityItem();
	            	if ( hasRoomForItem( stack ) <= 0 )
	            	{
	            		continue;
	            	}
	            	
	            	useHunger( 0.002f );
	            	
	            	for ( int i = 3; i < inv.getSizeInventory() && stack.stackSize > 0; ++i )
	            	{
	            		ItemStack slot = inv.getStackInSlot( i );
	            		if ( slot == null )
	            		{
	            			inv.setInventorySlotContents( i, stack.copy() );
	            			stack.stackSize = 0;
	            		}
	            		else if ( stack.getItem() == slot.getItem() && stack.getItemDamage() == slot.getItemDamage() && ItemStack.areItemStackTagsEqual( stack, slot ) )
	            		{
	            			int amt = Math.min( stack.stackSize, slot.getMaxStackSize() - slot.stackSize );
	            			amt = Math.max( amt, 0 ); // Not sure if I need this... Just in case.
	            			slot.stackSize += amt;
	                    	stack.stackSize -= amt;
	            		}
	            	}
	            	
	            	item.setEntityItemStack( stack );
	            	if ( stack.stackSize <= 0 )
	            	{
	            		worldObj.removeEntity( item );
	            	}
	            }
	    	}
    	}
    }
    
    /*
    @Override
    public void setDead()
    {
    }
    */
	
	@Override
    public boolean attackEntityFrom( DamageSource source, float damage )
    {
		if ( source.getEntity() == getOwner() )
		{
			return false;
		}
		
        Entity entity = source.getEntity();
        if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
        {
        	damage = (damage + 1.0F) / 2.0F;
        }
        
        float percent = 1.f;
        for ( Skill skill : Skill.skills.values() )
        {
        	if ( !hasSkill( skill.id ) || !( skill instanceof DefenseSkill ) )
        	{
        		continue;
        	}
        	DefenseSkill defense = ( DefenseSkill ) skill;
        	
        	if ( defense.types == null )
        	{
        		percent -= defense.percent;
        	}
        	else
        	{
        		boolean found = false;
        		for ( String str : defense.types )
        		{
        			if ( str.equals( source.damageType ) )
        			{
        				found = true;
        				break;
        			}
        		}
        		
        		if ( found )
        		{
        			percent -= defense.percent;
        		}
        	}
        }
        
        damage *= Math.max( percent, 0.f );
        if ( damage <= 0.f )
        {
        	return false;
        }
        
        useHunger( 0.01f );
        
        aiSit.setSitting( false );
        setSitting( false );

        return super.attackEntityFrom(source, damage);
    }
	
	@Override
    public boolean shouldDismountInWater( Entity rider )
	{
        return super.shouldDismountInWater( rider ) && !hasSkill( Skill.TRAVEL_SWIMMING.id );
    }
	
    // EntityLivingBase, EntityLiving
    @Override
    protected boolean canDespawn()
    {
    	return false;
    }

    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    @Override
    protected String getLivingSound()
    {
        return type.getLivingSound();
    }
    
    @Override
    protected String getHurtSound()
    {
        return "damage.hit";//type.getHurtSound();
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }
    
    @Override
    public String getCommandSenderName() // getEntityName() ?
    {
        return hasCustomNameTag() ? getCustomNameTag() : ( "entity.pet." + type.name );
    }
    
    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();

        getEntityAttribute( SharedMonsterAttributes.maxHealth ).setBaseValue( 20 );
        getEntityAttribute( SharedMonsterAttributes.movementSpeed ).setBaseValue( 0.3 );
    }
    
    @Override
    public void onDeath( DamageSource source )
    {
    }
    
    @Override
    public boolean isEntityAlive()
    {
    	return true;
    }
    
    @Override
    protected boolean isMovementBlocked()
    {
        return isSitting();
    }
    
    @Override
    public boolean interact( EntityPlayer player )
    {
    	if ( player.isSneaking() )
    	{
    		if ( player.getUniqueID().equals( UUID.fromString( func_152113_b() ) ) )
    		{
	    		UsefulPets.proxy.setPendingPetForGui( ( worldObj.isRemote ? Side.CLIENT : Side.SERVER ), this );
	    		player.openGui( UsefulPets.instance, UsefulPets.PET_INVENTORY_GUI_ID, worldObj, 0, 0, 0 );
    		}
    		else
    		{
    			TranslateUtils.chat( player, "chat.pet.notYours", "{TODO}" );
    		}
    		return false;
    	}
    	
    	if ( worldObj.isRemote )
    	{
    		return false;
    	}
    	
    	ItemStack held = player.getHeldItem();
    	if ( held != null && held.getItem() instanceof ItemFood )
    	{
    		ItemFood food = ( ItemFood ) held.getItem();
    		
    		boolean canEat = false;
    		for ( int id : skills )
    		{
    			Skill skill = Skill.forId( id );
    			if ( !( skill instanceof FoodSkill ) )
    			{
    				continue;
    			}
    			FoodSkill foodSkill = ( FoodSkill ) skill;
    			
    			if ( foodSkill.type.doesMatch( type, held ) )
    			{
    				canEat = true;
    				break;
    			}
    		}
    		
    		if ( canEat )
    		{
    			setHunger( getHunger() + food.func_150905_g( held ) );
    			setSaturation( getSaturation() + food.func_150906_h( held ) );
    			
    			if ( !player.capabilities.isCreativeMode )
    			{
	    			held.stackSize -= 1;
	    			if ( held.stackSize <= 0 )
	    			{
	                    player.inventory.setInventorySlotContents( player.inventory.currentItem, null );
	    			}
    			}
        		return true;
    		}
    	}
    	else if ( held != null && held.getItem() instanceof PetWandItem )
    	{
			player.addChatMessage( new ChatComponentTranslation( "chat.pet.help.level" ) );
			player.addChatMessage( new ChatComponentTranslation( "chat.pet.help.inv" ) );
    	}
    	else if ( held == null && hasSkill( Skill.TRAVEL_MOUNTABLE.id ) && hasSaddle() )
    	{
    		if ( player.getUniqueID().equals( UUID.fromString( func_152113_b() ) ) )
    		{
    			player.mountEntity( this );
    		}
    		else
    		{
    			player.addChatMessage( new ChatComponentTranslation( "chat.pet.notYours", "{TODO}" ) );
    		}
    	}
    	else
    	{
    		if ( player.getUniqueID().equals( UUID.fromString( func_152113_b() ) ) )
    		{
    			setSitting( !isSitting() );
    		}
    		else
    		{
    			player.addChatMessage( new ChatComponentTranslation( "chat.pet.notYours", "{TODO}" ) );
    		}
    	}
    	
    	return false;
    }

    @Override
    public boolean attackEntityAsMob( Entity entity )
    {
    	int damage = 0;
    	for ( int id : skills )
    	{
    		Skill skill = Skill.forId( id );
    		if ( !( skill instanceof AttackSkill ) )
    		{
    			continue;
    		}
    		AttackSkill attack = ( AttackSkill ) skill;
    		
    		damage += attack.damage;
    	}
    	
    	if ( hasSkill( Skill.INVENTORY_WEAPON.id ) && inv.getStackInSlot( 2 ) != null )
    	{
    		ItemStack stack = inv.getStackInSlot( 2 );
    		if ( stack.getItem() instanceof ClawItem )
    		{
    			ClawItem claw = ( ClawItem ) stack.getItem();
    			damage += claw.damage;
    		}
    	}
    	
        return entity.attackEntityFrom( DamageSource.causeMobDamage( this ), (float) damage );
    }
    
    @Override
    public int getTotalArmorValue()
    {
    	if ( !hasSkill( Skill.INVENTORY_ARMOR.id ) || inv.getStackInSlot( 1 ) == null )
    	{
    		return 0;
    	}
    	
    	ItemStack stack = inv.getStackInSlot( 1 );
    	if ( stack.getItem() == Items.iron_horse_armor )
    	{
    		return 5;
    	}
    	else if ( stack.getItem() == Items.golden_horse_armor )
    	{
    		return 7;
    	}
    	else if ( stack.getItem() == Items.diamond_horse_armor )
    	{
    		return 11;
    	}
    	
    	return 0;
    }
    
    // Copied from EntityHorse
    public void setJumpingState( int par1 )
    {
    	useHunger( 0.025f );
        //if (this.func_110257_ck())
        {
            if (par1 < 0)
            {
                par1 = 0;
            }
            else
            {
               // this.field_110294_bI = true;
               // this.func_110220_cK();
            }

            if (par1 >= 90)
            {
                this.field_110277_bt = 1.0F;
            }
            else
            {
                this.field_110277_bt = 0.4F + 0.4F * (float)par1 / 90.0F;
            }
        }
    }
    
    // And this
    @Override
    public void moveEntityWithHeading(float par1, float par2)
    {
        if (this.riddenByEntity != null && hasSkill( Skill.TRAVEL_MOUNTABLE.id ) && hasSaddle() && !isSitting()/* && this.func_110257_ck()*/)
        {
            this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
            this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            par1 = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
            par2 = ((EntityLivingBase)this.riddenByEntity).moveForward;
            
            if (par2 <= 0.0F)
            {
                par2 *= 0.25F;
                //this.field_110285_bP = 0;
            }
            
            if ( hasSkill( Skill.TRAVEL_SWIMCONTROL.id ) && ( isInsideOfMaterial( Material.water ) || isInsideOfMaterial( Material.lava ) ) )
            {
            	if ( riddenByEntity.rotationPitch > 15 )
            	{
            		motionY = -0.1f;
            	}
            	else if ( riddenByEntity.rotationPitch < -15 )
            	{
            		motionY = 0.1f;
            	}
            }

            // Jumping code?
            //*
            /*
            if (this.onGround && this.field_110277_bt == 0.0F&&false&& this.func_110209_cd() && !this.field_110294_bI)
            {
                par1 = 0.0F;
                par2 = 0.0F;
            }
            */

            if ( hasSkill( Skill.TRAVEL_MOUNTJUMP.id ) && this.field_110277_bt > 0.0F && !wasInAir && this.onGround)
            {
                this.motionY = 0.85f * (double)this.field_110277_bt;

                if (this.isPotionActive(Potion.jump))
                {
                    this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                }

                wasInAir=true;
                //this.func_110255_k(true);
                this.isAirBorne = true;

                if (par2 > 0.0F)
                {
                    float f2 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
                    float f3 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
                    this.motionX += (double)(-0.4F * f2 * this.field_110277_bt);
                    this.motionZ += (double)(0.4F * f3 * this.field_110277_bt);
                    this.playSound("mob.horse.jump", 0.4F, 1.0F);
                }

                this.field_110277_bt = 0.0F;
            }
            //*/

            this.stepHeight = 1.0F;
            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

            if (!this.worldObj.isRemote)
            {
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue() / 2);
                super.moveEntityWithHeading(par1, par2);
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            }

            if (this.onGround)
            {
                this.field_110277_bt = 0.0F;
                //this.func_110255_k(false);
                wasInAir=false;
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d0 = this.posX - this.prevPosX;
            double d1 = this.posZ - this.prevPosZ;
            float f4 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

            if (f4 > 1.0F)
            {
                f4 = 1.0F;
            }

            this.prevLimbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
        else
        {
            this.stepHeight = 0.5F;
            this.jumpMovementFactor = 0.02F;
            /*if ( type.equals( PetType.SLIME ) )
            {
            	// I'm making this up since I can't find where exactly the vertical motion is
            	if ( onGround )
            	{
            		//worldObj.playSoundAtEntity( this, "mob.slime.small", 0.4f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            		isJumping = true;
            		motionY = -1;
            		moveForward = par1;
            	}
            	else
            	{
            		isJumping = false;
            	}
            }
            else*/
            {
            	super.moveEntityWithHeading(par1, par2);
            }
        }
    }
    private float field_110277_bt = 0;
    private boolean wasInAir=false;

	// EntityAnimal
	@Override
	public EntityAgeable createChild( EntityAgeable entity )
	{
		// TODO
		return null;
	}
	
	// EntityOwnable (+some)
	
	@Override
    public String func_152113_b() // getOwnerUUID
	{
		return ownerId;
	}
	
	public void setOwnerUUID( String id )
	{
		ownerId = id;
		if ( !worldObj.isRemote )
		{
			dataWatcher.updateObject( DATA_OWNER, ownerId );
		}
	}
	
	// We NEED to convert this to GameProfile stuff later
	/*
	@Override
	public String getOwnerName()
	{
		return ownerName;
	}
	
	public void setOwnerName( String theOwnerName )
	{
		ownerName = theOwnerName;
		dataWatcher.updateObject( DATA_OWNER, ownerName );
	}*/

	@Override
	public Entity getOwner()
	{
		if ( ownerId == null || ownerId.length() == 0 ) return null;
        return worldObj.func_152378_a( UUID.fromString( ownerId ) );
	}
	
	// Variables
	// Pet info
	private String ownerId = null;
	private PetType type = PetType.CAT;
	private List< Integer > skills = new ArrayList< Integer >();
	private PetInventory inv = new PetInventory( this );
	
	// State stuff
	private float saturation;
	private int regenTicks = 0;
	private int syncTimer = 20;
	
	// AI stuff
	private SitAI aiSit = new SitAI( this );
	
	public static final int DATA_OWNER = 20;
	public static final int DATA_TYPE = 21;
	public static final int DATA_SITTING = 22;
	public static final int DATA_HUNGER = 23;
	public static final int DATA_LEVEL = 24;
	public static final int DATA_FREE_POINTS = 25;
	public static final int DATA_SKILLS = 26;
	public static final int DATA_TEXTURE = 27;
	public static final int DATA_DISPLAY_FLAGS = 28;
	
	public static final float MAX_HUNGER = 20;
	public static final int FLAG_SADDLE = 1 << 0;
}
