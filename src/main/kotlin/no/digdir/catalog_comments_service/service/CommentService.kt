package no.digdir.catalog_comments_service.service

import no.digdir.catalog_comments_service.model.Comment
import no.digdir.catalog_comments_service.model.CommentDBO
import no.digdir.catalog_comments_service.model.UserDBO
import no.digdir.catalog_comments_service.repository.CommentDAO
import no.digdir.catalog_comments_service.repository.UserDAO
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private val logger = LoggerFactory.getLogger(CommentService::class.java)

@Service
class CommentService (private val commentDAO: CommentDAO, private val userDAO: UserDAO) {

    private fun createUserIfNotExists(userId: String, name: String? = null, email: String? = null) {
        try {
            if (!userDAO.existsById(userId)) {
                val userDocument = UserDBO(id = userId, name = name, email = email)
                userDAO.insert(userDocument)
            }
        } catch (ex: Exception) {
            logger.error("insert user failed", ex)
        }
    }

    fun insert(comment: Comment, orgNumber: String, topicId: String, userId: String, name: String? = null, email: String? = null): Comment? {

        createUserIfNotExists(userId, name, email)

        if (!userDAO.existsById(userId)) {
            throw object : Exception("User not found") {}
        }
        val newComment: CommentDBO = comment.mapForCreation(orgNumber, topicId, userId)

        return commentDAO
            .insert(newComment )
            ?.let { it.toDTO(userDAO.findById(userId)) }
    }

    fun getCommentsByOrgNumber(orgNumber: String): List<Comment> = commentDAO.findCommentsByOrgNumber(orgNumber)
        .map { it.toDTO(userDAO.findById(it.user)) }

    fun getCommentsByOrgNumberAndTopicId(orgNumber: String, topicId: String): List<Comment> =
        commentDAO.findCommentsByOrgNumberAndTopicId(orgNumber, topicId)
            .map { it.toDTO(userDAO.findById(it.user)) }

    fun getCommentDBO(id: String): CommentDBO? =
        commentDAO.findByIdOrNull(id)

    fun updateComment(commentId: String, obj: Comment, userId: String): Comment? =
        commentDAO.findByIdOrNull(commentId)
            ?.copy(
                comment = obj.comment ?: ""
            )
            ?.updateLastChanged()
            ?.let { commentDAO.save(it) }
            ?.let { it.toDTO(userDAO.findById(userId)) }
}
