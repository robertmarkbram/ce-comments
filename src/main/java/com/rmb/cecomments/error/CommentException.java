package com.rmb.cecomments.error;

/**
 * Error for the Comment application..
 */
public class CommentException extends Exception {

   /**
    * Instantiates a new Comment exception.
    *
    * @param message the message
    */
   public CommentException(final String message) {
      super(message);
   }

   /**
    * Instantiates a new Comment exception.
    *
    * @param message the message
    * @param cause   the cause
    */
   public CommentException(final String message, final Throwable cause) {
      super(message, cause);
   }

   /**
    * Instantiates a new Comment exception.
    *
    * @param cause the cause
    */
   public CommentException(final Throwable cause) {
      super(cause);
   }

   /**
    * Instantiates a new Comment exception.
    *
    * @param message            the message
    * @param cause              the cause
    * @param enableSuppression  the enable suppression
    * @param writableStackTrace the writable stack trace
    */
   public CommentException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

}
