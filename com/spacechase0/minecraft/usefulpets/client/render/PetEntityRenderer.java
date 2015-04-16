package com.spacechase0.minecraft.usefulpets.client.render;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.spacechase0.minecraft.spacecore.util.ClientUtils;
import com.spacechase0.minecraft.usefulpets.UsefulPets;
import com.spacechase0.minecraft.usefulpets.client.model.CatModel;
import com.spacechase0.minecraft.usefulpets.client.model.DogModel;
import com.spacechase0.minecraft.usefulpets.client.model.MagmaCubeModel;
import com.spacechase0.minecraft.usefulpets.client.model.PigModel;
import com.spacechase0.minecraft.usefulpets.client.model.SilverfishModel;
import com.spacechase0.minecraft.usefulpets.client.model.SlimeModel;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.pet.PetType;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

public class PetEntityRenderer extends RenderLiving
{
	public PetEntityRenderer()
	{
		super( new CatModel(), 0.45f );
	}

	@Override
    public void doRender( Entity entity, double par2, double par4, double par6, float par8, float par9 )
    {
		PetEntity pet = ( PetEntity ) entity;
		if ( pet.getPetType().equals( PetType.CAT ) )
		{
			mainModel = renderPassModel = catModel;
		}
		else if ( pet.getPetType().equals( PetType.DOG ) )
		{
			mainModel = renderPassModel = dogModel;
		}
		else if ( pet.getPetType().equals( PetType.PIG ) )
		{
			mainModel = renderPassModel = pigModel;
		}
		else if ( pet.getPetType().equals( PetType.SLIME ) )
		{
			mainModel = renderPassModel = slimeModel;
			if ( pet.getTexture().contains( "magma" ) )
			{
				mainModel = renderPassModel = magmaCubeModel;
			}
		}
		else if ( pet.getPetType().equals( PetType.SILVERFISH ) )
		{
			mainModel = renderPassModel = silverfishModel;
		}
		
		Minecraft.getMinecraft().renderEngine.bindTexture( getEntityTexture( entity ) );
        doRender( ( EntityLiving ) entity, par2, par4, par6, par8, par9 );
		
        // TODO: Render armor
        
        if ( hasSaddle( pet ) )
        {
        	ClientUtils.bindTexture( "usefulpets:textures/entity/" + pet.getPetType().name + "/saddle.png" );
            doRender( ( EntityLiving ) entity, par2, par4, par6, par8, par9 );
        }
        
        /*
        Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation( "usefulpets:textures/entity/" + pet.getPetType().name + "/armorDiamond.png" ) );
        doRenderLiving( ( EntityLiving ) entity, par2, par4, par6, par8, par9 );
        //*/
        
    }
	
	@Override
	protected ResourceLocation getEntityTexture( Entity entity )
	{
		PetEntity pet = ( PetEntity ) entity;
		return new ResourceLocation( pet.getTexture() );
	}
	
	@Override
    protected void bindEntityTexture(Entity par1Entity)
    {
        //this.bindTexture(this.func_110775_a(par1Entity));
    }
	
	@Override
    protected void passSpecialRender(EntityLivingBase entity, double x, double y, double z)
    {
		PetEntity pet = ( PetEntity ) entity;
		int health = ( int ) pet.getHealth();
		int maxHealth = ( int ) pet.getMaxHealth();
		
		if ( hasSaddle( pet ) )
		{
			justRendered = !justRendered;
			if ( !justRendered )
			{
				return;
			}
		}

		if ( !UsefulPets.instance.config.get( "general", "fancyStatRender", true ).getBoolean( true ) )
		{
			String hp = "HP: " + health + "/" + maxHealth;
			String food = "Food: " + ( ( int ) pet.getHunger() ) + "/" + ( ( int ) PetEntity.MAX_HUNGER );
			
			// renderLivingLabel ?
			func_147906_a( entity, hp, x, y, z, 4 );
			func_147906_a( entity, food, x, y - 0.3f, z, 4 );
		}
		else
		{
			double dist = entity.getDistanceToEntity( renderManager.livingPlayer );
			if ( dist > 4.f )
			{
				return;
			}
			
			glPushMatrix();
			{
				glTranslated( x, y + entity.height + 0.25f, z );
				glNormal3f( 0.f, 1.f, 0.f );
				glRotatef( -renderManager.playerViewY, 0.f, 1.f, 0.f );
				glRotatef( renderManager.playerViewX, 1.f, 0.f, 0.f );
				//glScalef( 1.f, 1.f, 1.f );
				glDisable( GL_LIGHTING );
				glDepthMask( false );
				glDisable( GL_DEPTH_TEST );
				glEnable( GL_BLEND );
	            glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
	            glDisable( GL_TEXTURE_2D );
	            
	            drawBar( 1.f, 1.f, 0.5f, 0.f, 0.f );
	            drawBar( 1.f, health / ( float ) maxHealth, 1.f, 0.f, 0.f );
	            glTranslatef( 0.f, -0.05f, 0.f );
	            drawBar( 1.f, 1.f, 0.f, 0.25f, 0.f );
	            drawBar( 1.f, pet.getHunger() / PetEntity.MAX_HUNGER, 0.f, 1.f, 0.f );
	            
	            glEnable( GL_TEXTURE_2D );
	            glDepthMask( true );
	            glEnable( GL_DEPTH_TEST );
	            glEnable( GL_LIGHTING );
	            glDisable( GL_BLEND );
	            glColor3f( 1.f, 1.f, 1.f );
			}
			glPopMatrix();
			
			EntityLiving living = ( EntityLiving ) entity;
			if ( living.hasCustomNameTag() )
			{
				func_147906_a( entity, living.getCustomNameTag(), x, y, z, 4 );
			}
		}
    }
	
	private boolean hasSaddle( PetEntity pet )
	{
        ItemStack saddleStack = pet.getInventory().getStackInSlot( 0 );
        if ( pet.hasSkill( Skill.TRAVEL_MOUNTABLE.id ) && saddleStack != null && saddleStack.getItem() == Items.saddle )
        {
        	return true;
        }
        
        if ( ( pet.getDisplayFlags() & PetEntity.FLAG_SADDLE ) != 0 )
        {
        	return true;
        }
        
        return false;
	}
	
	private void drawBar( float width, float percent, float r, float g, float b )
	{
		float halfWidth = width / 2;
		float amount = width * percent;
		
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setColorOpaque_F( r, g, b );
        tess.addVertex( halfWidth, -0.05f, 0.f );
        tess.addVertex( halfWidth - amount, -0.05f, 0.f );
        tess.addVertex( halfWidth - amount, 0.f, 0.f );
        tess.addVertex( halfWidth, 0.f, 0.f );
        tess.draw();
	}
	
	private final ModelBase catModel = new CatModel();
	private final ModelBase dogModel = new DogModel();
	private final ModelBase pigModel = new PigModel();
	private final ModelBase slimeModel = new SlimeModel();
	private final ModelBase magmaCubeModel = new MagmaCubeModel();
	private final ModelBase silverfishModel = new SilverfishModel();
	//private final ResourceLocation catTex = new ResourceLocation( "textures/entity/cat/red.png" );
	//private final ResourceLocation dogTex = new ResourceLocation( "textures/entity/wolf/wolf_tame.png" );
	
	private boolean justRendered = false;
}
