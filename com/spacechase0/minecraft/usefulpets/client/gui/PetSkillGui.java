package com.spacechase0.minecraft.usefulpets.client.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.spacechase0.minecraft.spacecore.util.ClientUtils;
import com.spacechase0.minecraft.spacecore.util.TranslateUtils;
import com.spacechase0.minecraft.usefulpets.UsefulPets;
import com.spacechase0.minecraft.usefulpets.entity.PetEntity;
import com.spacechase0.minecraft.usefulpets.network.ChangeSkillPacket;
import com.spacechase0.minecraft.usefulpets.pet.skill.Skill;

// Based off of GuiAchievements
public class PetSkillGui extends GuiScreen
{
	public PetSkillGui( EntityPlayer thePlayer, PetEntity thePet )
	{
		super();
		
		player = thePlayer;
		pet = thePet;
	}
	
	private EntityPlayer player;
	private PetEntity pet;
	
	private int clicked = -1;
    
    @Override
    protected void mouseClicked(int x, int y, int button)
    {
    	clicked = button;
    }
	
	// Start GuiAchievement stuff
    private static final int guiMapTop = AchievementList.minDisplayColumn * 24 - 112;

    /** The left y coordinate of the achievement map */
    private static final int guiMapLeft = AchievementList.minDisplayRow * 24 - 112;

    /** The bottom x coordinate of the achievement map */
    private static final int guiMapBottom = AchievementList.maxDisplayColumn * 24 - 77;

    /** The right y coordinate of the achievement map */
    private static final int guiMapRight = AchievementList.maxDisplayRow * 24 - 77;
    private static final ResourceLocation field_110406_y = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    protected int achievementsPaneWidth = 256;
    protected int achievementsPaneHeight = 202;

    /** The current mouse x coordinate */
    protected int mouseX;

    /** The current mouse y coordinate */
    protected int mouseY;
    protected double field_74117_m;
    protected double field_74115_n;

    /** The x position of the achievement map */
    protected double guiMapX;

    /** The y position of the achievement map */
    protected double guiMapY;
    protected double field_74124_q;
    protected double field_74123_r;

    /** Whether the Mouse Button is down or not */
    private int isMouseButtonDown;
    private StatFileWriter statFileWriter;

    private int currentPage = -1;
    private GuiButton button; // Previously GuiSmallButton ?
    private LinkedList<Achievement> minecraftAchievements = new LinkedList<Achievement>();

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui()
    {
        this.buttonList.clear();
        //this.buttonList.add(new GuiSmallButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.getString("gui.done")));
        //this.buttonList.add(button = new GuiSmallButton(2, (width - achievementsPaneWidth) / 2 + 24, height / 2 + 74, 125, 20, AchievementPage.getTitle(currentPage)));
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
    	/*
        if (par1GuiButton.id == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }

        if (par1GuiButton.id == 2) 
        {
            currentPage++;
            if (currentPage >= AchievementPage.getAchievementPages().size())
            {
                currentPage = -1;
            }
            button.displayString = AchievementPage.getTitle(currentPage);
        }
        */

        super.actionPerformed(par1GuiButton);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        if (Mouse.isButtonDown(0))
        {
            int k = (this.width - this.achievementsPaneWidth) / 2;
            int l = (this.height - this.achievementsPaneHeight) / 2;
            int i1 = k + 8;
            int j1 = l + 17;

            if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && par1 >= i1 && par1 < i1 + 224 && par2 >= j1 && par2 < j1 + 155)
            {
                if (this.isMouseButtonDown == 0)
                {
                    this.isMouseButtonDown = 1;
                }
                else
                {
                    this.guiMapX -= (double)(par1 - this.mouseX);
                    this.guiMapY -= (double)(par2 - this.mouseY);
                    this.field_74124_q = this.field_74117_m = this.guiMapX;
                    this.field_74123_r = this.field_74115_n = this.guiMapY;
                }

                this.mouseX = par1;
                this.mouseY = par2;
            }

            if (this.field_74124_q < (double)guiMapTop)
            {
                this.field_74124_q = (double)guiMapTop;
            }

            if (this.field_74123_r < (double)guiMapLeft)
            {
                this.field_74123_r = (double)guiMapLeft;
            }

            if (this.field_74124_q >= (double)guiMapBottom)
            {
                this.field_74124_q = (double)(guiMapBottom - 1);
            }

            if (this.field_74123_r >= (double)guiMapRight)
            {
                this.field_74123_r = (double)(guiMapRight - 1);
            }
        }
        else
        {
            this.isMouseButtonDown = 0;
        }

        this.drawDefaultBackground();
        this.genAchievementBackground(par1, par2, par3);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.drawTitle();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen()
    {
        this.field_74117_m = this.guiMapX;
        this.field_74115_n = this.guiMapY;
        double d0 = this.field_74124_q - this.guiMapX;
        double d1 = this.field_74123_r - this.guiMapY;

        if (d0 * d0 + d1 * d1 < 4.0D)
        {
            this.guiMapX += d0;
            this.guiMapY += d1;
        }
        else
        {
            this.guiMapX += d0 * 0.85D;
            this.guiMapY += d1 * 0.85D;
        }
    }

    /**
     * Draws the "Achievements" title at the top of the GUI.
     */
    //@Override
    protected void drawTitle()
    {
        int i = (this.width - this.achievementsPaneWidth) / 2;
        int j = (this.height - this.achievementsPaneHeight) / 2;
        ClientUtils.drawString(TranslateUtils.translate("gui.pet.title"), i + 15, j + 5, 4210752);

        //this.buttonList.add(new GuiSmallButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.getString("gui.done")));
        //this.buttonList.add(button = new GuiSmallButton(2, (width - achievementsPaneWidth) / 2 + 24, height / 2 + 74, 125, 20, AchievementPage.getTitle(currentPage)));
        ClientUtils.drawString( TranslateUtils.translate("gui.pet.level") + " " + pet.getLevel(), (width - achievementsPaneWidth) / 2 + 24, height / 2 + 74 + 6, 4210752);
        ClientUtils.drawString( TranslateUtils.translate("gui.pet.freePoints") + ": " + pet.getFreeSkillPoints(), this.width / 2 + 24, this.height / 2 + 74 + 6, 4210752);
        //fontRendererObj.drawString( "id:"+pet.entityId, (width - achievementsPaneWidth) / 2 + 24, height / 2 + 74 + 16, 4210752);
    }

    protected void genAchievementBackground(int par1, int par2, float par3)
    {
        int k = MathHelper.floor_double(this.field_74117_m + (this.guiMapX - this.field_74117_m) * (double)par3);
        int l = MathHelper.floor_double(this.field_74115_n + (this.guiMapY - this.field_74115_n) * (double)par3);

        if (k < guiMapTop)
        {
            k = guiMapTop;
        }

        if (l < guiMapLeft)
        {
            l = guiMapLeft;
        }

        if (k >= guiMapBottom)
        {
            k = guiMapBottom - 1;
        }

        if (l >= guiMapRight)
        {
            l = guiMapRight - 1;
        }

        int i1 = (this.width - this.achievementsPaneWidth) / 2;
        int j1 = (this.height - this.achievementsPaneHeight) / 2;
        int k1 = i1 + 16;
        int l1 = j1 + 17;
        this.zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        int i2 = k + 288 >> 4;
        int j2 = l + 288 >> 4;
        int k2 = (k + 288) % 16;
        int l2 = (l + 288) % 16;
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        Random random = new Random();
        int i3;
        int j3;
        int k3;

        for (i3 = 0; i3 * 16 - l2 < 155; ++i3)
        {
            float f1 = 0.6F - (float)(j2 + i3) / 25.0F * 0.3F;
            GL11.glColor4f(f1, f1, f1, 1.0F);

            for (k3 = 0; k3 * 16 - k2 < 224; ++k3)
            {
                random.setSeed((long)(1234 + i2 + k3));
                random.nextInt();
                j3 = random.nextInt(1 + j2 + i3) + (j2 + i3) / 2;
                IIcon icon = Blocks.sand.getIcon(0, 0);

                if (j3 <= 37 && j2 + i3 != 35)
                {
                    if (j3 == 22)
                    {
                        if (random.nextInt(2) == 0)
                        {
                            icon = Blocks.diamond_ore.getIcon(0, 0);
                        }
                        else
                        {
                            icon = Blocks.redstone_ore.getIcon(0, 0);
                        }
                    }
                    else if (j3 == 10)
                    {
                        icon = Blocks.iron_ore.getIcon(0, 0);
                    }
                    else if (j3 == 8)
                    {
                        icon = Blocks.coal_ore.getIcon(0, 0);
                    }
                    else if (j3 > 4)
                    {
                        icon = Blocks.stone.getIcon(0, 0);
                    }
                    else if (j3 > 0)
                    {
                        icon = Blocks.dirt.getIcon(0, 0);
                    }
                }
                else
                {
                    icon = Blocks.bedrock.getIcon(0, 0);
                }

                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.drawTexturedModelRectFromIcon(k1 + k3 * 16 - k2, l1 + i3 * 16 - l2, icon, 16, 16);
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        int l3;
        int i4;
        int j4;
        

        List<Skill> achievementList = new ArrayList< Skill >( Skill.skills.values() );
        //List<Achievement> achievementList = (currentPage == -1 ? minecraftAchievements : AchievementPage.getAchievementPage(currentPage).getAchievements());
        for (i3 = 0; i3 < achievementList.size(); ++i3)
        {
            Skill skill = achievementList.get(i3);
            
            for ( int si = 0; skill.skillReqs != null && si < skill.skillReqs.length; ++si)
            {
            	Skill parent = Skill.forId( skill.skillReqs[ si ] );

                k3 = ( int )( skill.x * 24 - k + 11 + k1 );
                j3 = ( int )( skill.y * 24 - l + 11 + l1 );
                j4 = ( int )( parent.x * 24 - k + 11 + k1 );
                l3 = ( int )( parent.y * 24 - l + 11 + l1 );
                boolean flag5 = pet.hasSkill( skill.id );
                boolean flag6 = pet.hasSkillRequirements( skill.id );
                i4 = Math.sin((double)(Minecraft.getSystemTime() % 600L) / 600.0D * Math.PI * 2.0D) > 0.6D ? 255 : 130;
                int k4 = -16777216;

                if (flag5)
                {
                    k4 = -9408400;
                }
                else if (flag6)
                {
                    k4 = 65280 + (i4 << 24);
                }

                this.drawHorizontalLine(k3, j4, j3, k4);
                this.drawVerticalLine(j4, j3, l3, k4);
            }
        }

        Skill hoveredSkill = null;
        RenderItem renderitem = new RenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        int l4;
        int i5;

        for (k3 = 0; k3 < achievementList.size(); ++k3)
        {
        	Skill achievement2 = (Skill)achievementList.get(k3);
            j4 = ( int )( achievement2.x * 24 - k );
            l3 = ( int )( achievement2.y * 24 - l );

            if (j4 >= -24 && l3 >= -24 && j4 <= 224 && l3 <= 155)
            {
                float f2;

                if (pet.hasSkill(achievement2.id))
                {
                    f2 = 1.0F;
                    GL11.glColor4f(f2, f2, f2, 1.0F);
                }
                else if (pet.hasSkillRequirements(achievement2.id))
                {
                    f2 = Math.sin((double)(Minecraft.getSystemTime() % 600L) / 600.0D * Math.PI * 2.0D) < 0.6D ? 0.6F : 0.8F;
                    GL11.glColor4f(f2, f2, f2, 1.0F);
                }
                else
                {
                    f2 = 0.3F;
                    GL11.glColor4f(f2, f2, f2, 1.0F);
                }

                this.mc.getTextureManager().bindTexture(field_110406_y);
                i5 = k1 + j4;
                l4 = l1 + l3;

                GL11.glEnable(GL11.GL_ALPHA_TEST);
                if (achievement2.skillReqs == null)
                {
                    this.drawTexturedModalRect(i5 - 2, l4 - 2, 26, 202, 26, 26);
                }
                else
                {
                    this.drawTexturedModalRect(i5 - 2, l4 - 2, 0, 202, 26, 26);
                }

                if (!pet.hasSkillRequirements(achievement2.id) && !pet.hasSkill( achievement2.id ) )
                {
                    float f3 = 0.1F;
                    GL11.glColor4f(f3, f3, f3, 1.0F);
                    renderitem.renderWithColor = false;
                }

                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_CULL_FACE);
                //GL11.glEnable( GL11.GL_DEPTH_TEST );
                renderitem.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), achievement2.icon, i5 + 3, l4 + 3);
                GL11.glColor4f( 1.f, 1.f, 1.f, 1.f );
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_LIGHTING);

                if (!pet.hasSkillRequirements(achievement2.id) && !pet.hasSkill( achievement2.id ))
                {
                    renderitem.renderWithColor = true;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                if (par1 >= k1 && par2 >= l1 && par1 < k1 + 224 && par2 < l1 + 155 && par1 >= i5 && par1 <= i5 + 22 && par2 >= l4 && par2 <= l4 + 22)
                {
                    hoveredSkill = achievement2;
                }
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110406_y);
        this.drawTexturedModalRect(i1, j1, 0, 0, this.achievementsPaneWidth, this.achievementsPaneHeight);
        GL11.glPopMatrix();
        this.zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        super.drawScreen(par1, par2, par3);

        if (hoveredSkill != null)
        {
            String s = TranslateUtils.translate( "pet.skill." + hoveredSkill.name + ".name" );
            String s1 = TranslateUtils.translate( "pet.skill." + hoveredSkill.name + ".desc");
            j4 = par1 + 12;
            l3 = par2 - 4;

            if (pet.hasSkillRequirements(hoveredSkill.id) || pet.hasSkill( hoveredSkill.id ))
            {
                i5 = Math.max(this.fontRendererObj.getStringWidth(s), 120);
                l4 = this.fontRendererObj.splitStringWidth(s1, i5);

                if (pet.hasSkill(hoveredSkill.id))
                {
                    l4 += 12;
                }

                this.drawGradientRect(j4 - 3, l3 - 3, j4 + i5 + 3, l3 + l4 + 3 + 12, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(s1, j4, l3 + 12, i5, -6250336);

                if (pet.hasSkill(hoveredSkill.id))
                {
                    this.fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken"), j4, l3 + l4 + 4, -7302913);
                    if ( clicked == 1 )
                    {
                    	pet.removeSkill( hoveredSkill.id );
                    	UsefulPets.network.sendToServer( new ChangeSkillPacket( pet.getEntityId(), ChangeSkillPacket.ACTION_REMOVE, hoveredSkill.id ) );
                    }
                }
                else
                {
                	if ( clicked == 0 && pet.getFreeSkillPoints() > 0 )
                	{
                    	pet.addSkill( hoveredSkill.id );
                    	UsefulPets.network.sendToServer( new ChangeSkillPacket( pet.getEntityId(), ChangeSkillPacket.ACTION_ADD, hoveredSkill.id ) );
                	}
                }
            }
            else
            {
                i5 = fontRendererObj.getStringWidth(s);//Math.max(this.fontRendererObj.getStringWidth(s), 120);
                String s2 = new String();
                if ( pet.getLevel() < hoveredSkill.levelReq )
                {
                	String levelReq = StatCollector.translateToLocal( "gui.pet.levelReq" ) + " " + hoveredSkill.levelReq + "\n";
                	s2 += levelReq;
                }
                for ( int i = 0; hoveredSkill.skillReqs != null && i < hoveredSkill.skillReqs.length; ++i )
                {
                	int id = hoveredSkill.skillReqs[ i ];
                	if ( pet.hasSkill( id ) )
                	{
                		continue;
                	}
                	
                	String name = StatCollector.translateToLocal( "pet.skill." + Skill.forId( id ).name + ".name" );
                	s2 += StatCollector.translateToLocal( "gui.pet.skillReq" ) + " " + name + "\n";
                }
                i5 = Math.max( i5, fontRendererObj.getStringWidth( s2 ) );
                i4 = this.fontRendererObj.splitStringWidth(s2, i5);
                this.drawGradientRect(j4 - 3, l3 - 3, j4 + i5 + 3, l3 + i4 + 12 + 3, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(s2, j4, l3 + 12, i5, -9416624);
            }

            this.fontRendererObj.drawStringWithShadow(s, j4, l3, ( pet.hasSkillRequirements(hoveredSkill.id) || pet.hasSkill( hoveredSkill.id ) ) ? (hoveredSkill.skillReqs == null ? -128 : -1) : (hoveredSkill.skillReqs == null ? -8355776 : -8355712));
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.disableStandardItemLighting();
        
        clicked = -1;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }
}
