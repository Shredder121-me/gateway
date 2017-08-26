package chat.amy;

import chat.amy.noelia.Noelia;
import chat.amy.noelia.message.util.heartbeat.HeartbeatPredicate;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author amy
 * @since 8/25/17.
 */
public class Gateway {
    public static final String SERVICE_NAME = "gateway";
    
    @Getter
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<String, Long> heartbeatTimeTracker = new ConcurrentHashMap<>();
    
    private final HeartbeatCheckerService heartbeatCheckerService;
    
    private Gateway() {
        heartbeatCheckerService = new HeartbeatCheckerService(this);
    }
    
    public static void main(final String[] args) {
        new Gateway().startGateway();
    }
    
    private void startGateway() {
        // Start heartbeat checker
        heartbeatCheckerService.startAsync();
        // Set up gateway message proxy
        Noelia.flow()
                .check(message -> message.getTopic().startsWith("gateway:"))
                .accept(message -> {
                    // TODO: Routing
                    return ImmutableMap.of();
                })
                .subscribe();
        // Start accepting heartbeats
        Noelia.flow()
                .check(new HeartbeatPredicate())
                .accept(message -> {
                    heartbeatTimeTracker.put(message.getSource(), System.currentTimeMillis());
                    return ImmutableMap.of();
                })
                .subscribe();
    }
}
