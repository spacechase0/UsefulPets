package com.spacechase0.minecraft.usefulpets.item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.spacechase0.minecraft.spacecore.util.TranslateUtils;
import com.spacechase0.minecraft.usefulpets.UsefulPets;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.Level;
import com.spacechase0.minecraft.usefulpets.pet.PetType;

public class PetWandItem extends Item
{
	public PetWandItem()
	{
		super();
		setUnlocalizedName( "petWand" );
		setCreativeTab( CreativeTabs.tabTools );
		setMaxStackSize( 1 );
	}
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		itemIcon = register.registerIcon( "usefulpets:converter" );
	}
	
	@Override
    public boolean onItemUse( ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int blockSide, float par8, float par9, float par10)
    {
		player.openGui( UsefulPets.instance, UsefulPets.PET_TRACKING_GUI_ID, world, blockX, blockY, blockZ );
        return false;
    }
	
	@Override
    public boolean onLeftClickEntity( ItemStack stack, EntityPlayer player, Entity entity )
    {
		if ( player.worldObj.isRemote )
		{
			return true;
		}
		
		if ( entity instanceof PetEntity )
		{
			levelUp( player, ( PetEntity ) entity );
		}
		else if ( entity instanceof EntityLivingBase )
		{
			convertPet( player, ( EntityLivingBase ) entity );
		}
		
		return true;
    }
	
	@Override
	public void addInformation( ItemStack stack, EntityPlayer player, List list, boolean par4 )
	{
		list.add( TranslateUtils.translate( "item.petWand.tooltip.help.convert" ) );
		list.add( TranslateUtils.translate( "item.petWand.tooltip.help.track" ) );
		list.add( TranslateUtils.translate( "item.petWand.tooltip.help.more" ) );
	}
	
	private void convertPet( EntityPlayer player, EntityLivingBase entity )
	{
		EntityTameable tameable = null;
		if ( entity instanceof EntityTameable )
		{
			tameable = ( EntityTameable ) entity;
			if ( !tameable.isTamed() )
			{
				TranslateUtils.chat( player, "chat.pet.convert.notTamed" );
				return;
			}
			
			if ( !UUID.fromString( tameable.func_152113_b() ).equals( player.getUniqueID() ) )
			{
				TranslateUtils.chat( player, "chat.pet.convert.needOwnership" );
				return;
			}
		}
		else
		{
			
			if ( entity instanceof EntitySlime )
			{
				EntitySlime slime = ( EntitySlime ) entity;
				if ( slime.getSlimeSize() != 1 )
				{
					System.out.println(slime.getSlimeSize());
					TranslateUtils.chat( player, "chat.pet.convert.notSmallSlime" );
					return;
				}
			}
			if ( !entity.isPotionActive( Potion.weakness ) )
			{
				TranslateUtils.chat( player, "chat.pet.convert.weakness" );
				return;
			}
		}
		
		for ( PetType type : PetType.types.values() )
		{
			if ( !type.convertFrom.equals( entity.getClass() ) )
			{
				continue;
			}
			
			PetEntity pet = new PetEntity( entity.worldObj );
			pet.setPosition( entity.posX, entity.posY, entity.posZ );
			//pet.setOwnerName( entity.getOwnerName() );
			pet.setOwnerUUID( player.getGameProfile().getId().toString() );
			pet.setPetType( type );
			if ( tameable != null ) pet.setSitting( tameable.isSitting() );
			if ( entity instanceof EntityLiving )
			{
				EntityLiving living = ( EntityLiving ) entity;
				if ( living.getCustomNameTag() != null )
				{
					pet.setCustomNameTag( living.getCustomNameTag() );
				}
			}
			
			entity.worldObj.removeEntity( entity );
			entity.worldObj.spawnEntityInWorld( pet );
			
			TranslateUtils.chat( player, "chat.pet.convert.success" );
			return;
		}

		TranslateUtils.chat( player, "chat.pet.convert.failure" );
	}
	
	private void levelUp( EntityPlayer player, PetEntity pet )
	{
		if ( pet.getLevel() >= Level.MAX_LEVEL )
		{
			TranslateUtils.chat( player, "chat.pet.level.max" );
			return;
		}
		
		boolean someMissing = false;
		List< ItemStack > reqs = new ArrayList< ItemStack >();
		int reqLevel = 0;
		if ( !player.capabilities.isCreativeMode )
		{
			reqs = Level.getLevelItemRequirements( pet.getLevel() + 1 );
			for ( ItemStack stack : reqs )
			{
				if ( !hasAmount( player.inventory, stack.getItem(), stack.stackSize ) )
				{
					someMissing = true;
					TranslateUtils.chat( player, "chat.pet.level.missing", stack.getDisplayName(), stack.stackSize );
				}
			}
			
			reqLevel = Level.getLevelExperienceRequirements( pet.getLevel() + 1 );
			if ( player.experienceLevel < reqLevel )
			{
				someMissing = true;
				TranslateUtils.chat( player, "chat.pet.level.missing", TranslateUtils.translate( "misc.level.name" ), reqLevel );
			}
		}
		
		if ( someMissing )
		{
			return;
		}
		
		if ( !player.capabilities.isCreativeMode )
		{
			player.addExperienceLevel( -reqLevel );
			for ( ItemStack stack : reqs )
			{
				takeAmount( player.inventory, stack.getItem(), stack.stackSize );
			}
		}
		
		pet.levelUp();
		TranslateUtils.chat( player, "chat.pet.level.success" );
	}
	
	private boolean hasAmount( IInventory inv, Item item, int reqAmount )
	{
		int amount = 0;
		for ( int is = 0; is < inv.getSizeInventory(); ++is )
		{
			ItemStack stack = inv.getStackInSlot( is );
			if ( stack != null && stack.getItem() == item )
			{
				amount += stack.stackSize;
			}
		}
		
		return ( amount >= reqAmount );
	}
	
	private void takeAmount( IInventory inv, Item item, int reqAmount )
	{
		int left = reqAmount;
		for ( int is = 0; is < inv.getSizeInventory() && left > 0; ++is )
		{
			ItemStack stack = inv.getStackInSlot( is );
			if ( stack != null && stack.getItem() == item )
			{
				left -= inv.decrStackSize( is, left ).stackSize;
			}
		}
	}
}
