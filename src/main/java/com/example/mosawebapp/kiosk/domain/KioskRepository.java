package com.example.mosawebapp.kiosk.domain;

import com.example.mosawebapp.cart.domain.Cart;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KioskRepository extends JpaRepository<Kiosk, String> {
  List<Kiosk> findByToken(String token);

  Kiosk findByIdAndToken(String id, String token);
  @Query(value = "SELECT * FROM kiosk WHERE token = :kioskToken LIMIT 1", nativeQuery = true)
  Kiosk findKioskByToken(@Param("kioskToken") String kioskToken);

  @Query("SELECT k FROM Kiosk k WHERE k.token = :kioskToken AND k.isCheckedOut = false")
  List<Kiosk> findNotCheckedOutKioskByToken(@Param("kioskToken") String kioskToken);

  /*@Query("SELECT k FROM Kiosk k WHERE k.queueingNumber = (SELECT MAX(k2.queueingNumber) FROM Kiosk k2)")
  Kiosk findLatestQueueingNumber();*/

  @Query(value = "SELECT * FROM kiosk k WHERE EXISTS (SELECT 1 FROM thread_type_details t WHERE t.id = k.details_id) "
      + "ORDER BY k.date_created DESC LIMIT 1", nativeQuery = true)
  Kiosk findLatestQueueingNumber();

  @Query("SELECT k FROM Kiosk k WHERE k.token = :token AND k.type = :threadType AND k.details = :threadTypeDetails And k.isCheckedOut = false")
  Kiosk findByTokenAndTypeAndDetailsAndNotCheckedOut(@Param("token") String token, @Param("threadType") ThreadType threadType, @Param("threadTypeDetails")
  ThreadTypeDetails threadTypeDetails);

  @Query(value = "SELECT k.* FROM kiosk k "
      + "INNER JOIN orders o ON o.kiosk_id = k.id "
      + "INNER JOIN thread_type_details ttd ON ttd.id = k.details_id "
      + "INNER JOIN thread_type tt ON tt.id = k.thread_type_id "
      + "WHERE o.order_id = :orderId", nativeQuery = true)
  List<Kiosk> findAllKiosksByOrderId(@Param("orderId") String orderId);

  @Query(value = "SELECT k.* FROM kiosk k "
      + "INNER JOIN orders o ON o.kiosk_id = k.id "
      + "WHERE o.order_status = 'ORDER_COMPLETED'", nativeQuery = true)
  List<Kiosk> findCompletedOrders();

  @Query(value = "SELECT k.* FROM kiosk k "
      + "INNER JOIN orders o ON o.kiosk_id = k.id "
      + "WHERE o.order_status = 'PROCESSING'", nativeQuery = true)
  List<Kiosk> findProcessingOrders();
}
