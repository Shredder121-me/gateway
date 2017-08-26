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
    
    public static Opcode getOpcodeByValue(final int value) {
        for(final Opcode opcode : values()) {
            if(opcode.opcodeId == value) {
                return opcode;
            }
        }
        return null;
    }
    
    public static Opcode getOpcodeByValue(final String value) {
        try {
            return getOpcodeByValue(Integer.parseInt(value));
        } catch(final NumberFormatException e) {
            return null;
        }
    }
}
