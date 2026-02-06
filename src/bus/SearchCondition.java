/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: SearchCondition - Điều kiện tìm kiếm nâng cao
 * Hỗ trợ các phép toán: =, <>, >, >=, <, <=, LIKE (contains)
 */
package bus;

public class SearchCondition {
    private String field;      // Tên trường
    private String operator;   // Toán tử: =, <>, >, >=, <, <=, LIKE
    private String value;      // Giá trị so sánh

    public SearchCondition() {}

    public SearchCondition(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    /**
     * Đánh giá điều kiện với giá trị trường từ dữ liệu
     */
    public boolean evaluate(String fieldValue) {
        if (fieldValue == null) fieldValue = "";
        if (value == null || value.isEmpty()) return true;

        String valueLower = value.toLowerCase();
        String fieldLower = fieldValue.toLowerCase();

        try {
            switch (operator) {
                case "=" -> {
                    return fieldLower.equals(valueLower);
                }
                case "<>" -> {
                    return !fieldLower.equals(valueLower);
                }
                case "LIKE" -> {
                    return fieldLower.contains(valueLower);
                }
                case ">" -> {
                    return compareNumeric(fieldValue, value) > 0;
                }
                case ">=" -> {
                    return compareNumeric(fieldValue, value) >= 0;
                }
                case "<" -> {
                    return compareNumeric(fieldValue, value) < 0;
                }
                case "<=" -> {
                    return compareNumeric(fieldValue, value) <= 0;
                }
                default -> {
                    return fieldLower.contains(valueLower);
                }
            }
        } catch (Exception e) {
            // Fallback to string comparison
            return fieldLower.contains(valueLower);
        }
    }

    /**
     * So sánh số 
     */
    private int compareNumeric(String fieldValue, String compareValue) {
        try {
            double fieldNum = Double.parseDouble(fieldValue.replaceAll("[^\\d.-]", ""));
            double compareNum = Double.parseDouble(compareValue.replaceAll("[^\\d.-]", ""));
            return Double.compare(fieldNum, compareNum);
        } catch (NumberFormatException e) {
            return fieldValue.compareTo(compareValue);
        }
    }

    @Override
    public String toString() {
        return field + " " + operator + " " + value;
    }
}
