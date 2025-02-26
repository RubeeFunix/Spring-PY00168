package poly.petshop.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Transient;

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
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

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
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // one user to many payments
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Payment> payments;

    @Transient // Không lưu vào DB, chỉ dùng tạm thời để nhận dữ liệu từ form
    private List<Integer> selectedProductIds;

    public List<Integer> getSelectedProductIds() {
        return selectedProductIds;
    }

    public void setSelectedProductIds(List<Integer> selectedProductIds) {
        this.selectedProductIds = selectedProductIds;
    }

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

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setNgayOrder(Date ngayOrder) {
        this.ngayOrder = ngayOrder;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void setTongGiaTri(float tongGiaTri) {
        this.tongGiaTri = tongGiaTri;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails.clear(); // Xóa danh sách cũ
        if (orderDetails != null) {
            this.orderDetails.addAll(orderDetails);
        }
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getOrderId() {
        return orderId;
    }

    public Date getNgayOrder() {
        return ngayOrder;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public float getTongGiaTri() {
        return tongGiaTri;
    }

    public User getUser() {
        return user;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public List<Payment> getPayments() {
        return payments;
    }

}
