package network.discov.component.showpaste;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import network.discov.core.spigot.model.Command;
import network.discov.core.spigot.model.TabArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasteCommand extends Command {

    public PasteCommand() {
        super("paste","Paste a FAWE schematic at a given location", "core.commands.paste", new String[]{});
        arguments.put(0, new PositionalWorldArgument());
        arguments.put(1, new PositionalXArgument());
        arguments.put(2, new PositionalYArgument());
        arguments.put(3, new PositionalZArgument());
        arguments.put(4, new SchematicTabArgument());
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        int x, y, z;
        x = Integer.parseInt(args[1]);
        y = Integer.parseInt(args[2]);
        z = Integer.parseInt(args[3]);

        World world = Bukkit.getWorld(args[0]);
        Location location = new Location(world, x, y, z);
        File file = new File(ShowPaste.getInstance().getSchematicsFolder() + args[4]);

        try {
            pasteSchematic(location, file);
            sender.sendMessage(ShowPaste.getInstance().getMessage("schematic-paste-success"));
        } catch (Exception e) {
            sender.sendMessage(ShowPaste.getInstance().getMessage("schematic-paste-error"));
            e.printStackTrace();
        }
    }

    private void pasteSchematic(@NotNull Location location, File file) throws IOException {
        Clipboard clipboard;

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        }

        assert location.getWorld() != null;
        com.sk89q.worldedit.world.World world = FaweAPI.getWorld(location.getWorld().getName());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        }
    }
}

class SchematicTabArgument extends TabArgument {

    public SchematicTabArgument() {
        super("schematic", true);
    }

    @Override
    public List<String> getSuggestions(CommandSender commandSender) {
        List<String> suggestions = new ArrayList<>();
        File dir = new File(getSchematicsFolder());
        File[] files = dir.listFiles();
        if (files == null) {
            return suggestions;
        }

        for (File file : files) {
            if (file.isFile()) { suggestions.add(file.getName()); }
        }

        return suggestions;
    }

    @Override
    public @Nullable String validate(String arg) {
        File file = new File(getSchematicsFolder(), arg);
        if (!file.exists()) {
            return "Schematic not found.";
        }
        return null;
    }

    private String getSchematicsFolder() {
        return ShowPaste.getInstance().getSchematicsFolder();
    }
}

abstract class IntegerTabArgument extends TabArgument {

    public IntegerTabArgument(String name, boolean required) {
        super(name, required);
    }

    @Override
    public @Nullable String validate(String arg) {
        try {
            Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return "Error parsing integer.";
        }
        return null;
    }
}

class PositionalXArgument extends IntegerTabArgument {

    public PositionalXArgument() {
        super("x", true);
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return Collections.singletonList(String.valueOf(player.getLocation().getBlockX()));
        }
        return new ArrayList<>();
    }
}

class PositionalYArgument extends IntegerTabArgument {

    public PositionalYArgument() {
        super("y", true);
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return Collections.singletonList(String.valueOf(player.getLocation().getBlockY()));
        }
        return new ArrayList<>();
    }
}

class PositionalZArgument extends IntegerTabArgument {

    public PositionalZArgument() {
        super("z", true);
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return Collections.singletonList(String.valueOf(player.getLocation().getBlockZ()));
        }
        return new ArrayList<>();
    }
}

class PositionalWorldArgument extends TabArgument {

    public PositionalWorldArgument() {
        super("world", true);
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return Collections.singletonList(player.getWorld().getName());
        }

        return new ArrayList<>();
    }

    @Override
    public @Nullable String validate(String arg) {
        if (Bukkit.getWorld(arg) == null) {
            return "World not found.";
        }
        return null;
    }
}