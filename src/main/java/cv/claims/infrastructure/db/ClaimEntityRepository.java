package cv.claims.infrastructure.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
interface ClaimEntityRepository extends JpaRepository<ClaimEntity, Long> {

    @Query("""
            SELECT c
            FROM claim c
            WHERE c.processStatus = :status
                AND c.deadline <= :deadline""")
    List<ClaimEntity> findAllByProcessStatusAndDeadlineBeforeEquals(@Param("status") ProcessStatus processStatus,
                                                                    @Param("deadline") LocalDate deadline,
                                                                    Pageable pageable);

    @Query("""
            SELECT SUM(c.amount)
            FROM claim c
            WHERE c.processedAt IS NOT NULL
                AND DATE(c.processedAt) = :date""")
    // ?? not sure if claims with a future deadline should be processed if daily budget allows it
    Optional<BigDecimal> getProcessedAmountByDay(@Param("date") LocalDate date);
}
