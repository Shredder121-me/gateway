package chat.amy.discord;

import lombok.Getter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A pool of all available shards. This maintains a list of all available shard
 * IDs. When shards connect, the gateway checks the pool for an available shard
 * ID and allocates it. If it can't find one, the shard connection sits in the
 * queue until an available ID becomes available.
 * <p>
 * TODO: Handling resharding
 *
 * @author amy
 * @since 8/27/17.
 */
public final class ShardPool {
    // TODO: This needs to be adjustable for dynamic resharding without totally fucking over the clients by rebooting the gateway
    @Getter
    private final int shardCount = 10;
    private final BlockingQueue<Integer> availableIds = new LinkedBlockingQueue<>();
    @Getter
    private PoolState state;
    
    public void repopulateIds() {
        state = PoolState.REFRESHING;
        state = PoolState.POPULATING;
        for(int i = 0; i < shardCount; i++) {
            availableIds.add(i);
        }
        // TODO: Invalidate old shard connections
        state = PoolState.READY;
    }
    
    /**
     * Blocking shard ID poll that handles the {@link InterruptedException}s
     * for you.
     *
     * @return The next available shard ID, or <code>-1</code> if it gets
     * interrupted.
     */
    public Integer pollNewId() {
        try {
            return availableIds.take();
        } catch(final InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public void release(final int id) {
        if(!availableIds.contains(id)) {
            availableIds.add(id);
        } else {
            throw new IllegalArgumentException("Attempted to release ID that's already released!?");
        }
    }
    
    /**
     * The possible states that the pool can be in. Used for determining
     * whether we can poll a shard ID or not.
     */
    public enum PoolState {
        /**
         * The pool is ready and can be polled
         */
        READY,
        /**
         * The pool is "refreshing," ie getting a new shard count
         */
        REFRESHING,
        /**
         * The pool is filling the available shard IDs.
         */
        POPULATING,
    }
}
