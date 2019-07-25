package petclinic

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class ApplicationSwaggerConfig {

    private val apiInfo: ApiInfo
        get() = ApiInfoBuilder()
            .title("REST Petclinic backend Api Documentation")
            .version("1.0")
            .description("Petclinic backend terms of service")
            .contact(
                Contact(
                    "Intuit Kotlin Community",
                    "https://github.com/jbristow/spring-petclinic-rest-kotlin",
                    null
                )
            ).termsOfServiceUrl("")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
            .license("Apache 2.0")
            .build()

    @Bean
    fun customDocket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo)
    }
}
