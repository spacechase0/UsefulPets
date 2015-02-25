package com.spacechase0.minecraft.usefulpets.client.gui;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.spacechase0.minecraft.spacecore.util.ClientUtils;
import com.spacechase0.minecraft.spacecore.util.TranslateUtils;
import com.spacechase0.minecraft.usefulpets.PetTrackingDataHandler.PetData;

public class PetTrackingGui extends GuiScreen
{
    @Override
    public void initGui()
    {
        this.buttonList.clear();
    }
    
    @Override
    public void drawScreen( int mx, int my, float par3 )
    {
    	drawDefaultBackground();
    	
    	String str = TranslateUtils.translate( "gui.pet.tracking" );
    	
    	GL11.glPushMatrix();
    	GL11.glTranslatef( width / 2 - fontRendererObj.getStringWidth( str ), 10, 0 );
    	GL11.glScalef( 2.f, 2.f, 2.f );
    	ClientUtils.drawString( str, 0, 0, WHITE );
    	GL11.glPopMatrix();
    	
    	if ( data == null )
    	{
    		return;
    	}
    	
    	Iterator< Entry< UUID, PetData > > it = data.entrySet().iterator();
    	for ( int i = 0; it.hasNext(); ++i )
    	{
    		Entry< UUID, PetData > entry = it.next();
    		
    		UUID id = entry.getKey();
    		PetData pet = entry.getValue();
    		
    		int y = 50 + ( i * 16 );
    		
    		ClientUtils.drawString( TranslateUtils.translate( pet.name ), 50, y, WHITE );
    		ClientUtils.drawString( TranslateUtils.translate( "gui.pet.tracking.level", pet.level ), 130, y, WHITE );
    		ClientUtils.drawString( TranslateUtils.translate( "gui.pet.tracking.dim", pet.dim ), 200, y, WHITE );
    		ClientUtils.drawString( TranslateUtils.translate( "gui.pet.tracking.pos", pet.pos.x, pet.pos.y, pet.pos.z ), 275, y, WHITE );
    	}
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    public Map< UUID, PetData > data;
    private static final int WHITE = 0xFFFFFFFF;
}
