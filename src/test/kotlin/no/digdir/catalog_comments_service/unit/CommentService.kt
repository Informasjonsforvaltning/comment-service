package no.digdir.catalog_comments_service.unit

import no.digdir.catalog_comments_service.repository.CommentDAO
import no.digdir.catalog_comments_service.repository.UserDAO
import no.digdir.catalog_comments_service.service.CommentService
import no.digdir.catalog_comments_service.service.toDBO
import no.digdir.catalog_comments_service.utils.ApiTestContext
import no.digdir.catalog_comments_service.utils.COMMENT_0
import no.digdir.catalog_comments_service.utils.COMMENT_1
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

@Tag("unit")
class CommentService: ApiTestContext() {
    private val commentDAO: CommentDAO = mock()
    private val userDAO: UserDAO = mock()
    private val commentService = CommentService(commentDAO, userDAO)

    @Test
    fun `Get all comments by topic id` () {
        whenever(commentDAO.findCommentsByOrgNumberAndTopicId("246813579","topicId0"))
            .thenReturn(listOf(COMMENT_0, COMMENT_1).map { it.toDBO("246813579","topicId0","1924782563") })

        val result = commentService.getCommentsByOrgNumberAndTopicId("246813579", "topicId0")

        assertTrue { result.size == 2 }
    }
}
