package com.navercorp.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.navercorp.board.constant.Const;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private CertificationInterceptor certificationInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(certificationInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/")
				.excludePathPatterns("/error")
				.excludePathPatterns("/js/**")
				.excludePathPatterns("/css/**")
				.excludePathPatterns("/img/**")
				.excludePathPatterns("/login")
				.excludePathPatterns("/signUp")
				.excludePathPatterns("/api/login")
				.excludePathPatterns("/api/signUp")
				.excludePathPatterns("/api/groups/filtering")
				.excludePathPatterns("/group/details/*")
				.excludePathPatterns("/search/**");
	}
	
	/**
	 * 운영체제에 따라 Resources location을 다르게 설정한다.
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    String OS = BoardApplication.OS;
	    if (OS.indexOf("win") >= 0) {
	    	registry.addResourceHandler("/img/**").addResourceLocations(Const.WINDOWS_FILE_DIRECTORY);
	    } else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0) {
		registry.addResourceHandler("/img/**").addResourceLocations(Const.LINUX_FILE_DIRECTORY);
	    }
	}
}
