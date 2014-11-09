package eu.matejkormuth.pexel.network.responses;

import java.nio.ByteBuffer;

import eu.matejkormuth.pexel.network.Response;

public class ServerResourcesResponse extends Response {
    long maxMem;
    long usedMem;
    
    public ServerResourcesResponse(final long maxMem, final long usedMem) {
        this.maxMem = maxMem;
        this.usedMem = usedMem;
    }
    
    @Override
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.allocate(2 * 8).putLong(this.maxMem).putLong(this.usedMem);
    }
    
    @Override
    public void fromByteBuffer(final ByteBuffer buffer) {
        this.maxMem = buffer.getLong();
        this.usedMem = buffer.getLong();
    }
}