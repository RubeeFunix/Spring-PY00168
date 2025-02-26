package poly.petshop.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poly.petshop.domain.Order;
import poly.petshop.domain.OrderDetail;
import poly.petshop.domain.User;
import poly.petshop.repository.OrderDetailRepository;
import poly.petshop.repository.OrderRepository;
import poly.petshop.repository.ProductRepository;
import poly.petshop.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Order> findAll() {
        return this.orderRepository.findAll();
    }

    public double calculateTotalRevenue() {
        return findAll().stream()
                .mapToDouble(Order::getTongGiaTri)
                .sum();
    }

    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findTop5ByOrderByNgayOrderDesc();
    }

    public List<Double> getMonthlyRevenue() {
        List<Object[]> results = orderRepository.findMonthlyRevenue();
        List<Double> monthlyRevenue = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthlyRevenue.add(0.0); // Khởi tạo 0 cho từng tháng
        }
        for (Object[] result : results) {
            int month = (int) result[0];
            double revenue = (double) result[1];
            monthlyRevenue.set(month - 1, revenue); // Tháng bắt đầu từ 1, index từ 0
        }
        return monthlyRevenue;
    }

    public List<Integer> getMonthlyOrderCount() {
        List<Object[]> results = orderRepository.findMonthlyOrderCount();
        List<Integer> monthlyOrders = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthlyOrders.add(0); // Khởi tạo 0 cho từng tháng
        }
        for (Object[] result : results) {
            int month = (int) result[0];
            int count = ((Number) result[1]).intValue();
            monthlyOrders.set(month - 1, count); // Tháng bắt đầu từ 1, index từ 0
        }
        return monthlyOrders;
    }

    // Thống kê doanh thu theo loại hàng (category)
    public Map<String, Map<String, Object>> getRevenueByCategory() {
        Map<String, Map<String, Object>> categoryStats = new HashMap<>();
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();

        // Nhóm theo category
        Map<String, List<OrderDetail>> detailsByCategory = orderDetails.stream()
                .collect(Collectors.groupingBy(detail -> detail.getProduct().getCategory().getCategoryName()));

        for (Map.Entry<String, List<OrderDetail>> entry : detailsByCategory.entrySet()) {
            String categoryName = entry.getKey();
            List<OrderDetail> details = entry.getValue();

            double totalRevenue = details.stream()
                    .mapToDouble(detail -> detail.getDonGia() * detail.getSoLuong())
                    .sum();
            int totalQuantity = details.stream()
                    .mapToInt(OrderDetail::getSoLuong)
                    .sum();
            double maxPrice = details.stream()
                    .mapToDouble(detail -> detail.getDonGia())
                    .max()
                    .orElse(0.0);
            double minPrice = details.stream()
                    .mapToDouble(detail -> detail.getDonGia())
                    .min()
                    .orElse(0.0);
            double avgPrice = totalQuantity > 0 ? totalRevenue / totalQuantity : 0.0;

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRevenue", totalRevenue);
            stats.put("totalQuantity", totalQuantity);
            stats.put("maxPrice", maxPrice);
            stats.put("minPrice", minPrice);
            stats.put("avgPrice", avgPrice);
            categoryStats.put(categoryName, stats);
        }

        return categoryStats;
    }

    // Thống kê 10 khách hàng VIP (dựa trên tổng tiền đã mua)
    public List<Map<String, Object>> getTopVipCustomers() {
        List<User> users = userRepository.findAll();
        Map<User, Double> userTotalSpending = new HashMap<>();
        Map<User, Date> firstPurchase = new HashMap<>();
        Map<User, Date> lastPurchase = new HashMap<>();

        for (User user : users) {
            List<Order> orders = orderRepository.findByUser(user);
            if (!orders.isEmpty()) {
                double total = orders.stream()
                        .mapToDouble(Order::getTongGiaTri)
                        .sum();
                userTotalSpending.put(user, total);

                Date first = orders.stream()
                        .map(Order::getNgayOrder)
                        .min(Date::compareTo)
                        .orElse(null);
                Date last = orders.stream()
                        .map(Order::getNgayOrder)
                        .max(Date::compareTo)
                        .orElse(null);

                firstPurchase.put(user, first);
                lastPurchase.put(user, last);
            }
        }

        // Sắp xếp theo tổng tiền giảm dần và lấy top 10
        return userTotalSpending.entrySet().stream()
                .sorted(Map.Entry.<User, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Map<String, Object> customerInfo = new HashMap<>();
                    customerInfo.put("name", entry.getKey().getHoVaTen());
                    customerInfo.put("totalSpending", entry.getValue());
                    customerInfo.put("firstPurchase", firstPurchase.get(entry.getKey()));
                    customerInfo.put("lastPurchase", lastPurchase.get(entry.getKey()));
                    return customerInfo;
                })
                .collect(Collectors.toList());
    }

    public Order findbyOrders(int orderId) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order != null) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
            order.setOrderDetails(orderDetails);
            for (OrderDetail detail : orderDetails) {
                if (detail.getProduct() != null) {
                    detail.getProduct().getImageURL();
                }
            }
        }
        return order;
    }

    public Order handleSaveOrder(Order order) {
        Order ord = this.orderRepository.save(order);

        return ord;
    }

    @Transactional
    public void deleteOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Xóa tất cả OrderDetail trước khi xóa Order
        orderDetailRepository.deleteAll(order.getOrderDetails());

        // Xóa Order
        orderRepository.delete(order);
    }

}
