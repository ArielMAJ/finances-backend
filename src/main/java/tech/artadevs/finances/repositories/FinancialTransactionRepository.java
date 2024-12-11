
package tech.artadevs.finances.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tech.artadevs.finances.models.FinancialTransaction;
import tech.artadevs.finances.models.User;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    @Query("SELECT ft FROM FinancialTransaction ft WHERE ft.user = :user AND ft.deletedAt IS NULL")
    List<FinancialTransaction> findByUser(User user);

    Optional<FinancialTransaction> findByIdAndUser(Long id, User user);

}
