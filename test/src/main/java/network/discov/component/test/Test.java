package network.discov.component.test;

import network.discov.core.spigot.model.SpigotComponent;

public class Test extends SpigotComponent {

    @Override
    public void onEnable() {
        registerCommand(new TestCommand());
        registerListener(new TestListener());
    }

    @Override
    public void onDisable() {

    }
}
