package com.example.assignment.global.config;

import java.util.Iterator;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Todolist API")
				.version("0.0.1"))
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
			.components(new Components()
				.addSecuritySchemes("bearerAuth", new SecurityScheme()
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")));
	}

	@Bean
	public OpenApiCustomiser hideCpUserParameter() {
		return openApi -> openApi.getPaths().forEach((path, pathItem) ->
			pathItem.readOperations().forEach(operation -> {
				if (operation.getParameters() != null) {
					for (Iterator<Parameter> iterator = operation.getParameters().iterator(); iterator.hasNext();) {
						Parameter parameter = iterator.next();
						if ("amUser".equals(parameter.getName())) {
							iterator.remove();
						}
					}
				}
			})
		);
	}
}
