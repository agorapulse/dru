package com.agorapulse.dru

/**
 * Book's genre.
 */
class Genre implements Comparable<Genre> {

    String name

    SortedSet<Book> books

    @SuppressWarnings([
        'IfStatementCouldBeTernary',
        'IfStatementBraces',
        'UnnecessaryIfStatement',
    ])
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Genre genre = (Genre) o

        if (name != genre.name) return false

        return true
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }

    @Override
    int compareTo(Genre o) {
        return name.compareToIgnoreCase(o.name)
    }
}
