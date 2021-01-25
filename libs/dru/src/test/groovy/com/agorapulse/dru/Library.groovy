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
package com.agorapulse.dru

/**
 * Library contains books from different authors and
 */
class Library implements Comparable<Library> {

    String name

    NavigableSet<Genre> genres
    SortedSet<Author> authors
    SortedSet<Book> books

    boolean accessible

    @SuppressWarnings([
        'IfStatementCouldBeTernary',
        'IfStatementBraces',
        'UnnecessaryIfStatement',
    ])
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Library library = (Library) o

        if (name != library.name) return false

        return true
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }

    @Override
    int compareTo(Library o) {
        return name.compareToIgnoreCase(name)
    }
}
