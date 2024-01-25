package com.grpc.server.loadbalance;

import io.grpc.EquivalentAddressGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRegistry {

    private static final Map<String, List<EquivalentAddressGroup>> MAP = new HashMap<> ();

    public static void register (String service, List<String> instances) {
        final List<EquivalentAddressGroup> list = instances.stream ()
                .map (i -> i.split (":"))
                .map (addr -> new InetSocketAddress (addr[0], Integer.parseInt (addr[1])))
                .map (EquivalentAddressGroup::new)
                .toList ();

        MAP.put (service, list);

    }

    public static List<EquivalentAddressGroup> getInstances (String service) {
        return MAP.get (service);

    }

}
