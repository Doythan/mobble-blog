package org.example.mobble._util.config;

import org.example.mobble._util.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/user/**")
                .addPathPatterns("/board/**")
//                .addPathPatterns("/love/**")
//                .addPathPatterns("/reply/**")
                .addPathPatterns("/api/**")
                // Board 상세보기 : session 인증 없이 볼 수 있어야 함 - 글의 수정/삭제와 주소가 겹침 >> 정규 표현식
                // 정규 표현식 : ChatGPT에 검색!!
                .excludePathPatterns("/board/{id:\\d+}")
                .excludePathPatterns("/check-username-available/**");
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/"); // static 아래 전부

        registry.addResourceHandler("/profile/**")
                .addResourceLocations("file:///C:/workspace/mobble-blog/src/main/resources/static/profile/")
                .setCachePeriod(0);

        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///C:/workspace/mobble-blog/src/main/resources/static/img/")
                .setCachePeriod(0);
    }

}
