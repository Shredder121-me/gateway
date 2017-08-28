package chat.amy;

import chat.amy.noelia.Noelia;
import chat.amy.noelia.message.NoeliaMessage;
import chat.amy.noelia.message.util.heartbeat.HeartbeatPredicate;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.Collections;
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
        // TODO: /gateway auth endpoint
        // Note: Jetty supports `Upgrade: Websocket`, so we can just run everything on one port and let the internal Jetty server figure it out
        
        // Start heartbeat checker
        heartbeatCheckerService.startAsync();
        // Set up gateway message proxy
        Noelia.flow()
                .check(message -> message.getTopic().startsWith("gateway-proxy:"))
                .accept(message -> {
                    final String[] topics = message.getTopic().split(":", 3);
                    final String proxyTarget = topics[1];
                    
                    return ImmutableMap.of(proxyTarget, Collections.singletonList(new NoeliaMessage(proxyTarget, topics[2], message.getData())));
                })
                .subscribe();
        // Actual gateway messages
        Noelia.flow()
                .check(message -> message.getTopic().startsWith("gateway:"))
                .accept(message -> {
                    final String topic = message.getTopic().replaceFirst("gateway:", "");
                    if(topic.equalsIgnoreCase("opcode")) {
                        final JsonElement data = message.getData();
                        final JsonObject o = data.getAsJsonObject();
                        final String opcodeString = o.get("opcode").getAsString();
                        final Opcode op = Opcode.getOpcodeByValue(opcodeString);
                        if(op == null) {
                            // Invalid OP, do nothing
                        } else {
                            switch(op) {
                                case IDENTIFY:
                                    // TODO: Determine if a service is allowed to connect.
                                    break;
                                case SHARD_CONNECT:
                                    // TODO: Maintain a table of connected shards and when new shards can boot. This needs to be determined based on how many shards we actually need.
                                    break;
                            }
                        }
                    }
                })
                .subscribe();
        // Start accepting heartbeats
        Noelia.flow()
                .check(new HeartbeatPredicate())
                .accept(message -> {
                    heartbeatTimeTracker.put(message.getSource(), System.currentTimeMillis());
                })
                .subscribe();
    }
}
