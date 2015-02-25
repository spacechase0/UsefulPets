package com.spacechase0.minecraft.usefulpets.inventory;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class PetInventory implements IInventory
{
	public PetInventory( PetEntity thePet )
	{
		pet = thePet;
	}
	
	@Override
	public int getSizeInventory()
	{
		int base = 3;
		base += 3 * ( pet.hasSkill( Skill.INVENTORY.id ) ? 1 : 0 );
		base += 3 * ( pet.hasSkill( Skill.INVENTORY_UPGRADE1.id ) ? 1 : 0 );
		base += 3 * ( pet.hasSkill( Skill.INVENTORY_UPGRADE2.id ) ? 1 : 0 );
		base += 3 * ( pet.hasSkill( Skill.INVENTORY_UPGRADE3.id ) ? 1 : 0 );
		base += 3 * ( pet.hasSkill( Skill.INVENTORY_UPGRADE4.id ) ? 1 : 0 );
		
		return base;
	}

	@Override
	public ItemStack getStackInSlot( int slot )
	{
		return stacks[ slot ];
	}

	@Override
	public ItemStack decrStackSize( int slot, int amount )
	{
		// From InventoryBasic
        if (stacks[slot] != null)
        {
            ItemStack itemstack;

            if (stacks[slot].stackSize <= amount)
            {
                itemstack = stacks[slot];
                stacks[slot] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = stacks[slot].splitStack(amount);

                if (stacks[slot].stackSize == 0)
                {
                	stacks[slot] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing( int slot )
	{
		ItemStack tmp = getStackInSlot( slot );
		stacks[ slot ] = null;
		return tmp;
	}

	@Override
	public void setInventorySlotContents( int slot, ItemStack stack )
	{
		stacks[ slot ] = stack;
	}

	@Override
	public String getInventoryName()
	{
		return "gui.pet.inventory";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer player )
	{
		return ( player.getUniqueID().equals( UUID.fromString( pet.func_152113_b() ) ) );
	}

	@Override
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{
	}

	@Override
	public boolean isItemValidForSlot( int i, ItemStack stack )
	{
		// TODO
		return true;
	}
	
	private PetEntity pet;
	private ItemStack[] stacks = new ItemStack[ 18 ];
}
