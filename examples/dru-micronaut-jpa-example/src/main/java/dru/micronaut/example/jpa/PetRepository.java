package dru.micronaut.example.jpa;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.GenericRepository;

import java.util.UUID;

@Repository
public interface PetRepository extends GenericRepository<Pet, UUID> {

    int count();
    Pet save(Pet pet);

}
