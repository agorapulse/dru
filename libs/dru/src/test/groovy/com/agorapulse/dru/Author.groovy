package com.agorapulse.dru

/**
 * Author of the book.
 */
class Author implements Comparable<Author> {

    String fullName

    SortedSet<Book> books

    @SuppressWarnings([
        'IfStatementCouldBeTernary',
        'IfStatementBraces',
        'UnnecessaryIfStatement',
    ])
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Author author = (Author) o

        if (fullName != author.fullName) return false

        return true
    }

    int hashCode() {
        return (fullName != null ? fullName.hashCode() : 0)
    }

    @Override
    int compareTo(Author o) {
        return fullName.compareToIgnoreCase(o.fullName)
    }
}
