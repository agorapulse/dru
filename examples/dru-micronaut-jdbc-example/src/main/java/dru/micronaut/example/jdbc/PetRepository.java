package dru.micronaut.example.jdbc;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.H2)
public interface PetRepository extends GenericRepository<Pet, UUID> {

    int count();
    Pet save(Pet pet);

}
