package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Contraseñas con PBKDF2-HMAC-SHA256 y sal propia por usuario.
 * Formato guardado en la BD:  pbkdf2$<sal en base64>$<hash en base64>  (76 caracteres).
 */
public final class PasswordUtil {

    private static final String PREFIJO = "pbkdf2$";
    private static final int ITERACIONES = 65_000;
    private static final int BITS = 256;

    private PasswordUtil() { }

    public static boolean esHash(String guardado) {
        return guardado != null && guardado.startsWith(PREFIJO);
    }

    public static String hashear(String contrasena) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return PREFIJO + Base64.getEncoder().encodeToString(sal) + "$"
                + Base64.getEncoder().encodeToString(derivar(contrasena, sal));
    }

    public static boolean verificar(String contrasena, String guardado) {
        if (!esHash(guardado)) {
            return false;
        }
        String[] partes = guardado.substring(PREFIJO.length()).split("\\$");
        if (partes.length != 2) {
            return false;
        }
        byte[] sal = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        return MessageDigest.isEqual(esperado, derivar(contrasena, sal));
    }

    private static byte[] derivar(String contrasena, byte[] sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        }
    }
}
