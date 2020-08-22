package com.rmb.cecomments.service;

import com.rmb.cecomments.error.CommentException;
import com.rmb.cecomments.model.Comment;
import com.rmb.cecomments.repo.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * The type Comment service. No need to code against an interface here because the service methods are simple enough.
 */
@Service
@Slf4j
public class CommentService {

   /**
    * The Comment repository.
    */
   private final CommentRepository commentRepository;

   /**
    * Instantiates a new Comment service.
    *
    * @param commentRepository the comment repository
    */
   @Autowired
   public CommentService(final CommentRepository commentRepository) {
      this.commentRepository = commentRepository;
   }

   /**
    * Find all comments as a list.
    *
    * @return the list of all comments
    */
   public List<Comment> findAll() {
      final List<Comment> all = commentRepository.findAll();
      log.info("Retrieving list of all {} comments.", all.size());
      return all;
   }

   /**
    * Save a comment.
    *
    * @param commentText text of the comment
    *
    * @return the comment just saved.
    *
    * @throws CommentException if the <code>commentText</code> is invalid.
    */
   @Transactional
   public Comment save(final String commentText) throws CommentException {
      log.debug("Request to save comment text: {}", commentText);

      if (isBlank(commentText)) {
         throw new CommentException("Comment text cannot be empty.");
      }
      if (commentText.length() > 200) {
         throw new CommentException("Comment text too long (" +
               commentText.length() + " characters long). It must be between 1 - 200 characters long.");
      }
      final Comment comment = Comment.builder()
            .text(commentText)
            .build();
      final Comment saved = commentRepository.save(comment);
      log.info("Saved comment: {}", saved);
      return saved;
   }

}
