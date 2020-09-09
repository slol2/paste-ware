package me.cereal.utility;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import me.cereal.utility.command.Command;
import me.cereal.utility.command.CommandManager;
import me.cereal.utility.event.ForgeEventProcessor;
import me.cereal.utility.gui.kami.KamiGUI;
import me.cereal.utility.gui.rgui.component.AlignedComponent;
import me.cereal.utility.gui.rgui.component.Component;
import me.cereal.utility.gui.rgui.component.container.use.Frame;
import me.cereal.utility.gui.rgui.util.ContainerHelper;
import me.cereal.utility.gui.rgui.util.Docking;
import me.cereal.utility.module.Module;
import me.cereal.utility.module.ModuleManager;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.setting.SettingsRegister;
import me.cereal.utility.setting.config.Configuration;
import me.cereal.utility.util.Friends;
import me.cereal.utility.util.LagCompensator;
import me.cereal.utility.util.Wrapper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Created by 086 on 7/11/2017.
 */
@Mod(modid = KamiMod.MODID, name = KamiMod.MODNAME, version = KamiMod.MODVER)
public class KamiMod {

    public static final String MODID = "cereal";
    public static final String MODNAME = "Cereal Utility Mod";
    public static final String MODVER = "0.8";

    public static final String NAME_UNICODE = "Cereal-Utility";

    private static final String KAMI_CONFIG_NAME_DEFAULT = "CerealConfig.json";

    public static final Logger log = LogManager.getLogger("Cereal Utility Mod");

    public static final EventBus EVENT_BUS = new EventManager();

    @Mod.Instance
    private static KamiMod INSTANCE;

    public KamiGUI guiManager;
    public CommandManager commandManager;
    public FMLPreInitializationEvent hwid;
    private Setting<JsonObject> guiStateSetting = Settings.custom("gui", new JsonObject(), new Converter<JsonObject, JsonObject>() {
        @Override
        protected JsonObject doForward(JsonObject jsonObject) {
            return jsonObject;
        }

        @Override
        protected JsonObject doBackward(JsonObject jsonObject) {
            return jsonObject;
        }
    }).buildAndRegister("");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        final String var0 = String.valueOf(System.getenv("os")) + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("os.version") + System.getProperty("user.language") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS");
        final String sha512hex = DigestUtils.sha512Hex(var0);
        final String key = DigestUtils.sha512Hex(sha512hex);
        try {
            String fuck = "https://pastebin.com/raw/GkZv0NTd";
            URL pastebin = new URL(fuck.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(pastebin.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase(key))
                    verified = true;
            }
            if (!verified) {
                JOptionPane.showMessageDialog(null, "Your key has been copied to your clipboard, please message zopac/freemanatee#7735 the string of numbers.");
                StringSelection stringSelection = new StringSelection(key);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                System.out.println("cereal is no");
            }
        } catch (Exception exception) {}
        if (!verified) {
            Runtime.getRuntime().halt(0);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        KamiMod.log.info("\n\nInitializing Cereal Utility Mod " + MODVER);

        ModuleManager.initialize();

        ModuleManager.getModules().stream().filter(module -> module.alwaysListening).forEach(EVENT_BUS::subscribe);
        MinecraftForge.EVENT_BUS.register(new ForgeEventProcessor());
        LagCompensator.INSTANCE = new LagCompensator();

        Wrapper.init();

        guiManager = new KamiGUI();
        guiManager.initializeGUI();

        commandManager = new CommandManager();

        Friends.initFriends();
        SettingsRegister.register("commandPrefix", Command.commandPrefix);
        loadConfiguration();
        KamiMod.log.info("Settings loaded");

        ModuleManager.updateLookup(); // generate the lookup table after settings are loaded to make custom module names work

        // After settings loaded, we want to let the enabled modules know they've been enabled (since the setting is done through reflection)
        ModuleManager.getModules().stream().filter(Module::isEnabled).forEach(Module::enable);

        KamiMod.log.info("Cereal Utility Mod initialized!\n");
    }

    public static String getConfigName() {
        Path config = Paths.get("CerealLastConfig.txt");
        String kamiConfigName = KAMI_CONFIG_NAME_DEFAULT;
        try(BufferedReader reader = Files.newBufferedReader(config)) {
            kamiConfigName = reader.readLine();
            if (!isFilenameValid(kamiConfigName)) kamiConfigName = KAMI_CONFIG_NAME_DEFAULT;
        } catch (NoSuchFileException e) {
            try(BufferedWriter writer = Files.newBufferedWriter(config)) {
                writer.write(KAMI_CONFIG_NAME_DEFAULT);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kamiConfigName;
    }

    public static void loadConfiguration() {
        try {
            loadConfigurationUnsafe();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfigurationUnsafe() throws IOException {
        String kamiConfigName = getConfigName();
        Path kamiConfig = Paths.get(kamiConfigName);
        if (!Files.exists(kamiConfig)) return;
        Configuration.loadConfiguration(kamiConfig);

        JsonObject gui = KamiMod.INSTANCE.guiStateSetting.getValue();
        for (Map.Entry<String, JsonElement> entry : gui.entrySet()) {
            Optional<Component> optional = KamiMod.INSTANCE.guiManager.getChildren().stream().filter(component -> component instanceof Frame).filter(component -> ((Frame) component).getTitle().equals(entry.getKey())).findFirst();
            if (optional.isPresent()) {
                JsonObject object = entry.getValue().getAsJsonObject();
                Frame frame = (Frame) optional.get();
                frame.setX(object.get("x").getAsInt());
                frame.setY(object.get("y").getAsInt());
                Docking docking = Docking.values()[object.get("docking").getAsInt()];
                if (docking.isLeft()) ContainerHelper.setAlignment(frame, AlignedComponent.Alignment.LEFT);
                else if (docking.isRight()) ContainerHelper.setAlignment(frame, AlignedComponent.Alignment.RIGHT);
                else if (docking.isCenterVertical()) ContainerHelper.setAlignment(frame, AlignedComponent.Alignment.CENTER);
                frame.setDocking(docking);
                frame.setMinimized(object.get("minimized").getAsBoolean());
                frame.setPinned(object.get("pinned").getAsBoolean());
            } else {
                System.err.println("Found GUI config entry for " + entry.getKey() + ", but found no frame with that name");
            }
        }
        KamiMod.getInstance().getGuiManager().getChildren().stream().filter(component -> (component instanceof Frame) && (((Frame) component).isPinneable()) && component.isVisible()).forEach(component -> component.setOpacity(0f));
    }

    public static void saveConfiguration() {
        try {
            saveConfigurationUnsafe();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfigurationUnsafe() throws IOException {
        JsonObject object = new JsonObject();
        KamiMod.INSTANCE.guiManager.getChildren().stream().filter(component -> component instanceof Frame).map(component -> (Frame) component).forEach(frame -> {
            JsonObject frameObject = new JsonObject();
            frameObject.add("x", new JsonPrimitive(frame.getX()));
            frameObject.add("y", new JsonPrimitive(frame.getY()));
            frameObject.add("docking", new JsonPrimitive(Arrays.asList(Docking.values()).indexOf(frame.getDocking())));
            frameObject.add("minimized", new JsonPrimitive(frame.isMinimized()));
            frameObject.add("pinned", new JsonPrimitive(frame.isPinned()));
            object.add(frame.getTitle(), frameObject);
        });
        KamiMod.INSTANCE.guiStateSetting.setValue(object);

        Path outputFile = Paths.get(getConfigName());
        if (!Files.exists(outputFile))
            Files.createFile(outputFile);
        Configuration.saveConfiguration(outputFile);
        ModuleManager.getModules().forEach(Module::destroy);
    }

    public static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean verified = false;

    public static KamiMod getInstance() {
        return INSTANCE;
    }

    public KamiGUI getGuiManager() {
        return guiManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
