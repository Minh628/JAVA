/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BusinessException - Exception tầng nghiệp vụ
 * Giúp tách biệt BUS layer khỏi SQLException (tầng DAO)
 */
package bus;

/**
 * Custom exception cho tầng Business Logic
 * Giúp tuân thủ kiến trúc 3 tầng bằng cách tách biệt exception của DAO layer
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
