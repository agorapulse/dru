package dru.micronaut.example.jpa;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.async.AsyncCrudRepository;

@Repository
public interface ProductRepository extends AsyncCrudRepository<Product, Long> {

}
