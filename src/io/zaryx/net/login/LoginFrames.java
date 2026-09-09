package io.zaryx.net.login;

import io.netty.buffer.ByteBuf;

final class LoginFrames {
    private LoginFrames() { }
    static boolean hasLogin(ByteBuf input) {
        // The 317 client advertises RSA block length + 41, but writes
        // only 40 bytes before that block (magic, revision, memory, CRCs).
        // Preserve that established wire format without waiting for a phantom byte.
        return input.readableBytes() >= 2
                && input.readableBytes() >= 1 + input.getUnsignedByte(input.readerIndex() + 1);
    }
    static boolean hasProof(ByteBuf input) {
        return input.readableBytes() >= 1 + Long.BYTES;
    }
}
