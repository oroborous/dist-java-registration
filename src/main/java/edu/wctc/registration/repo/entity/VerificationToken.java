package edu.wctc.registration.repo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private User user;

    private LocalDateTime expiryDate;

    public VerificationToken(final String token, final User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private LocalDateTime calculateExpiryDate(int expiryTimeInMinutes) {
//        return LocalDateTime.now().plus(expiryTimeInMinutes, ChronoUnit.MINUTES);
        // tokens expire in 10 seconds to test resending
        return LocalDateTime.now().plusSeconds(10);
    }

    public void updateToken(String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
