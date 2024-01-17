package be.nicholasmeyers.passwordlessbackend.user.extension;

import com.yubico.webauthn.data.ClientExtensionOutputs;

import java.util.Collections;
import java.util.Set;

public class CustomClientExtensionOutput implements ClientExtensionOutputs {
    @Override
    public Set<String> getExtensionIds() {
        return Collections.emptySet();
    }
}
