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
