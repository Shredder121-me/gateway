package chat.amy;

import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author amy
 * @since 8/26/17.
 */
public class HeartbeatCheckerService extends AbstractScheduledService {
    private final Gateway gateway;
    
    public HeartbeatCheckerService(final Gateway gateway) {
        this.gateway = gateway;
    }
    
    @Override
    protected void runOneIteration() throws Exception {
        final List<Entry<String, Long>> deadServices = gateway.getHeartbeatTimeTracker().entrySet().stream()
                .filter(e -> e.getValue() + 5000 <= System.currentTimeMillis())
                .collect(Collectors.toList());
        deadServices.forEach(serviceTimeEntry -> {
            // TODO: Logging
            gateway.getHeartbeatTimeTracker().remove(serviceTimeEntry.getKey());
        });
    }
    
    @Override
    protected Scheduler scheduler() {
        return null;
    }
}
