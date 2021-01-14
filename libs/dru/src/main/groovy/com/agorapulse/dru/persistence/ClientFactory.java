package com.agorapulse.dru.persistence;

public interface ClientFactory {

    int getIndex();
    boolean isSupported(Object unitTest);
    Client newClient(Object unitTest);

}
