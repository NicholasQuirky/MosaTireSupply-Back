package com.example.mosawebapp.cart.domain;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
  List<Cart> findByAccount(Account account);
  @Query(value = "SELECT ct.* FROM cart ct "
      + "INNER JOIN thread_type tt ON tt.id = ct.thread_type_id "
      + "INNER JOIN thread_type_details ttd ON ttd.id = ct.details_id "
      + "WHERE customer_id = :customerId AND is_paid = false", nativeQuery = true)
  List<Cart> findByAccountAndIsNotPaid(@Param("customerId") String customerId);
  @Query(value = "SELECT ct.* FROM cart ct "
      + "INNER JOIN thread_type tt ON tt.id = ct.thread_type_id "
      + "INNER JOIN thread_type_details ttd ON ttd.id = ct.details_id "
      + "WHERE customer_id = :customerId AND is_paid = true", nativeQuery = true)
  List<Cart> findByAccountAndIsPaid(@Param("customerId") String customerId);
  Cart findByIdAndAccount(String id, Account account);
  @Query("SELECT c FROM Cart c WHERE c.account = :account AND c.type = :threadType AND c.details = :threadTypeDetails And c.isCheckedOut = false")
  Cart findByAccountAndTypeAndDetailsAndNotCheckedOut(@Param("account") Account account, @Param("threadType") ThreadType threadType, @Param("threadTypeDetails")
  ThreadTypeDetails threadTypeDetails);

  @Query(value = "SELECT * FROM cart WHERE is_checked_out = true AND is_paid = true", nativeQuery = true)
  List<Cart> findAllCheckedOutAndPaidCarts();

  @Query(value = "SELECT * FROM cart WHERE id = :cartId", nativeQuery = true)
  Cart findCartById(@Param("cartId") String cartId);

  @Query(value = "SELECT c.* FROM cart c "
      + "INNER JOIN orders o ON o.cart_id = c.id "
      + "INNER JOIN thread_type_details ttd ON ttd.id = c.details_id "
      + "INNER JOIN thread_type tt ON tt.id = c.thread_type_id "
      + "WHERE o.order_id = :orderId", nativeQuery = true)
  List<Cart> findAllCartsByOrderId(@Param("orderId") String orderId);
}
