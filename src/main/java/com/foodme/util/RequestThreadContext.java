package com.foodme.util;

import com.google.common.net.InetAddresses;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.util.UUID;

public class RequestThreadContext {

    private static final String REQUEST_ID = "request_id";
    private static final String REMOTE_ADDRESS = "remote_address";

    public static void initialize(String remoteIp) {
        MDC.put(REQUEST_ID, UUIDUtils.toStringWithoutDashes(UUID.randomUUID()));
        MDC.put(REMOTE_ADDRESS, remoteIp);
    }

    public static void clear() {
        MDC.clear();
    }

    public static String getRequestIdString() {
        return MDC.get(REQUEST_ID);
    }
    public static UUID getRequestId() {
        return UUIDUtils.fromStringWithoutDashes(getRequestIdString());
    }

    public static String getRemoteAddressString() {
        return MDC.get(REMOTE_ADDRESS);
    }
    public static InetAddress getRemoteAddress() {
        return InetAddresses.forString(getRemoteAddressString());
    }
}
