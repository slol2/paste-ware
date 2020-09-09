
package me.cereal.utility.gui.cereal.theme.cereal;

import me.cereal.utility.gui.cereal.RenderHelper;
import me.cereal.utility.gui.cereal.component.SettingsPanel;
import me.cereal.utility.gui.rgui.render.AbstractComponentUI;
import me.cereal.utility.gui.rgui.render.font.FontRenderer;

/**
 * Created by 086 on 16/12/2017.
 */

// Modify by Rina in 06/03/20.

import me.cereal.utility.util.TurokGL;

public class CerealSettingsPanelUI extends AbstractComponentUI<SettingsPanel> {

    @Override
    public void renderComponent(SettingsPanel component, FontRenderer fontRenderer) {
        super.renderComponent(component, fontRenderer);

        TurokGL.turok_RGBA(35, 35, 128, 100);
        RenderHelper.drawFilledRectangle(0, 0, component.getWidth(), component.getHeight());

        TurokGL.turok_RGBA(0, 0, 0, 255);
        RenderHelper.drawRectangle(0, 0, component.getWidth(), component.getHeight());
    }
}