/**
 * 
 */
package com.expert.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.expert.constant.SysConfig;
import com.expert.listener.FileUploadProgressListener.Progress;
import com.expert.service.CacheService;
import com.expert.util.FileCacheUtil;
import com.expert.util.FilePathUtil;
import com.expert.util.ImageAnalyseUtil;
import com.expert.util.ZipUtil;

/**
 * @Desc 利用SpringMVC监听进度。优点：进度较准确 。缺点：不过只有整体，对应关系不确定
 * @author wenge.yan
 * @Date 2016年7月4日
 * @ClassName FileController
 */
@Controller
@RequestMapping("file")
public class FileController {
	private static final long MAX_FILE_SIZE = 1000 * 1024 * 1024L;

	private final static Logger logger = Logger.getLogger(FileController.class);

	@Autowired
	private CacheService cacheService;

	@RequestMapping(value = "progress", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Map<String, String> getProgress(HttpServletRequest request) {
		String[] fileSizes = request.getParameterValues("fileSizes");
		Object attribute = request.getSession().getAttribute("upload_progress");
		Map<String, String> progressMap = new HashMap<String, String>();
		if (attribute != null) {
			Progress progress = (Progress) attribute;
			long size = 0;
			for (int i = 0; i < progress.getItems() - 1; i++) {
				progressMap.put("progress" + i, "100");
				size += Long.valueOf(fileSizes[i]);
			}
			String percent = String.valueOf((progress.getBytesRead() - size) * 100 / (progress.getContentLength() - size));
			progressMap.put("progress" + (progress.getItems() - 1), percent);
		} else {
			for (int i = 0; i < fileSizes.length - 1; i++) {
				progressMap.put("progress" + i, "100");
			}
		}
		logger.info("进度：" + progressMap);
		return progressMap;
	}

	@RequestMapping(value = "upload", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Map<String, String> uploadFiles(HttpServletRequest request, @RequestParam("files") MultipartFile[] files, ModelMap model) {
		logger.info("上传---------------start");
		String targetPath = cacheService.getSysConfig(SysConfig.FILE_PATH);

		// logger.info(System.getProperty("java.io.tmpdir"));

		Map<String, String> result = new HashMap<String, String>();

		// 文件大小过滤(前台也过滤)
		long totalSize = 0;
		for (MultipartFile file : files) {
			totalSize += file.getSize();
		}
		if (MAX_FILE_SIZE < totalSize) {
			result.put("result", "fail");
			result.put("error", "文件大小超过最大值" + MAX_FILE_SIZE / 1024 / 1024 + "M");
			return result;
		}

		List<FileCacheUtil.FileInfo> fileInfos = new ArrayList<FileCacheUtil.FileInfo>();
		for (MultipartFile file : files) {
			String originalName = file.getOriginalFilename();
			originalName = originalName.substring(originalName.lastIndexOf(File.separator) + 1);
			long size = file.getSize();
			File targetFile = FileCacheUtil.generateNameAfterUpload(targetPath, originalName);
			File tempFile = null;
			if (file instanceof CommonsMultipartFile) {
				CommonsMultipartFile cmf = (CommonsMultipartFile) file;
				tempFile = ((DiskFileItem) cmf.getFileItem()).getStoreLocation();
			}
			try {
				file.transferTo(targetFile);
			} catch (Exception e) {
				logger.error(file.toString() + ":" + e.getMessage());
				continue;
			}
			String targetName = targetFile.getName();
			if (originalName.matches(".*[(.jpg)(.png)(.gif)]$")) {
				targetName = ImageAnalyseUtil.generateFingerprint(targetFile);
				targetName += originalName.substring(originalName.lastIndexOf("."));
				targetFile.renameTo(new File(targetPath + "/" + targetName));
			}
			fileInfos.add(new FileCacheUtil.FileInfo(originalName, size, tempFile, targetName));
		}

		result.put("result", files.length > 0 ? "success" : "fail");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fileInfos.size(); i++) {
			if (i > 0) {
				sb.append(";");
			}
			sb.append(cacheService.getSysConfig(SysConfig.FILE_URL)).append("/").append(fileInfos.get(i).getTargetName());
		}

		result.put("targetNames", sb.toString());
		logger.info("上传-----------end:" + result);
		return result;
	}

	@RequestMapping(value = "download", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<byte[]> downloadFiles(HttpServletRequest request) {
		String[] fileNames = request.getParameterValues("files");
		Map<String, String> fileMap = (HashMap<String, String>) request.getSession().getAttribute("download");
		if (fileMap == null) {
			fileMap = new HashMap<String, String>();
		}
		String uploadFolder = cacheService.getSysConfig(SysConfig.FILE_PATH);
		String finalFileName = null;
		if (fileNames != null && fileNames.length == 1) {
			finalFileName = fileNames[0];
		} else if (fileNames != null && fileNames.length > 1) {
			List<File> files = new ArrayList<File>();
			for (String fileName : fileNames) {
				String trueFileName = fileMap.get(fileName);
				if (trueFileName != null) {
					File file = new File(uploadFolder + File.separator + trueFileName);
					files.add(file);
				}
				finalFileName = FilePathUtil.generateRandomFileName(10) + ".zip";
				ZipUtil.zipFilesByFiles(files, uploadFolder, finalFileName, true, false);
			}
		}
		HttpHeaders headers = new HttpHeaders();
		// try {
		// // 为了解决中文名称乱码问题
		// trueFileName = new String(trueFileName.getBytes("UTF-8"), "iso-8859-1");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		headers.setContentDispositionFormData("attachment", finalFileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		byte[] fileBytes = new byte[] {};
		try {
			String pathname = uploadFolder + File.separator + finalFileName;
			File file = new File(pathname);
			fileBytes = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<byte[]>(fileBytes, headers, HttpStatus.CREATED);
	}
}
