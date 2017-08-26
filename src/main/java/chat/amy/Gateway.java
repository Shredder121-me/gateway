package chat.amy;

import chat.amy.noelia.Noelia;
import com.google.common.collect.ImmutableMap;

/**
 * @author amy
 * @since 8/25/17.
 */
public class Gateway {
    public static final String SERVICE_NAME = "gateway";
    
    public static void main(final String[] args) {
        new Gateway().startGateway();
    }
    
    private void startGateway() {
        Noelia.flow()
                .check(message -> message.getTopic().startsWith("gateway:"))
                .accept(message -> {
                    return ImmutableMap.of();
                })
                .subscribe();
    }
}
