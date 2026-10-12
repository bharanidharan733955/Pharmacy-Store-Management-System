package com.pharmacy.order.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String orderId;
    private final String customerName;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(String orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Order(String orderId, String customerName, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    @Override
    public String toString() {
        return String.format("Order[ID=%s, Customer='%s', Status=%s, Items=%d, Total=₹%.2f]",
                orderId, customerName, status, items.size(), getTotalAmount());
    }
}
