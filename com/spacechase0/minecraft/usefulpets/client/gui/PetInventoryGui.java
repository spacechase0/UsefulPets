package com.spacechase0.minecraft.usefulpets.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.spacechase0.minecraft.spacecore.util.ClientUtils;
import com.spacechase0.minecraft.spacecore.util.TranslateUtils;
import com.spacechase0.minecraft.usefulpets.UsefulPets;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.inventory.PetInventoryContainer;
import com.spacechase0.minecraft.usefulpets.network.ChangeTexturePacket;
import com.spacechase0.minecraft.usefulpets.pet.PetType;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class PetInventoryGui extends GuiContainer
{
    private static final ResourceLocation field_110414_t = new ResourceLocation("usefulpets:textures/gui/pet.png");
    private IInventory field_110413_u;
    private IInventory field_110412_v;
    private PetEntity pet;
    private float mouseX;
    private float mouseY;

    public PetInventoryGui(InventoryPlayer par1IInventory, IInventory par2IInventory, PetEntity thePet)
    {
        super(new PetInventoryContainer(par1IInventory, par2IInventory, thePet));
        this.field_110413_u = par1IInventory;
        this.field_110412_v = par2IInventory;
        this.pet = thePet;
        xSize += 18 * 3;
        //this.allowUserInput = false;
    }
    
    @Override
    public void initGui()
    {
    	super.initGui();
    	
    	buttonList.clear();
    	buttonList.add(             new GuiButton( SKILLS_BUTTON_ID,  guiLeft + 8, guiTop + 39, 48, 20, TranslateUtils.translate( "gui.pet.skills" ) ) );
    	
    	buttonList.add( texButton = new GuiButton( TEXTURE_BUTTON_ID, guiLeft + 8, guiTop - 28, xSize - 16, 20, "<...>" ) );
    	updateTextureButton( true );
    }
    
    @Override
    public void actionPerformed( GuiButton button )
    {
    	if ( button.id == SKILLS_BUTTON_ID )
    	{
    		mc.thePlayer.closeScreen();
    		mc.thePlayer.openGui( UsefulPets.instance, UsefulPets.PET_SKILLS_GUI_ID, mc.thePlayer.worldObj, 0, 0, 0 );
    	}
    	else if ( button.id == TEXTURE_BUTTON_ID )
    	{
    		PetType type = pet.getPetType();
    		if ( ++currTexIndex >= type.textures.size() )
    		{
    			currTexIndex = 0;
    		}
    		
    		String tex = type.textures.get( currTexIndex );
    		pet.setTexture( type.textures.get( currTexIndex ) );
    		UsefulPets.network.sendToServer( new ChangeTexturePacket( pet.getEntityId(), tex ) );
        	
        	updateTextureButton( false );
    	}
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
    	ClientUtils.drawString(this.field_110412_v.hasCustomInventoryName() ? this.field_110412_v.getInventoryName() : I18n.format(this.field_110412_v.getInventoryName()), 8, 6, 4210752);
    	ClientUtils.drawString(this.field_110413_u.hasCustomInventoryName() ? this.field_110413_u.getInventoryName() : I18n.format(this.field_110413_u.getInventoryName()), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110414_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        
        if ( pet.hasSkill( Skill.INVENTORY_ARMOR.id ) )
        {
            this.drawTexturedModalRect(k + 7 + 18, l + 17, 0, 220, 18, 18);
        }
        if ( pet.hasSkill( Skill.TRAVEL_MOUNTABLE.id ) )
        {
            this.drawTexturedModalRect(k + 7, l + 17, 18, 220, 18, 18);
        }
        if ( pet.hasSkill( Skill.INVENTORY_WEAPON.id ) )
        {
            this.drawTexturedModalRect(k + 7 + 36, l + 17, 36, 220, 18, 18);
        }
        
        if ( pet.hasSkill( Skill.INVENTORY.id ) )
        {
        	int count = ( pet.getInventory().getSizeInventory() - 3 ) / 3;
        	
        	int rows = Math.min( count, 3 );
        	drawTexturedModalRect( k + 115, l + 17, 0, 166, 54, 18 * rows );
        	
        	int cols = Math.max( count - 3, 0 );
        	drawTexturedModalRect( k + 115 + 18 * 3, l + 17, 18 * 3, 166, 18 * cols, 54 );
        }

        GuiInventory.func_147046_a(k + 88, l + 60, 17, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.pet);
    }

    @Override
    public void drawScreen(int mx, int my, float par3)
    {
        this.mouseX = (float)mx;
        this.mouseY = (float)my;
        super.drawScreen(mx, my, par3);
    }
    
    private void updateTextureButton( boolean initial )
    {
    	if ( initial )
    	{
	    	int tex = pet.getPetType().textures.indexOf( pet.getTexture() );
	    	if ( tex == -1 )
	    	{
	    		tex = 0;
	    	}
	    	currTexIndex = tex;
    	}
    	
    	String full = pet.getTexture();
    	String file = full.substring( full.lastIndexOf( '/' ) + 1 );
    	String filename = file.substring( 0, file.lastIndexOf( '.' ) );
    	
    	texButton.displayString = TranslateUtils.translate( TranslateUtils.translate( "pet.texture" ), TranslateUtils.translate( "pet.texture." + pet.getPetType().name + "." + filename ) );
    }
    
    private int currTexIndex = 0;
    private GuiButton texButton;
    
    private static final int SKILLS_BUTTON_ID = 0;
    private static final int TEXTURE_BUTTON_ID = 1;
}
