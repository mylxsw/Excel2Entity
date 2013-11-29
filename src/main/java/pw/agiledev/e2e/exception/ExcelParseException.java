package pw.agiledev.e2e.exception;
/**
 * 功能说明： Excel解析异常
 * 参数说明：
 * @author 管宜尧
 * 2013-11-28 下午4:46:59
 */
public class ExcelParseException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message  = null;
	
	public ExcelParseException(Exception e){
		initCause(e);
	}
	
	public ExcelParseException(String message){
		this.message = message;
	}
	
	public ExcelParseException(String message, Exception e){
		initCause(e);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		if(message == null){
			return super.getMessage();
		}
		return message;
	}
	
}
