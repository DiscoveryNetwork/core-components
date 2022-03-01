package network.discov.component.signatures.manager;

import network.discov.component.signatures.model.SignatureRequest;
import network.discov.component.signatures.Signatures;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class RequestManager {
    private final HashMap<UUID, SignatureRequest> requests = new HashMap<>();

    public void addRequest(SignatureRequest request) {
        requests.put(request.getUniqueId(), request);
        Signatures.getInstance().getScheduler().runTaskLater(() -> {
            requests.remove(request.getUniqueId());
        }, 1200L);
    }

    public @Nullable SignatureRequest getRequest(UUID uuid) {
        return requests.get(uuid);
    }

    public void removeRequest(SignatureRequest request) {
        requests.remove(request.getUniqueId());
    }

    public void clearRequests() {
        requests.clear();
    }
}
