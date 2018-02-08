package com.foodme.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtils {

    public static UUID fromStringWithoutDashes(String uuid) {
        try {
            final ByteBuffer buffer = ByteBuffer.wrap(Hex.decodeHex(uuid.toCharArray()));
            return new UUID(buffer.getLong(), buffer.getLong());
        } catch (DecoderException e) {
            throw new IllegalArgumentException(uuid, e);
        }
    }

    public static String toStringWithoutDashes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return Hex.encodeHexString(buffer.array());
    }
}
