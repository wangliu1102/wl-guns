package com.wl.guns.modular.system.controller;

import cn.hutool.core.util.StrUtil;
import com.wl.guns.core.util.file.FileUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件请求处理
 *
 * @author 王柳
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileController {
	private static final Logger log = LoggerFactory.getLogger(FileController.class);

	/**
	 * 通用下载请求
	 *
	 * @param fileName 文件名称
	 * @param delete   是否删除
	 */
	@GetMapping("/download")
	public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
		try {
			if (!FileUtils.isValidFilename(fileName)) {
				throw new Exception(StrUtil.format("文件名称({})非法，不允许下载。 ", fileName));
			}
			String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
			String filePath = FileUtils.getFileUploadPath("") + fileName;

			response.setCharacterEncoding("utf-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition", "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, realFileName));
			FileUtils.writeBytes(filePath, response.getOutputStream());
			if (delete) {
				FileUtils.deleteFile(filePath);
			}
		} catch (Exception e) {
			log.error("下载文件失败", e);
		}
	}

	/**
	 * 本地资源通用下载
	 */
	@GetMapping("/download/resource")
	public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
		throws Exception
	{
		// 本地资源路径
		String localPath = FileUtils.getFileUploadPath("");
		// 数据库资源地址
		String downloadPath = localPath + resource;
		// 下载名称
		String downloadName = StrUtil.subAfter(downloadPath, "/",true);
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition",
			"attachment;fileName=" + FileUtils.setFileDownloadHeader(request, downloadName));
		FileUtils.writeBytes(downloadPath, response.getOutputStream());
	}
}
