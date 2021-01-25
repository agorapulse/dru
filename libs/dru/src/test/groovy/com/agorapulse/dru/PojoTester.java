/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.dru;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PojoTester {

    public static class ThrowsExceptionInConstructor {
        public ThrowsExceptionInConstructor() {
            throw new IllegalStateException("You can't initialize this class");
        }
    }

    public static class CollectionSubClass extends ArrayList<Integer> { }

    public static abstract class AbstractCollectionWithInterface implements List<Boolean> { }

    private Collection<String> collectionValue;

    private CollectionSubClass collectionSubClassValue;

    private Collection rawCollectionValue;

    private Collection<Object> objectCollectionValue;

    private AbstractCollectionWithInterface abstractCollectionWithInterfaceValue;

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
