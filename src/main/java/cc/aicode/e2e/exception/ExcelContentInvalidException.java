package cc.aicode.e2e.exception;

/**
 * 内容规则校验异常
 *
 */
public class ExcelContentInvalidException extends Exception {
    private static final long serialVersionUID = 1L;
    private String message;

    public ExcelContentInvalidException(Exception e) {
        initCause(e);
    }

    public ExcelContentInvalidException() {
    }

    public ExcelContentInvalidException(String message) {
        this.message = message;
    }

    public ExcelContentInvalidException(String message, Exception e) {
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
