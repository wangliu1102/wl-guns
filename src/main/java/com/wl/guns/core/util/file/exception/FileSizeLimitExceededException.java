package com.wl.guns.core.util.file.exception;

import lombok.NoArgsConstructor;

/**
 * 文件名大小限制异常类
 *
 * @author 王柳
 */
@NoArgsConstructor
public class FileSizeLimitExceededException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FileSizeLimitExceededException(String message) {
		super(message);
	}

	public FileSizeLimitExceededException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileSizeLimitExceededException(Throwable cause) {
		super(cause);
	}

	public FileSizeLimitExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
