package com.shihui.openpf.living.exception;

/**
 * Created by GSC on 2016/1/14.
 */
public class SimpleRuntimeException extends RuntimeException{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6366295190725151791L;
	private Integer errorCode;
	private String errorMsg;

    public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 默认构造，一般用于获取动态方法堆栈
     */
    @SuppressWarnings("unused")
	private SimpleRuntimeException() {
        super();
    }

    /**
     * 根据异常信息构造异常
     *
     * @param message
     */
    public SimpleRuntimeException(String message) {
        super(message);
        this.errorMsg = message;
    }
    
    /**
     * 根据异常信息构造异常
     *
     * @param message
     */
    public SimpleRuntimeException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }

    /**
     * 屏蔽异常堆栈
     */
    public Throwable fillInStackTrace() {
        return null;
    }
}
