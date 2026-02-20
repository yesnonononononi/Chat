package com.summit.chat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Swagger/OpenAPI 配置类（兼容Spring Boot 4.x + SpringDoc OpenAPI）
 * 替代传统Springfox Swagger2，无需@EnableSwagger2注解
 */
@Configuration
public class SwaggerConfig {

    /**
     * 核心配置：文档基础信息（标题、版本、联系人等）
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 定义全局Token请求头（示例：Bearer Token认证）
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization")
                .in(SecurityScheme.In.HEADER);

        // 服务器环境配置（可配置多环境：开发/测试/生产）
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("开发环境");

        Server testServer = new Server();
        testServer.setUrl("http://test-api.example.com");
        testServer.setDescription("测试环境");

        return new OpenAPI()
                .openapi("3.0.1")
                // 全局安全配置（启用Token请求头）
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", securityScheme)
                        // 全局公共请求头（示例：终端类型）
                        .addParameters("terminalType", new HeaderParameter()
                                .name("terminal-type")
                                .description("终端类型（web/app/ios/android）")
                                .required(false)
                                .example("web")))
                // 文档基础信息
                .info(new Info()
                        .title("XX项目API文档") // 替换为你的项目名称
                        .description("XX项目接口文档：包含用户、订单、支付等模块接口") // 替换为项目描述
                        .version("v1.0.0") // 接口版本
                        // 联系人信息
                        .contact(new Contact()
                                .name("开发团队") // 联系人
                                .email("dev@example.com") // 联系邮箱
                                .url("https://example.com")) // 联系链接
                        // 许可证信息（可选）
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                // 服务器环境
                .servers(Collections.singletonList(devServer)) // 可添加testServer、prodServer
                // 全局响应配置（示例：统一401/403/500响应说明）
                .extensions(Collections.singletonMap(
                        "x-global-responses",
                        Collections.singletonMap("401", "未授权，请携带Token请求")
                ));
    }

    /**
     * 接口分组配置：按模块拆分文档（示例：用户模块）
     * 可创建多个GroupedOpenApi Bean实现多模块分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户模块") // 分组名称（Swagger UI中显示）
                .pathsToMatch("/api/v1/user/**") // 匹配该路径下的接口
                .packagesToScan("com.example.project.controller.user") // 扫描指定包下的Controller
                .addOpenApiCustomizer(openApiCustomizer()) // 自定义扩展
                .build();
    }

    /**
     * 接口分组配置：订单模块（可复制扩展其他模块）
     */
    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("订单模块")
                .pathsToMatch("/api/v1/order/**")
                .packagesToScan("com.example.project.controller.order")
                // 排除指定接口（示例：排除删除订单接口）
                .pathsToExclude("/api/v1/order/delete/**")
                .build();
    }

    /**
     * 自定义扩展：给分组接口添加统一标签
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi.getTags().add(
                new io.swagger.v3.oas.models.tags.Tag()
                        .name("通用说明")
                        .description("所有接口均返回JSON格式，统一响应码：200成功/4xx客户端错误/5xx服务端错误")
        );
    }
}