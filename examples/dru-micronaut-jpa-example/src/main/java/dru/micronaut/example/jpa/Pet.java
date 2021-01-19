package dru.micronaut.example.jpa;

import io.micronaut.core.annotation.Creator;

import javax.annotation.Nullable;
import javax.persistence.*;

@Entity
@SuppressWarnings({"FieldMayBeFinal", "JpaObjectClassSignatureInspection"})
public class Pet {
    public enum PetType {
        DOG,
        CAT
    }

    @Id
    @GeneratedValue()
    private Long id;
    private String name;
    @ManyToOne
    private Owner owner;
    private PetType type = PetType.DOG;

    @Creator
    public Pet(String name, @Nullable Owner owner) {
        this.name = name;
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public PetType getType() {
		return type;
	}

	public void setType(PetType type) {
		this.type = type;
	}

	public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pet{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", owner=" + owner +
            ", type=" + type +
            '}';
    }
}
