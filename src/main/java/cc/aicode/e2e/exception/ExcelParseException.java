package cc.aicode.e2e.exception;

/**
 * Excel解析异常
 */
public class ExcelParseException extends Exception {
    private static final long serialVersionUID = 1L;
    private String message;

    public ExcelParseException(Exception e) {
        initCause(e);
    }

    public ExcelParseException(String message) {
        this.message = message;
    }

    public ExcelParseException(String message, Exception e) {
        initCause(e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (message == null) {
            return super.getMessage();
        }
        return message;
    }

}
