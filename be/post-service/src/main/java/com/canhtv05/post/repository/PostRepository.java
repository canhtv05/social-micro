package com.canhtv05.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.canhtv05.post.entity.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

  Page<Post> findAllByUserId(String userId, Pageable pageable);

}
