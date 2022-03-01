package network.discov.component.buildtools.command;

import network.discov.core.spigot.model.ComponentCommandExecutor;
import network.discov.core.spigot.model.SpigotComponent;

public class OutlinesCommandExecutor extends ComponentCommandExecutor {

    public OutlinesCommandExecutor(SpigotComponent component) {
        super("outlines", "Outlines command", new String[]{"o"}, component);
        commands.put("list", new ListReferenceCommand());
        commands.put("create", new CreateReferenceCommand());
        commands.put("remove", new RemoveReferenceCommand());
        commands.put("select", new SelectReferenceCommand());
        commands.put("place", new OutlinePlaceCommand());
    }
}
