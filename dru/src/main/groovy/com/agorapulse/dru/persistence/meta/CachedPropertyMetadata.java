package com.agorapulse.dru.persistence.meta;

public class CachedPropertyMetadata implements PropertyMetadata {

    private final String name;
    private final Class type;
    private final Class referencedPropertyType;
    private final boolean persistent;
    private final boolean oneToMany;
    private final boolean manyToOne;
    private final boolean manyToMany;
    private final boolean oneToOne;
    private final boolean association;
    private final boolean owningSide;
    private final String referencedPropertyName;
    private final boolean embedded;
    private final boolean basicCollectionType;
    private final PropertyMetadata original;

    CachedPropertyMetadata(PropertyMetadata original) {
        this.name = original.getName();
        this.type = original.getType();
        this.referencedPropertyType = original.getReferencedPropertyType();
        this.persistent = original.isPersistent();
        this.oneToMany = original.isOneToMany();
        this.manyToOne = original.isManyToOne();
        this.manyToMany = original.isManyToMany();
        this.oneToOne = original.isOneToOne();
        this.association = original.isAssociation();
        this.owningSide = original.isOwningSide();
        this.referencedPropertyName = original.getReferencedPropertyName();
        this.embedded = original.isEmbedded();
        this.basicCollectionType = original.isBasicCollectionType();
        this.original = original;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public Class getReferencedPropertyType() {
        return referencedPropertyType;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public boolean isOneToMany() {
        return oneToMany;
    }

    @Override
    public boolean isManyToOne() {
        return manyToOne;
    }

    @Override
    public boolean isManyToMany() {
        return manyToMany;
    }

    @Override
    public boolean isOneToOne() {
        return oneToOne;
    }

    public boolean isAssociation() {
        return association;
    }

    @Override
    public boolean isOwningSide() {
        return owningSide;
    }

    @Override
    public String getReferencedPropertyName() {
        return referencedPropertyName;
    }

    @Override
    public boolean isEmbedded() {
        return embedded;
    }

    @Override
    public boolean isBasicCollectionType() {
        return basicCollectionType;
    }

    @Override
    public boolean isCollectionType() {
        return isBasicCollectionType() || isManyToMany() || isOneToMany();
    }

    public PropertyMetadata getOriginal() {
        return original;
    }
}
