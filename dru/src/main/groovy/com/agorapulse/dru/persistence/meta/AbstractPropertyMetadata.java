package com.agorapulse.dru.persistence.meta;

public abstract class AbstractPropertyMetadata implements PropertyMetadata {

    @Override
    public final boolean isCollectionType() {
        return isBasicCollectionType() || isManyToMany() || isOneToMany();
    }

}
