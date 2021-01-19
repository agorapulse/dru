package dru.micronaut.example.jdbc;

import javax.persistence.*;

@Entity
@SuppressWarnings({"JpaAttributeTypeInspection", "JpaObjectClassSignatureInspection"})
public class Sale {

    @ManyToOne
    private final Product product;
    private final Quantity quantity;

    @Id
    @GeneratedValue
    private Long id;

    public Sale(Product product, Quantity quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
