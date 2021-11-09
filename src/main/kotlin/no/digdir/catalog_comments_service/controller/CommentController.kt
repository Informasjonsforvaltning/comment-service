package no.digdir.catalog_comments_service.controller

import no.digdir.catalog_comments_service.model.Comment
import no.digdir.catalog_comments_service.security.EndpointPermissions
import no.digdir.catalog_comments_service.service.CommentService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
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
class CommentController (private val endpointPermissions: EndpointPermissions, private val commentService: CommentService) {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable orgNumber: String,
        @PathVariable topicId: String,
        @RequestBody comment: Comment
    ): ResponseEntity<Comment> {
        val userId = endpointPermissions.getUserId(jwt)
        return when {
            userId == null -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            !endpointPermissions.hasOrgReadPermission(jwt, orgNumber) ->
                ResponseEntity(HttpStatus.FORBIDDEN)
            else -> {
                logger.info("creating comment for ${orgNumber}")
                commentService.insert(comment, orgNumber, topicId, userId, endpointPermissions.getUserName(jwt), endpointPermissions.getUserEmail(jwt))
                    ?.let { ResponseEntity(it, locationHeaderForCreated(it), HttpStatus.CREATED) }
                    ?: ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }

    @GetMapping
    fun getComments(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable orgNumber: String,
        @PathVariable topicId: String
    ): ResponseEntity<List<Comment>> {
        return when {
            !endpointPermissions.hasOrgReadPermission(jwt, orgNumber) -> ResponseEntity<List<Comment>>(HttpStatus.FORBIDDEN)
            else -> ResponseEntity<List<Comment>>(commentService.getCommentsByOrgNumberAndTopicId(orgNumber, topicId), HttpStatus.OK)
        }
    }
}

private fun locationHeaderForCreated(comment: Comment): HttpHeaders =
    HttpHeaders().apply {
        add(HttpHeaders.LOCATION, "/comment/${comment.id}")
        add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
    }
