package network.discov.component.signatures.command;

import network.discov.core.spigot.model.ComponentCommandExecutor;
import network.discov.core.spigot.model.SpigotComponent;

public class SignatureCommandExecutor extends ComponentCommandExecutor {

    public SignatureCommandExecutor(SpigotComponent component) {
        super("signatures", "Main command to manage your signatures!", new String[]{"sig", "autograph"}, component);
        commands.put("get", new GetCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("send", new SendCommand());
        commands.put("request", new RequestCommand());
    }
}
