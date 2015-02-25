package com.spacechase0.minecraft.usefulpets.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class PetInventoryContainer extends Container
{
    private IInventory field_111243_a;
    private PetEntity pet;

    public PetInventoryContainer(InventoryPlayer inv1, IInventory inv2, PetEntity thePet)
    {
        this.field_111243_a = inv2;
        this.pet = thePet;
        byte b0 = 3;
        inv2.openInventory();
        int i = (b0 - 4) * 18;
        int j;
        int k;
        //*
        if ( pet.hasSkill( Skill.TRAVEL_MOUNTABLE.id ) ) { this.addSlotToContainer(new SaddleSlot(this, inv2, 0, 8, 18)); didSaddle = true; }
        if ( pet.hasSkill( Skill.INVENTORY_ARMOR.id ) ) { this.addSlotToContainer(new PetArmorSlot(this, inv2, 1, 26, 18, thePet)); didArmor = true; }
        if ( pet.hasSkill( Skill.INVENTORY_WEAPON.id ) ) { this.addSlotToContainer(new PetWeaponSlot(this, inv2, 2, 44, 18, thePet)); didWeapon = true; }
        
        if ( pet.hasSkill( Skill.INVENTORY.id ) )
        {
        	invAmount = ( pet.getInventory().getSizeInventory() - 3 );
            for (j = 0; j < Math.min( invAmount / 3, 3 ); ++j)
            {
                for (k = 0; k < 3; ++k)
                {
                    this.addSlotToContainer(new Slot(inv2, 3 + j * 3 + k, 116 + k * 18, 18 + j * 18));
                }
            }
            for (j = 0; j < Math.max( invAmount / 3 - 3, 0 ); ++j)
            {
                for (k = 0; k < 3; ++k)
                {
                    this.addSlotToContainer(new Slot(inv2, 3 + 9 + j * 3 + k, 116 + 18 * 3 + j * 18, 18 + k * 18));
                }
            }
        }

        bindPlayerInventory( inv1 );
        //*/
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.field_111243_a.isUseableByPlayer(par1EntityPlayer) && this.pet.isEntityAlive() && this.pet.getDistanceToEntity(par1EntityPlayer) < 8.0F;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
    	return null;
    	
    	// USE THIS: ( InventoryPlayer.getHotbarSize() * 4 )
    	/*
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        // TODO
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < this.field_111243_a.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.field_111243_a.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (this.getSlot(2).isItemValid(itemstack1) && !this.getSlot(2).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 2, 3, false))
                {
                    return null;
                }
            }
            else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 1, 2, false))
                {
                    return null;
                }
            }
            else if (this.getSlot(0).isItemValid(itemstack1))
            {
                if (!this.mergeItemStack(itemstack1, 0, 1, false))
                {
                    return null;
                }
            }
            else if (this.field_111243_a.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.field_111243_a.getSizeInventory(), false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
        */
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);
    }

    protected void bindPlayerInventory( InventoryPlayer player )
    {
		for (int iy = 0; iy < 3; iy++)
		{
			for (int ix = 0; ix < InventoryPlayer.getHotbarSize(); ix++)
			{
				addSlotToContainer( new Slot( player, ix + iy * 9 + InventoryPlayer.getHotbarSize(), 8 + ix * 18, 84 + iy * 18 ) );
			}
		}
		
		for (int ix = 0; ix < InventoryPlayer.getHotbarSize(); ix++)
		{
			addSlotToContainer( new Slot( player, ix, 8 + ix * 18, 142 ) );
		}
	}
    
    private boolean didSaddle = false;
    private boolean didArmor = false;
    private boolean didWeapon = false;
    private int invAmount = 0;
}