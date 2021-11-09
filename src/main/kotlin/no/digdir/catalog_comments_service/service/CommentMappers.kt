package no.digdir.catalog_comments_service.service

import no.digdir.catalog_comments_service.model.Comment
import no.digdir.catalog_comments_service.model.CommentDBO
import no.digdir.catalog_comments_service.model.UserDBO
import java.time.LocalDateTime
import java.util.*

fun CommentDBO.toDTO(userDBO: Optional<UserDBO>): Comment =
    Comment(
        id = id,
        createdDate = createdDate,
        topicId = topicId,
        orgNumber = orgNumber,
        user = userDBO,
        comment = comment
    )

fun Comment.toDBO(orgNr: String, id: String, userId: String): CommentDBO {
    val newCreatedDate = LocalDateTime.now()

    return CommentDBO(
        id = id,
        createdDate = newCreatedDate!!,
        topicId = id,
        orgNumber = orgNr,
        user = userId,
        comment = comment
    )
}

fun Comment.mapForCreation(orgNumber: String, topicId: String, user: String): CommentDBO {
    val newId = UUID.randomUUID().toString()
    val newCreatedDate = LocalDateTime.now()

    return CommentDBO(
        id = newId,
        createdDate = newCreatedDate,
        topicId = topicId,
        orgNumber = orgNumber,
        user = user,
        comment = comment
    )
}
