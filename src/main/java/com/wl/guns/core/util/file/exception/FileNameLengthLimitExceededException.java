package com.wl.guns.core.util.file.exception;

import lombok.NoArgsConstructor;

/**
 * 文件名称超长限制异常类
 *
 * @author 王柳
 */
@NoArgsConstructor
public class FileNameLengthLimitExceededException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FileNameLengthLimitExceededException(String message) {
		super(message);
	}

	public FileNameLengthLimitExceededException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileNameLengthLimitExceededException(Throwable cause) {
		super(cause);
	}

	public FileNameLengthLimitExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
