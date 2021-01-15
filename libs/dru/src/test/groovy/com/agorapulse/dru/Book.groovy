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
 * Book entity
 */
class Book implements Comparable<Book> {

    String title
    List<Author> authors
    Set<Genre> genres
    Collection<String> tags
    Map<String, String> ext

    @SuppressWarnings([
        'IfStatementCouldBeTernary',
        'IfStatementBraces',
        'UnnecessaryIfStatement',
    ])
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Book book = (Book) o

        if (title != book.title) return false

        return true
    }

    int hashCode() {
        return (title != null ? title.hashCode() : 0)
    }

    @Override
    int compareTo(Book o) {
        return title.compareToIgnoreCase(o.title)
    }
}
