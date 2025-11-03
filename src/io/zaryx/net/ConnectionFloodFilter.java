package io.zaryx.net;

import java.util.concurrent.*;

public class ConnectionFloodFilter
{

    private static final int MAX_CONNECTIONS_PER_IP = 1;
    private static ConcurrentHashMap<String, Integer> connMap;

    static
    {
        connMap = new ConcurrentHashMap<String, Integer>();
    }

    /**
     * Registers a host and returns whether or not
     * the host limit has been reached.
     *
     * @param host The host to register.
     * @return <code>true</code> if registration was successful,
     * <code>false</code> if otherwise.
     */
    public static boolean register(String host)
    {
        Integer count = connMap.putIfAbsent(host, 1);
        if (count != null)
            if (++count > MAX_CONNECTIONS_PER_IP)
                return false;
            else
                connMap.replace(host, count);
        return true;
    }

    /**
     * Unregisters the argued host from the filter.
     *
     * @param host The host to unregister.
     */
    public static void unregister(String host)
    {
        int count = connMap.get(host);
        if (--count > 0)
            connMap.replace(host, count);
        else
            connMap.remove(host);
    }

}