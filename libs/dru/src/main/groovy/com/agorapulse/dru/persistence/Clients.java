package com.agorapulse.dru.persistence;

import java.util.*;

public class Clients {

    private Clients() { }

    public static Collection<Client> createClients(Object unitTest) {
        ServiceLoader<ClientFactory> clientFactories = ServiceLoader.load(ClientFactory.class);
        TreeMap<Integer, Client> clients = new TreeMap<>();
        for (ClientFactory factory : clientFactories) {
            if (factory.isSupported(unitTest)) {
                clients.put(factory.getIndex(), factory.newClient(unitTest));
            }
        }
        return Collections.unmodifiableCollection(clients.values());
    }

}
