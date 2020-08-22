package com.rmb.cecomments.service;

import com.rmb.cecomments.error.CommentException;
import com.rmb.cecomments.model.Comment;
import com.rmb.cecomments.repo.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * The type Comment service test.
 */
class CommentServiceTest {

   /**
    * The Comment repository that underlies the service.
    */
   @Mock
   CommentRepository commentRepository;

   /**
    * The Comment service being tested.
    */
   CommentService commentService;

   /**
    * Sets up.
    */
   @BeforeEach
   void setUp() {
      initMocks(this);
      commentService = new CommentService(commentRepository);
   }

   /**
    * Find all.
    */
   @Test
   void testFindAll() {
      // Set up test data - we don't care about the result.. the service method is just a pass-through.
      when(commentRepository.findAll()).thenReturn(Collections.emptyList());
      // Call the method under test.
      commentService.findAll();
      // What we care about is that it called the underlying repository method once.
      verify(commentRepository, times(1)).findAll();
   }

   /**
    * Test save with valid data.
    */
   @Test
   void testSave() throws CommentException {
      // Set up test data.
      when(commentRepository.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());

      // Save comment - just the max length.
      final String commentText = "12345678901234567890123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890";
      final Comment saved = commentService.save(commentText);
      assertNotNull(saved, "Saved comment should not be null.");
      assertEquals(commentText, saved.getText());
   }

   /**
    * Test save with null/blank text fails.
    */
   @Test
   void testSaveWithBlankText() {
      // Save comment with bad data.
      Exception exception = assertThrows(CommentException.class, () -> commentService.save(null));
      assertEquals("Comment text cannot be empty.", exception.getMessage());
      exception = assertThrows(CommentException.class, () -> commentService.save(""));
      assertEquals("Comment text cannot be empty.", exception.getMessage());
      exception = assertThrows(CommentException.class, () -> commentService.save("   "));
      assertEquals("Comment text cannot be empty.", exception.getMessage());
   }

   /**
    * Test save with long text fails.
    */
   @Test
   void testSaveWithLongText() {
      // Save comment with bad data - one character too many.
      Exception exception = assertThrows(CommentException.class, () -> commentService.save("12345678901234567890123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890"
            + "12345678901234567890123456789012345678901234567890"
            + "1"));
      assertEquals("Comment text too long (201 characters long). It must be between 1 - 200 characters long.",
            exception.getMessage());
   }


}
