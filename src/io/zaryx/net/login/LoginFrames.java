package io.zaryx.net.login;

import io.netty.buffer.ByteBuf;

final class LoginFrames {
    private LoginFrames() { }
    static boolean hasLogin(ByteBuf input) {
        return input.readableBytes() >= 2
                && input.readableBytes() >= 2 + input.getUnsignedByte(input.readerIndex() + 1);
    }
    static boolean hasProof(ByteBuf input) {
        return input.readableBytes() >= 1 + Long.BYTES;
    }
}
