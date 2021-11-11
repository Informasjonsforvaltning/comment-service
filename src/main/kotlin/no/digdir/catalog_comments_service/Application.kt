package no.digdir.catalog_comments_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
open class CommentApplication

fun main(args: Array<String>) {
    runApplication<CommentApplication>(*args)
}
