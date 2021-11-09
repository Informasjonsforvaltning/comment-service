package no.digdir.catalog_comments_service.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.digdir.catalog_comments_service.utils.ApiTestContext
import no.digdir.catalog_comments_service.utils.authorizedRequest
import no.fdk.catalog_comments_service.utils.*
import no.fdk.concept_catalog.utils.jwk.Access
import no.fdk.concept_catalog.utils.jwk.JwtToken
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
val dateTimeSerializer = LocalDateTimeSerializer(formatter)

private val mapper: ObjectMapper = jacksonObjectMapper()
    .registerModule(
        JavaTimeModule()
            .addSerializer(LocalDateTime::class.java, dateTimeSerializer)
    )
    .registerModule(Jdk8Module ())

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=integration-test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class CommentIntegration : ApiTestContext() {

    @Test
    fun `Unauthorized when access token is not included`() {
        val rsp = authorizedRequest("/${ORG_NUMBER}/${TOPIC_ID}/comment", port, mapper.writeValueAsString(COMMENT_0), null, HttpMethod.POST)

        assertEquals(HttpStatus.UNAUTHORIZED.value(), rsp["status"])
    }

    @Test
    fun `Forbidden when comment has non write access orgId`() {
        val rsp = authorizedRequest(
            "/${WRONG_ORG_NUMBER}/${TOPIC_ID}/comment", port, mapper.writeValueAsString(COMMENT_0),
            JwtToken(Access.ORG_WRITE).toString(), HttpMethod.POST
        )

        assertEquals(HttpStatus.FORBIDDEN.value(), rsp["status"])
    }

    @Test
    fun `Ok - Created - for read access`() {
        val before = authorizedRequest(
            "/${ORG_NUMBER}/${TOPIC_ID}/comment", port, "",
            JwtToken(Access.ORG_READ).toString(), HttpMethod.GET
        )

        val rsp = authorizedRequest(
            "/${ORG_NUMBER}/${TOPIC_ID}/comment", port, mapper.writeValueAsString(COMMENT_TO_BE_CREATED),
            JwtToken(Access.ORG_READ).toString(), HttpMethod.POST
        )
        assertEquals(HttpStatus.CREATED.value(), rsp["status"])

        val after = authorizedRequest(
            "/${ORG_NUMBER}/${TOPIC_ID}/comment", port, "",
            JwtToken(Access.ORG_READ).toString(), HttpMethod.GET
        )

        val beforeList = mapper.readValue(before["body"] as String, List::class.java)
        val afterList = mapper.readValue(after["body"] as String, List::class.java)
        assertEquals(beforeList.size + 1, afterList.size)
    }

    @Test
    fun `Ok - Created - for write access`() {
        val before = authorizedRequest(
            "/${ORG_NUMBER}/${TOPIC_ID}/comment", port, "",
            JwtToken(Access.ORG_READ).toString(), HttpMethod.GET
        )

        val rsp = authorizedRequest(
            "/${ORG_NUMBER}/${TOPIC_ID}/comment", port, mapper.writeValueAsString(COMMENT_TO_BE_CREATED_1),
            JwtToken(Access.ORG_WRITE).toString(), HttpMethod.POST
        )
        assertEquals(HttpStatus.CREATED.value(), rsp["status"])

        val after = authorizedRequest(
            "/${ORG_NUMBER}/${TOPIC_ID}/comment", port, "",
            JwtToken(Access.ORG_READ).toString(), HttpMethod.GET
        )

        val beforeList = mapper.readValue(before["body"] as String, List::class.java)
        val afterList = mapper.readValue(after["body"] as String, List::class.java)
        assertEquals(beforeList.size + 1, afterList.size)
    }

    @Test
    fun `Forbidden for read access of comments when non read access orgId`() {
        val rsp = authorizedRequest(
            "/${WRONG_ORG_NUMBER}/${TOPIC_ID}/comment", port, "",
            JwtToken(Access.ORG_READ).toString(), HttpMethod.GET
        )

        assertEquals(HttpStatus.FORBIDDEN.value(), rsp["status"])
    }

    @Test
    fun `Read access of comments`() {
        val rsp = authorizedRequest(
            "/${ORG_NUMBER}/${TOPIC_ID}/comment", port, "",
            JwtToken(Access.ORG_READ).toString(), HttpMethod.GET
        )

        assertEquals(HttpStatus.OK.value(), rsp["status"])
    }
}
