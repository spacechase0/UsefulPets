package com.spacechase0.minecraft.usefulpets.client.model;

import net.minecraft.client.model.ModelPig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

public class PigModel extends ModelPig
{
	@Override
    public void setLivingAnimations( EntityLivingBase entity, float par2, float par3, float par4 )
	{
		super.setLivingAnimations( entity, par2, par3, par4 );
		
		PetEntity pet = ( PetEntity ) entity;
		if ( pet.isSitting() )
		{
			leg1.rotateAngleZ = 3.14f / 2;
			leg2.rotateAngleZ = -3.14f / 2;
			leg3.rotateAngleZ = 3.14f / 2;
			leg4.rotateAngleZ = -3.14f / 2;
		}
		else
		{
			leg1.rotateAngleZ = 0;
			leg2.rotateAngleZ = 0;
			leg3.rotateAngleZ = 0;
			leg4.rotateAngleZ = 0;
		}
	}
	
	@Override
    public void render( Entity entity, float par2, float par3, float par4, float par5, float par6, float par7 )
	{
		GL11.glPushMatrix();
		
		PetEntity pet = ( PetEntity ) entity;
		
		if ( pet.isSitting() )
		{
			GL11.glTranslatef( 0.f, 0.35f, 0.f );
		}
		super.render( entity, par2, par3, par4, par5, par6, par7 );
		
		GL11.glPopMatrix();
	}
}
