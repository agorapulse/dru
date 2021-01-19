package dru.micronaut.example.jpa;

import javax.persistence.*;

@SuppressWarnings({"JpaObjectClassSignatureInspection", "FieldMayBeFinal"})
@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private Manufacturer manufacturer;

    public Product(String name, Manufacturer manufacturer) {
        this.name = name;
        this.manufacturer = manufacturer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    @Override
    public String toString() {
        return "Product{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", manufacturer=" + manufacturer +
            '}';
    }
}
