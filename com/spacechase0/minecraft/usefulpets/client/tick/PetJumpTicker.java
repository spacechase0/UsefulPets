package com.spacechase0.minecraft.usefulpets.client.tick;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class PetJumpTicker
{
	@SubscribeEvent
	public void tick( TickEvent.ClientTickEvent event )
	{
		/*
		if ( !type.contains( TickType.CLIENT ) )
		{
			return;
		}
		//*/
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.thePlayer;
		
		if ( player == null || player.movementInput == null )
		{
			return;
		}
		
		// Lazy hack - hope this makes it work outside MCP
		try
		{
			// TODO SEVERE FIXME BUG ETC.
			// Check each update to make sure it works :P
			
			Class c = EntityPlayerSP.class;
			for ( int i = 0; i < c.getDeclaredFields().length; ++i )
			{
				c.getDeclaredFields()[ i ].setAccessible( true );
			}
			for ( int i = 0; i < c.getDeclaredMethods().length; ++i )
			{
				c.getDeclaredMethods()[ i ].setAccessible( true );
			}
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
		}
		
		// Copied from EntityPlayerSP.onLivingUpdate
        boolean flag = player.movementInput.jump;
        if ( player.ridingEntity instanceof PetEntity )
        {
            if (jumpPowerCounter < 0)
            {
                ++jumpPowerCounter;

                if (jumpPowerCounter == 0)
                {
                	jumpPower = 0.0F;
                }
            }

            if (prevJump && !player.movementInput.jump)
            {
                jumpPowerCounter = -10;
                // Originally func_110318_g
                ( ( EntityClientPlayerMP ) player ).sendQueue.addToSendQueue(new C0BPacketEntityAction(player, 6, (int)(jumpPower * 100.0F)));
            }
            else if (!prevJump && player.movementInput.jump)
            {
            	jumpPowerCounter = 0;
            	jumpPower = 0.0F;
            }
            else if (prevJump)
            {
                ++jumpPowerCounter;

                if (jumpPowerCounter < 10)
                {
                	jumpPower = (float)jumpPowerCounter * 0.1F;
                }
                else
                {
                	jumpPower = 0.8F + 2.0F / (float)(jumpPowerCounter - 9) * 0.1F;
                }
            }
        }
        
        prevJump = player.movementInput.jump;
	}
	
	private float jumpPower;
	private int jumpPowerCounter;
	
	private boolean prevJump = false;
}
