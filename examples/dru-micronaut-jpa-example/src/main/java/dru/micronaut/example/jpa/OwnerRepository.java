package dru.micronaut.example.jpa;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface OwnerRepository extends CrudRepository<Owner, Long> {

}
