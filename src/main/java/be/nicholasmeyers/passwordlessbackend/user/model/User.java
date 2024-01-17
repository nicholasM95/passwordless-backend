package be.nicholasmeyers.passwordlessbackend.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    @Column(name = "id")
    private UUID id;
    private String username;
    @Column(length = 1000000)
    @Lob
    private String publicKeyJson;
    @Column(length = 1000000)
    @Lob
    private String assertion;
    private byte[] userHandle;
    private boolean registrationComplete;
}
