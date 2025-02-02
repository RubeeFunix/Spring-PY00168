package poly.petshop.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int supplierId;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String tenSup;

    @Column(nullable = true, columnDefinition = "NVARCHAR(255)")
    private String diaChiSup;

    @Column(nullable = true, unique = true, columnDefinition = "NVARCHAR(255)")
    private String emailSup;

    @Column(nullable = true, unique = true, columnDefinition = "NVARCHAR(255)")
    private String taxCode;

    // One supplier --> to many --> products
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Product> products;

    public Supplier() {
    }

    public Supplier(int supplierId, String tenSup, String diaChiSup, String emailSup, String taxCode,
            List<Product> products) {
        this.supplierId = supplierId;
        this.tenSup = tenSup;
        this.diaChiSup = diaChiSup;
        this.emailSup = emailSup;
        this.taxCode = taxCode;
        this.products = products;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getTenSup() {
        return tenSup;
    }

    public void setTenSup(String tenSup) {
        this.tenSup = tenSup;
    }

    public String getDiaChiSup() {
        return diaChiSup;
    }

    public void setDiaChiSup(String diaChiSup) {
        this.diaChiSup = diaChiSup;
    }

    public String getEmailSup() {
        return emailSup;
    }

    public void setEmailSup(String emailSup) {
        this.emailSup = emailSup;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
