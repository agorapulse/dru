package dru.micronaut.example.jpa;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository;

@Repository
public interface ManufacturerRepository extends ReactiveStreamsCrudRepository<Manufacturer, Long> {

}
