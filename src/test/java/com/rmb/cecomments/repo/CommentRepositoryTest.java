package com.rmb.cecomments.repo;

import com.rmb.cecomments.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test the repository.
 */
@DataJpaTest
@Slf4j
class CommentRepositoryTest {

   /**
    * The Comment repository.
    */
   @Autowired
   private CommentRepository commentRepository;

   /**
    * Test saving.
    */
   @Test
   public void testSaving() {
      final String text = "abc";
      Comment comment = Comment.builder()
            .text(text)
            .build();
      final Comment saved = commentRepository.save(comment);
      assertNotNull(saved, "Saved object should not be null");
      assertEquals(text, saved.getText());
      assertNotNull(saved.getCreated());
      assertNotNull(saved.getId());
   }

   /**
    * Test find all.
    */
   @Test
   public void testFindAll() {
      final String text = "abc";
      commentRepository.save(Comment.builder().text(text).build());
      commentRepository.findAll();
      commentRepository.save(Comment.builder().text(text).build());
      commentRepository.findAll();
      commentRepository.save(Comment.builder().text(text).build());
      commentRepository.findAll();
      commentRepository.save(Comment.builder().text(text).build());
      final List<Comment> all = commentRepository.findAll();
      log.info("Search results: {}", all);
      final int expectedSize = 4;
      assertEquals(expectedSize, all.size(), "Expected " + expectedSize + " results.");
   }


}
