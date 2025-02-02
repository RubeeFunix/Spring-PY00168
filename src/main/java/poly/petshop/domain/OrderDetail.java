package poly.petshop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordersdetails")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderDetailId;

    @Column(nullable = false)
    private int soLuong;

    @Column(nullable = false)
    private float donGia;

    @Column(nullable = false)
    private float tongGia;

    // many orderdetail to one order
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    // many orderdetail to one product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Override
    public String toString() {
        return "OrderDetail [orderDetailId=" + orderDetailId + ", soLuong=" + soLuong + ", donGia=" + donGia
                + ", tongGia=" + tongGia + ", order=" + order + ", product=" + product + "]";
    }

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailId, int soLuong, float donGia, float tongGia, Order order, Product product) {
        this.orderDetailId = orderDetailId;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.tongGia = tongGia;
        this.order = order;
        this.product = product;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public float getDonGia() {
        return donGia;
    }

    public void setDonGia(float donGia) {
        this.donGia = donGia;
    }

    public float getTongGia() {
        return tongGia;
    }

    public void setTongGia(float tongGia) {
        this.tongGia = tongGia;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
