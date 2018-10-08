package org.pinguin.gf.service.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Classe para habilitar o Swagger e contém suas as configurações Para usar o
 * Swagger: 1 - Suba a aplicação normalmente (Spring boot) 2 - No browser digite
 * a URL do swagger -> localhost:8080/swagger-ui.html 3 - Entre com o token
 * retornado do serviço de login Exemplo: Bearer eyJhbGciOiJIUz...x... 4 -
 * Execute o respectivo serviço de forma gráfica
 *
 */
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

	/**
	 * Mapeia o pacote os estão os serviço para o sewagger scan e seta as
	 * configurações de seguraça necessárias para autenticação
	 * 
	 * @return
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("org.pinguin.gf.service")).paths(PathSelectors.any()).build()
				.pathMapping("/").apiInfo(metaData());
	}

	/**
	 * COnfigura a identidade do Swagger
	 * 
	 * @return
	 */
	private ApiInfo metaData() {
		return new ApiInfoBuilder().title("GFinanceiro:. REST API")
				.description("\"GFinanceiro:. Spring Boot REST API \"").version("1.0.0").build();
	}

}
