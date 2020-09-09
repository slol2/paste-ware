package me.cereal.utility.command.commands;

import me.cereal.utility.CerealMod;
import me.cereal.utility.command.Command;
import me.cereal.utility.command.syntax.ChunkBuilder;
import me.cereal.utility.command.syntax.parsers.DependantParser;
import me.cereal.utility.command.syntax.parsers.EnumParser;
import me.cereal.utility.gui.cereal.CerealGUI;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by 086 on 14/10/2018.
 */
public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", new ChunkBuilder()
                .append("mode", true, new EnumParser(new String[]{"reload", "save", "path"}))
                .append("path", true, new DependantParser(0, new DependantParser.Dependency(new String[][]{{"path", "path"}}, "")))
                .build());
    }

    @Override
    public void call(String[] args) {
        if (args[0] == null) {
            Command.sendChatMessage("Missing argument &bmode&r: Choose from reload, save or path");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reload();
                break;
            case "save":
                try {
                    CerealMod.saveConfigurationUnsafe();
                    Command.sendChatMessage("Saved configuration!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Command.sendChatMessage("Failed to save! " + e.getMessage());
                }
                break;
            case "path":
                if (args[1] == null) {
                    Path file = Paths.get(CerealMod.getConfigName());
                    Command.sendChatMessage("Path to configuration: &b" + file.toAbsolutePath().toString());
                } else {
                    String newPath = args[1];
                    if (!CerealMod.isFilenameValid(newPath)) {
                        Command.sendChatMessage("&b" + newPath + "&r is not a valid path");
                        break;
                    }
                    try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("CerealLastConfig.txt"))) {
                        writer.write(newPath);
                        reload();
                        Command.sendChatMessage("Configuration path set to &b" + newPath + "&r!");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Command.sendChatMessage("Couldn't set path: " + e.getMessage());
                        break;
                    }
                }
                break;
            default:
                Command.sendChatMessage("Incorrect mode, please choose from: reload, save or path");
        }
    }

    private void reload() {
        CerealMod.getInstance().guiManager = new CerealGUI();
        CerealMod.getInstance().guiManager.initializeGUI();
        CerealMod.loadConfiguration();
        Command.sendChatMessage("Configuration reloaded!");
    }

}
