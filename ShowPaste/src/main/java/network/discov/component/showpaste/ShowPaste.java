package network.discov.component.showpaste;

import lombok.Getter;
import network.discov.core.spigot.model.SpigotComponent;

public class ShowPaste extends SpigotComponent {
    @Getter private static ShowPaste instance;

    public ShowPaste() {
        super("ShowPaste");
        instance = this;
    }

    @Override
    public void onEnable() {
        addDefaultMessage("schematic-paste-success", "&aSchematic pasted!");
        addDefaultMessage("schematic-paste-error", "&cAn error occurred while pasting the schematic. Please check the console.");
        registerCommand(new PasteCommand());
    }

    @Override
    public void onDisable() {

    }

    public String getSchematicsFolder() {
        return getConfig().getString("schematics-folder");
    }
}
