package com.spacechase0.minecraft.usefulpets.inventory;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SaddleSlot extends Slot
{
    public SaddleSlot( PetInventoryContainer theContainer, IInventory inv, int par3, int par4, int par5 )
    {
        super( inv, par3, par4, par5 );
        container = theContainer;
    }

    @Override
    public boolean isItemValid( ItemStack stack )
    {
        return super.isItemValid( stack ) && stack.getItem() == Items.saddle && !this.getHasStack();
    }
    
    final PetInventoryContainer container;
}
