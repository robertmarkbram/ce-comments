package com.rmb.cecomments.repo;

import com.rmb.cecomments.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Comment repository.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
