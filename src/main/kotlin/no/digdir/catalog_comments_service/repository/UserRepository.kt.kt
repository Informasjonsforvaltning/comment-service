package no.digdir.catalog_comments_service.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import no.digdir.catalog_comments_service.model.UserDBO

@Repository
interface UserDAO:MongoRepository<UserDBO,String>
