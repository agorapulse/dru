package dru.micronaut.example.jdbc;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository;

@JdbcRepository(dialect = Dialect.H2)
public interface ManufacturerRepository extends ReactiveStreamsCrudRepository<Manufacturer, Long> {

}
