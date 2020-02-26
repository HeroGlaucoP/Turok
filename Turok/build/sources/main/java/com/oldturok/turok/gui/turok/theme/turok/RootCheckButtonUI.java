package com.oldturok.turok.gui.turok.theme.turok;

import com.oldturok.turok.gui.turok.TurokGUI;
import com.oldturok.turok.gui.rgui.component.container.Container;
import com.oldturok.turok.gui.rgui.component.use.CheckButton;
import com.oldturok.turok.gui.rgui.render.AbstractComponentUI;
import com.oldturok.turok.gui.rgui.render.font.FontRenderer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RootCheckButtonUI<T extends CheckButton> extends AbstractComponentUI<CheckButton> {

    protected Color backgroundColour = new Color(0, 0, 255);
    protected Color backgroundColourHover = new Color(0, 0, 255);

    protected Color idleColourNormal = new Color(0, 0, 255);
    protected Color downColourNormal = new Color(0, 0, 255);

    protected Color idleColourToggle = new Color(0, 0, 255);
    protected Color downColourToggle = idleColourToggle.brighter();

    @Override
    public void renderComponent(CheckButton component, FontRenderer ff) {

        glColor4f(backgroundColour.getRed()/255f, backgroundColour.getGreen()/255f, backgroundColour.getBlue()/255f, component.getOpacity());
        if (component.isToggled()){
            glColor3f(.9f, backgroundColour.getGreen()/255f, backgroundColour.getBlue()/255f);
        }
        if (component.isHovered() || component.isPressed()){
            glColor4f(backgroundColourHover.getRed()/255f, backgroundColourHover.getGreen()/255f, backgroundColourHover.getBlue()/255f, component.getOpacity());
        }

        String text = component.getName();
        int c = component.isPressed() ? 0x338243 : component.isToggled() ? 0x1f8a34 : 0x6cff89;
        if (component.isHovered())
            c = (c & 0x80f94d) << 1;

        glColor3f(1,1,1);
        glEnable(GL_TEXTURE_2D);
        TurokGUI.fontRenderer.drawString(1, TurokGUI.fontRenderer.getFontHeight()/2-1, c, text);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    @Override
    public void handleAddComponent(CheckButton component, Container container) {
        component.setWidth(TurokGUI.fontRenderer.getStringWidth(component.getName()) + 28);
        component.setHeight(TurokGUI.fontRenderer.getFontHeight()+2);
    }
}
