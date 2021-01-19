package dru.micronaut.example.jpa;

import javax.persistence.*;

@Entity
@SuppressWarnings({"JpaObjectClassSignatureInspection"})
public class Sale {

    @ManyToOne
    private final Product product;
    private final Integer quantity;

    @Id
    @GeneratedValue
    private Long id;

    public Sale(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Sale{" +
            "product=" + product +
            ", quantity=" + quantity +
            ", id=" + id +
            '}';
    }
}
