package no.digdir.catalog_comments_service.controller

import no.digdir.catalog_comments_service.model.Comment
import no.digdir.catalog_comments_service.security.EndpointPermissions
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

private val logger = LoggerFactory.getLogger(CommentController::class.java)

@RestController
@CrossOrigin
@RequestMapping("/{orgNumber}/{topicId}/comment")
class CommentController (private val endpointPermissions: EndpointPermissions) {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable orgNumber: String
    ): ResponseEntity<Comment> {
        val userId = endpointPermissions.getUserId(jwt)
        return when {
            userId == null -> ResponseEntity<Comment>(HttpStatus.UNAUTHORIZED)
            !endpointPermissions.hasOrgReadPermission(jwt, orgNumber) ->
                ResponseEntity<Comment>(HttpStatus.FORBIDDEN)
            else -> ResponseEntity<Comment>(HttpStatus.CREATED)
        }
    }

    @GetMapping
    fun getComments(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable orgNumber: String
    ): ResponseEntity<List<Comment>> {
        return when {
            !endpointPermissions.hasOrgReadPermission(jwt, orgNumber) -> ResponseEntity<List<Comment>>(HttpStatus.FORBIDDEN)
            else -> ResponseEntity<List<Comment>>(HttpStatus.OK)
        }
    }
}
