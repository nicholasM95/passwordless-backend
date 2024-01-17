package be.nicholasmeyers.passwordlessbackend.user.data;

import be.nicholasmeyers.passwordlessbackend.user.model.Passkey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PasskeyRepository extends JpaRepository<Passkey, UUID> {
    List<Passkey> findAllByUserHandle(byte[] userHandle);

    List<Passkey> findAllByKeyId(byte[] keyId);

    Optional<Passkey> findByUserHandleAndKeyId(byte[] userHandle, byte[] keyId);
}
