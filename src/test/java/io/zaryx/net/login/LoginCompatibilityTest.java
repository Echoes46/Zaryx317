package io.zaryx.net.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.zaryx.Configuration;
import io.zaryx.net.Packet;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class LoginCompatibilityTest {
    private EmbeddedChannel loginChannel() throws Exception {
        RS2LoginProtocol decoder = new RS2LoginProtocol();
        Field state = RS2LoginProtocol.class.getDeclaredField("state");
        state.setAccessible(true);
        state.setInt(decoder, 3); // Completed handshake and proof of work.
        EmbeddedChannel channel = new EmbeddedChannel() {
            @Override protected SocketAddress remoteAddress0() {
                return new InetSocketAddress("127.0.0.1", 12345);
            }
        };
        channel.pipeline().addLast("decoder", decoder);
        return channel;
    }

    private byte[] frame(int revision) {
        ByteBuf buffer = Unpooled.buffer();
        try {
            // Invalid RSA contents intentionally prove mismatches are rejected before decryption.
            buffer.writeByte(16).writeByte(43).writeByte(255).writeShort(revision);
            buffer.writeByte(0).writeZero(36).writeByte(1).writeByte(0);
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            return bytes;
        } finally { buffer.release(); }
    }

    @Test void oldAndNewerClientsReceiveOneUpdateResponseAtEverySplit() throws Exception {
        for (int revision : new int[] {369, Configuration.CLIENT_VERSION + 1}) {
            byte[] bytes = frame(revision);
            for (int split = 1; split < bytes.length; split++) {
                EmbeddedChannel channel = loginChannel();
                try {
                    assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(bytes, 0, split)));
                    assertTrue(channel.isActive());
                    assertNull(channel.readOutbound());
                    assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(bytes, split, bytes.length - split)));
                    Packet response = channel.readOutbound();
                    assertNotNull(response);
                    try {
                        assertEquals(1, response.getPayload().readableBytes());
                        assertEquals(6, response.getPayload().readUnsignedByte());
                    } finally { response.getPayload().release(); }
                    assertFalse(channel.isActive());
                    assertNull(channel.readOutbound());
                    assertNull(channel.readInbound());
                } finally { channel.finishAndReleaseAll(); }
            }
        }
    }

    @Test void matchingRevisionContinuesToEncryptedBlockValidation() throws Exception {
        EmbeddedChannel channel = loginChannel();
        try {
            assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(frame(Configuration.CLIENT_VERSION))));
            Packet response = channel.readOutbound();
            assertNotNull(response);
            try { assertEquals(LoginReturnCode.UNABLE_TO_CONNECT.getCode(), response.getPayload().readUnsignedByte()); }
            finally { response.getPayload().release(); }
            assertFalse(channel.isActive());
        } finally { channel.finishAndReleaseAll(); }
    }
}
