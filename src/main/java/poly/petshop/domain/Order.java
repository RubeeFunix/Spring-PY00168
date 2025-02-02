package poly.petshop.domain;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Column(nullable = false)
    private Date ngayOrder;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String trangThai;

    @Column(nullable = false)
    private float tongGiaTri;

    // many orders to one user
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    // one user to many orderDetails
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

    // one user to many payments
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Payment> payments;

    public Order() {
    }

    public Order(int orderId, Date ngayOrder, String trangThai, float tongGiaTri, User user,
            List<OrderDetail> orderDetails, List<Payment> payments) {
        this.orderId = orderId;
        this.ngayOrder = ngayOrder;
        this.trangThai = trangThai;
        this.tongGiaTri = tongGiaTri;
        this.user = user;
        this.orderDetails = orderDetails;
        this.payments = payments;
    }

    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", ngayOrder=" + ngayOrder + ", trangThai=" + trangThai + ", tongGiaTri="
                + tongGiaTri + ", user=" + user + ", orderDetails=" + orderDetails + ", payments=" + payments + "]";
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getNgayOrder() {
        return ngayOrder;
    }

    public void setNgayOrder(Date ngayOrder) {
        this.ngayOrder = ngayOrder;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public float getTongGiaTri() {
        return tongGiaTri;
    }

    public void setTongGiaTri(float tongGiaTri) {
        this.tongGiaTri = tongGiaTri;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

}
