package com.spacechase0.minecraft.usefulpets.client.model;

import org.lwjgl.opengl.GL11;

import com.spacechase0.minecraft.usefulpets.entity.PetEntity;

import cpw.mods.fml.relauncher.ReflectionHelper;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;

public class SilverfishModel extends ModelSilverfish
{
	public SilverfishModel()
	{
		normal = new ModelSilverfish();
		
		body = ReflectionHelper.getPrivateValue( ModelSilverfish.class, this, 0 );
		wings = ReflectionHelper.getPrivateValue( ModelSilverfish.class, this, 1 );
        body[ 3 ].rotateAngleX = body[ 4 ].rotateAngleX = body[ 5 ].rotateAngleX = body[ 6 ].rotateAngleX = 3.14f / 2;
        body[ 3 ].rotationPointZ = body[ 4 ].rotationPointZ = body[ 5 ].rotationPointZ = body[ 6 ].rotationPointZ = 0;
        body[ 6 ].offsetY = 0;
        body[ 5 ].offsetY = -0.1f;
        body[ 4 ].rotateAngleX *= -0.7f;
        body[ 4 ].offsetZ = 0.06f;
        body[ 4 ].offsetY = -0.2f;
        body[ 3 ].rotateAngleX *= -0.45f;
        body[ 3 ].offsetY = -0.3f;
        body[ 3 ].offsetZ = -0.05f;
        wings[ 1 ].rotateAngleX = body[ 5 ].rotateAngleX*1.4f;
        wings[ 1 ].rotationPointZ = body[ 5 ].rotationPointZ;
        wings[ 1 ].offsetY = body[ 4 ].offsetX;
        wings[ 1 ].offsetZ = -0.05f;
	}
	
	@Override
    public void render( Entity entity, float par2, float par3, float par4, float par5, float par6, float par7 )
	{
		PetEntity pet = ( PetEntity ) entity;
		
		GL11.glPushMatrix();
		
		if ( pet.isSitting() )
		{
			GL11.glTranslatef( 0, 0, 0.2f );
	        GL11.glPushMatrix();
	        setRotationAngles( par2, par3, par4, par5, par6, par7, entity );
	        for ( int i = 3; i < body.length; ++i )
	        {
	            body[ i ].render( par7 );
	        }
	        wings[ 1 ].render( par7 );
	        GL11.glPopMatrix();

	        GL11.glPushMatrix();
	        GL11.glTranslatef( 0, -0.4f, -0.3f );
	        for ( int i = 0; i < 3; ++i )
	        {
	            body[ i ].render( par7 );
	        }
	        wings[ 0 ].render( par7 );
	        wings[ 2 ].render( par7 );
	        GL11.glPopMatrix();
		}
		else
		{
			GL11.glTranslatef( 0, 0, -0.2f );
			normal.render( entity, par2, par3, par4, par5, par6, par7 );
		}
		
		GL11.glPopMatrix();
	}
	
	protected ModelSilverfish normal;
	protected ModelRenderer[] body;
	protected ModelRenderer[] wings;
}
