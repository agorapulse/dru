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
