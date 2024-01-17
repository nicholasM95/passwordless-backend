package be.nicholasmeyers.passwordlessbackend.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.util.UUID;

@Getter
@Setter
@Entity
public class Passkey {
    @Id
    @GeneratedValue(generator = "UUID")
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    @Column(name = "id")
    private UUID id;
    private byte[] userHandle;
    private byte[] publicKey;
    private byte[] keyId;
    private String type;
    private String transport;
    private long signatureCount;
}
