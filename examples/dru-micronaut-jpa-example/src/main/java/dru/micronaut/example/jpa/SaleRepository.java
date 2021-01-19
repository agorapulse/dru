package dru.micronaut.example.jpa;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;

@Repository
public interface SaleRepository extends RxJavaCrudRepository<Sale, Long> {

}
