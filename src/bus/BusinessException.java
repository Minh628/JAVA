/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BusinessException - Exception tầng nghiệp vụ
 * Giúp tách biệt BUS layer khỏi SQLException (tầng DAO)
 */
package bus;

/**
 * Custom exception cho tầng Business Logic
 * Giúp tuân thủ kiến trúc 3 tầng bằng cách tách biệt exception của DAO layer
 * 
 * Extends RuntimeException (unchecked) vì:
 * - Các lỗi SQLException thường là system errors (database không khả dụng, lỗi kết nối)
 * - GUI layer không thể xử lý được các lỗi này một cách có ý nghĩa
 * - Tránh bắt buộc GUI phải try-catch mọi method call
 * - Cho phép lỗi lan truyền lên và được xử lý ở global exception handler
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
