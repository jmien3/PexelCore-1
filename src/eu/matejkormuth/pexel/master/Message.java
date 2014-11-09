package eu.matejkormuth.pexel.master;

import java.nio.ByteBuffer;

/**
 * Class that represents message over network.
 */
public abstract class Message {
    /**
     * ID of request that involved creation of this message.
     */
    protected long requestID;
    
    /**
     * Returns byte array representation of this message.
     * 
     * @return
     */
    public abstract ByteBuffer toByteBuffer();
    
    /**
     * Should constrct Message ({@link Request}, {@link Response}, ...) from byte array.
     * 
     * @param buffer
     *            array containing data
     */
    public abstract void fromByteBuffer(ByteBuffer buffer);
}
