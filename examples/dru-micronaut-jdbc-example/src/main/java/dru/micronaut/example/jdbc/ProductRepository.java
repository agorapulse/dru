package dru.micronaut.example.jdbc;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.async.AsyncCrudRepository;

@JdbcRepository(dialect = Dialect.H2)
public interface ProductRepository extends AsyncCrudRepository<Product, Long> {

}
