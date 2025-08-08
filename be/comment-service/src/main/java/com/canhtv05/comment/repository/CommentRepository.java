package com.canhtv05.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.canhtv05.comment.entity.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

  Page<Comment> findByPostId(String postId, Pageable pageable);

  Page<Comment> findByParentId(String parentId, Pageable pageable);

}
