
package me.cereal.utility.gui.cereal.theme.cereal;

import me.cereal.utility.gui.cereal.CerealGUI;
import me.cereal.utility.gui.cereal.theme.staticui.RadarUI;
import me.cereal.utility.gui.cereal.theme.staticui.TabGuiUI;
import me.cereal.utility.gui.rgui.component.container.use.Frame;
import me.cereal.utility.gui.rgui.component.use.Button;
import me.cereal.utility.gui.rgui.render.AbstractComponentUI;
import me.cereal.utility.gui.rgui.render.font.FontRenderer;
import me.cereal.utility.gui.rgui.render.theme.AbstractTheme;

/**
 * Created by 086 on 26/06/2017.
 */
public class CerealTheme extends AbstractTheme {

    FontRenderer fontRenderer;

    public CerealTheme() {
        installUI(new RootButtonUI<Button>());
        installUI(new GUIUI());
        installUI(new RootGroupboxUI());
        installUI(new CerealFrameUI<Frame>());
        installUI(new RootScrollpaneUI());
        installUI(new RootInputFieldUI());
        installUI(new RootLabelUI());
        installUI(new RootChatUI());
        installUI(new RootCheckButtonUI());
        installUI(new CerealActiveModulesUI());
        installUI(new CerealSettingsPanelUI());
        installUI(new RootSliderUI());
        installUI(new CerealEnumbuttonUI());
        installUI(new RootColorizedCheckButtonUI());
        installUI(new CerealUnboundSliderUI());

        installUI(new RadarUI());
        installUI(new TabGuiUI());

        fontRenderer = CerealGUI.fontRenderer;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public class GUIUI extends AbstractComponentUI<CerealGUI> {
    }
}