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
                if (ready) output.add(input.readRetainedSlice(proof ? 9 : 2 + input.getUnsignedByte(before + 1)));
            }
        });
    }
    @Test void loginWorksAtEveryTcpSplitAndIgnoresUnusedCapacity() {
        byte[] frame = new byte[130]; frame[0] = 16; frame[1] = (byte)128;
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
}
