package com.imoxion.sensems.server.nio.common;

import java.io.IOException;
import java.io.InputStream;

public class BlockInputStream extends InputStream {
    private int subAvailable = 0; // <= 0, offset of stop-position relatively from end of main stream
    private InputStream stream;

    public BlockInputStream(InputStream stream, int offset, int length) throws Exception {
        // super();
        this.stream = stream;
        int available = stream.available();
        offset =  Math.min(offset, available);
        subAvailable = Math.min(offset + length, available) - available;
        this.skip(offset);
    }

    @Override
    public int available() throws IOException {
        return stream.available() + subAvailable;
    }

    @Override
    public void close() throws IOException {
        stream.close();
        super.close();
    }

    @Override
    public int read() throws IOException {
        if (available()>0)
            return stream.read();
        else
            return -1;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return stream.read(buffer, byteOffset, Math.min(byteCount, available()));
    }
}
