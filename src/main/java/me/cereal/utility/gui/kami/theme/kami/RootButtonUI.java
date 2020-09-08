package me.cereal.utility.gui.kami.theme.kami;

import me.cereal.utility.gui.kami.KamiGUI;
import me.cereal.utility.gui.kami.RenderHelper;
import me.cereal.utility.gui.rgui.component.container.Container;
import me.cereal.utility.gui.rgui.component.use.Button;
import me.cereal.utility.gui.rgui.render.AbstractComponentUI;
import me.cereal.utility.gui.rgui.render.font.FontRenderer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 25/06/2017.
 */
public class RootButtonUI<T extends Button> extends AbstractComponentUI<Button> {

    protected Color idleColour = new Color(163, 163, 163);
    protected Color downColour = new Color(255, 255, 255);

    @Override
    public void renderComponent(Button component, FontRenderer ff) {
        glColor3f(1.0f, 1.0f, 1.0f);
        if (component.isHovered() || component.isPressed()){
            glColor3f(1.0f, 1.0f, 1.0f);
        }

        RenderHelper.drawRoundedRectangle(0,0,component.getWidth(), component.getHeight(), 3f);

        glColor3f(1,1,1);
        glEnable(GL_TEXTURE_2D);
        KamiGUI.fontRenderer.drawString(component.getWidth() / 2 - KamiGUI.fontRenderer.getStringWidth(component.getName()) / 2, 0, component.isPressed() ? downColour : idleColour, component.getName());
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    @Override
    public void handleAddComponent(Button component, Container container) {
        component.setWidth(KamiGUI.fontRenderer.getStringWidth(component.getName()) + 28);
        component.setHeight(KamiGUI.fontRenderer.getFontHeight()+2);
    }
}