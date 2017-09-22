package com.agorapulse.dru;

import java.util.ArrayList;
import java.util.Collection;

public class PojoTester {

    public static class ThrowsExceptionInConstructor {
        public ThrowsExceptionInConstructor() {
            throw new IllegalStateException("You can't initialize this class");
        }
    }

    public static class CollectionSubClass extends ArrayList { }

    private Collection<String> collectionValue;

    private CollectionSubClass collectionSubClassValue;

    private boolean booleanValue;

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    private int numericalValue;

    public int getNumericalValue() {
        return numericalValue;
    }

    public void setNumericalValue(int numericalValue) {
        this.numericalValue = numericalValue;
    }

    private Library libraryValue;

    public Library getLibraryValue() {
        return libraryValue;
    }

    public void setLibraryValue(Library libraryValue) {
        this.libraryValue = libraryValue;
    }

    private final String readOnly = "readOnly";

    private String readWrite = "readWrite";

    private String _withGetterOnly = "withGetterOnly";

    public String getWithGetterOnly() {
        return _withGetterOnly;
    }

    private String _withSetterOnly = "withSetterOnly";

    public void setWithSetterOnly(String withSetterOnly) {
        this._withSetterOnly = withSetterOnly;
    }

    public void setWillThrowClassCastException(Object foo) {
        throw new ClassCastException();
    }
}
