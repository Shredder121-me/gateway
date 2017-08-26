package chat.amy;

import lombok.Getter;

/**
 * @author amy
 * @since 8/26/17.
 */
public enum Opcode {
    /*
     * Generic opcodes
     */
    IDENTIFY(0), SHUTDOWN(1), REBOOT(2),
    
    /*
     * Discord-specific opcodes
     */
    SHARD_CONNECT(10);
    
    @Getter
    private final int opcodeId;
    
    Opcode(final int opcodeId) {
        this.opcodeId = opcodeId;
    }
}
