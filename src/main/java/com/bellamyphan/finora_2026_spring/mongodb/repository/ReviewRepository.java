package com.bellamyphan.finora_2026_spring.mongodb.repository;

import com.bellamyphan.finora_2026_spring.mongodb.entity.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
}
