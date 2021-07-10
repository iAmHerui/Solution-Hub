package com.h3c.solutionhub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileConfig {

	/**
	 * 文件上传配置
	 * springboot默认限制了上传文件最大为1M,需修改为50M
	 * 
	 * @return MultipartConfigElement
	 */
	@Bean
	public MultipartConfigElement multipartConfigElement(
			@Value("${multipart.maxFileSize}") String maxFileSize,
			@Value("${multipart.maxRequestSize}") String maxRequestSize) {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		// 单个文件最大
		factory.setMaxFileSize(DataSize.parse(maxFileSize));
		// 设置总上传数据总大小
		factory.setMaxRequestSize(DataSize.parse(maxRequestSize));
		return factory.createMultipartConfig();
	}

}
