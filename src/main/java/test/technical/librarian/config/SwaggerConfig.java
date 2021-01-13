package test.technical.librarian.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${app.version}")
    private String version;

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors
                        .basePackage("test.technical.librarian"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo() {

        return new ApiInfoBuilder().title("REST API Documentation")
                .description("Technical Test - Back End Developer PT. Kreasi Rumah Aplikasi")
                .contact(new Contact("Danang Sigit Prasetya", "https://www.jasaonline.net", "danangsigit@gmail.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0-SNAPSHOT")
                .build();
    }

}
