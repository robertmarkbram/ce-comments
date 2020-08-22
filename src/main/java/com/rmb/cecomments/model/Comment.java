package com.rmb.cecomments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Comment.
 */
@Builder(toBuilder = true)
@Data
// We are saving instances of this object via JPA
@Entity
/* - JPA/JSON tools needs a no-args constructor.
   - So does @Data.
   - They instantiate an empty bean and use setters to init data.
 */
@NoArgsConstructor(force = true)
// @Builder needs an all-args constructor.
@AllArgsConstructor
public class Comment {

   /**
    * How to format dates when printing comments.
    */
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

   /**
    * The ID.
    */
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;

   /**
    * Date at which the comment was created.
    */
   @NonNull
   @Builder.Default
   private LocalDateTime created = LocalDateTime.now();

   /**
    * Comment text.
    */
   @NonNull
   private String text;

   @Override
   public String toString() {
      return "Created at: " + DATE_TIME_FORMATTER.format(created) +
            ". Comment: " + text;
   }

}
