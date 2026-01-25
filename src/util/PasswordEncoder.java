/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: PasswordEncoder - Mã hóa mật khẩu
 */
package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordEncoder {
    
    /**
     * Mã hóa mật khẩu bằng SHA-256
     */
    public static String encode(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Trả về mật khẩu gốc nếu lỗi
        }
    }
    
    /**
     * Kiểm tra mật khẩu có khớp không
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        String encoded = encode(rawPassword);
        return encoded.equals(encodedPassword);
    }
    
    /**
     * Tạo mật khẩu ngẫu nhiên
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
