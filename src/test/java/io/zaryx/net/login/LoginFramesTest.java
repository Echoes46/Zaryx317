package io.zaryx.net.login;

import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LoginFramesTest {
    private EmbeddedChannel channel(boolean proof) {
        return new EmbeddedChannel(new ByteToMessageDecoder() {
            protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> output) {
                int before = input.readerIndex();
                boolean ready = proof ? LoginFrames.hasProof(input) : LoginFrames.hasLogin(input);
                assertEquals(before, input.readerIndex());
                if (ready) output.add(input.readRetainedSlice(proof ? 9 : 1 + input.getUnsignedByte(before + 1)));
            }
        });
    }
    @Test void loginWorksAtEveryTcpSplitAndIgnoresUnusedCapacity() {
        // Match Client.login: RSA block length + 41 is advertised, but only
        // 40 prefix bytes plus the RSA block are transmitted after the header.
        byte[] frame = new byte[129]; frame[0] = 16; frame[1] = (byte)128;
        for (int split = 1; split < frame.length; split++) {
            EmbeddedChannel channel = channel(false);
            try {
                assertFalse(channel.writeInbound(Unpooled.buffer(1024).writeBytes(frame, 0, split)));
                assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(frame, split, frame.length - split)));
                ByteBuf result = channel.readInbound();
                assertEquals(frame.length, result.readableBytes()); result.release();
            } finally { channel.finishAndReleaseAll(); }
        }
    }
    @Test void fragmentedProofAndCoalescedMessagesArePreserved() {
        for (int split = 1; split < 9; split++) {
            EmbeddedChannel channel = channel(true);
            try {
                byte[] bytes = new byte[18]; bytes[0] = 20; bytes[9] = 20;
                assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(bytes, 0, split)));
                assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(bytes, split, bytes.length - split)));
                for (int i = 0; i < 2; i++) {
                    ByteBuf result = channel.readInbound(); assertEquals(9, result.readableBytes()); result.release();
                }
                assertNull(channel.readInbound());
            } finally { channel.finishAndReleaseAll(); }
        }
    }
    @Test void actualClientLoginLayoutDoesNotWaitForAnExtraByte() {
        ByteBuf rsa = Unpooled.buffer();
        ByteBuf wire = Unpooled.buffer();
        EmbeddedChannel channel = channel(false);
        try {
            rsa.writeByte(128).writeZero(128);
            wire.writeByte(16).writeByte(rsa.readableBytes() + 36 + 1 + 1 + 3);
            wire.writeByte(255).writeShort(369).writeByte(0);
            for (int i = 0; i < 9; i++) wire.writeInt(0);
            wire.writeBytes(rsa);
            int actualLength = wire.readableBytes();
            assertEquals(actualLength + 1, 2 + wire.getUnsignedByte(1));
            assertTrue(channel.writeInbound(wire.retain()));
            ByteBuf result = channel.readInbound();
            try { assertEquals(actualLength, result.readableBytes()); }
            finally { result.release(); }
            assertNull(channel.readInbound());
        } finally {
            wire.release(); rsa.release(); channel.finishAndReleaseAll();
        }
    }
}
