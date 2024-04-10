package com.example.mosawebapp.all_orders.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Query(value = "SELECT od.* "
        + "FROM orders od "
        + "LEFT JOIN cart ct ON ct.id = od.cart_id "
        + "LEFT JOIN kiosk k ON k.id = od.kiosk_id "
        + "LEFT JOIN onsite_order oo ON oo.id = od.onsite_order_id "
        + "INNER JOIN thread_type_details ttd ON ttd.id = ct.details_id OR ttd.id = k.details_id OR ttd.id = oo.details_id "
        + "INNER JOIN thread_type tt ON tt.id = ttd.thread_type_id", nativeQuery = true)
    List<Orders> findAllWithExistingIds();
    @Query(value = "SELECT order_status FROM orders WHERE cart_id = :cartId", nativeQuery = true)
    String findOrderByCartId(@Param("cartId") String cartId);

    @Query(value = "SELECT o.* FROM orders o "
        + "INNER JOIN kiosk k ON k.id = o.kiosk_id "
        + "WHERE k.token = :token", nativeQuery = true)
    List<Orders> findOrdersByKioskToken(@Param("token") String token);

    List<Orders> findByOrderId(String orderId);
}
