package no.digdir.catalog_comments_service.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import no.digdir.catalog_comments_service.model.CommentDBO

@Repository
interface CommentDAO:MongoRepository<CommentDBO,String>{
    fun findCommentsByOrgNumber(orgNumber:String):List<CommentDBO>
    fun findCommentsByOrgNumberAndTopicId(orgNumber:String, topicId: String):List<CommentDBO>
    fun findByTopicId(topicId:String):List<CommentDBO>
}
