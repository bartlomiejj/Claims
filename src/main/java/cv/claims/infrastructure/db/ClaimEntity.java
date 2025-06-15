package cv.claims.infrastructure.db;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Entity(name = "claim")
@Table(name = "claim")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "claimId")
class ClaimEntity {

    @Id
    @SequenceGenerator(name = "claim_id_seq", sequenceName = "claim_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Nonnull
    private String claimId;

    @Nonnull
    @Enumerated(value = STRING)
    private ClaimType type;

    @Nonnull
    @Column(precision = 10, scale = 4)
    private BigDecimal amount;

    @Nonnull
    private LocalDate deadline;

    @Nonnull
    @Enumerated(value = STRING)
    private Complexity complexity;

    @Nonnull
    @Enumerated(value = STRING)
    private ProcessStatus processStatus;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;
}
