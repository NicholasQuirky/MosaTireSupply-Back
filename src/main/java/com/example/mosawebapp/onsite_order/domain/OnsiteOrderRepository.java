package com.example.mosawebapp.onsite_order.domain;

import com.example.mosawebapp.kiosk.domain.Kiosk;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OnsiteOrderRepository extends JpaRepository<OnsiteOrder, String> {
  @Query("SELECT order FROM OnsiteOrder order WHERE order.isBeingOrdered = true")
  List<OnsiteOrder> findByIsBeingOrderedStatusAsTrue();

  @Query(value = "SELECT oo.* FROM onsite_order oo "
      + "INNER JOIN thread_type tt ON tt.id = oo.thread_type_id "
      + "INNER JOIN thread_type_details ttd ON ttd.id = oo.details_id "
      + "WHERE is_being_ordered = true", nativeQuery = true)
  List<OnsiteOrder> findByIsBeingOrderedStatusAsTrueAndWithExistingIds();

  @Query("SELECT order FROM OnsiteOrder order WHERE order.isBeingOrdered = true "
      + "AND order.type = :threadType AND order.details = :threadTypeDetails And order.isPaid = false")
  OnsiteOrder findByIsBeingOrderedStatusAndTypeAndDetailsAndNotCheckedOut(@Param("threadType") ThreadType threadType, @Param("threadTypeDetails")
  ThreadTypeDetails threadTypeDetails);

  @Query("SELECT order FROM OnsiteOrder order WHERE order.id = :id")
  OnsiteOrder findOrderById(@Param("id") String id);

  @Query(value = "SELECT ons.* FROM onsite_order ons "
      + "INNER JOIN orders o ON o.onsite_order_id = ons.id "
      + "INNER JOIN thread_type_details ttd ON ttd.id = ons.details_id "
      + "INNER JOIN thread_type tt ON tt.id = ons.thread_type_id "
      + "WHERE o.order_id = :orderId", nativeQuery = true)
  List<OnsiteOrder> findAllOnsiteOrdersByOrderId(@Param("orderId") String orderId);
}
