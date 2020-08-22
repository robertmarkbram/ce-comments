package com.rmb.cecomments;

import com.rmb.cecomments.error.CommentException;
import com.rmb.cecomments.model.Comment;
import com.rmb.cecomments.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

import static org.springframework.util.StringUtils.isEmpty;

@Component
@Slf4j
public class CommentsRunner implements CommandLineRunner {

   /**
    * The Comment service.
    */
   private final CommentService commentService;

   /**
    * Instantiates a new CommentsRunner.
    *
    * @param commentService the comment service
    */
   @Autowired
   public CommentsRunner(final CommentService commentService) {
      this.commentService = commentService;
   }

   @Override
   public void run(String... args) {
      log.info("EXECUTING : command line runner");

      final Scanner scanner = new Scanner(System.in);
      boolean userWantsToKeepGoing = true;

      while (userWantsToKeepGoing) {
         displayMenu();
         final int userChoice = getUserChoice(scanner);
         switch (userChoice) {
            case 1 -> createNewComment(scanner);
            case 2 -> showAllComments();
            case 3 -> {
               System.out.println("Exiting...");
               userWantsToKeepGoing = false;
            }
            default -> System.out.println("Invalid option. Please try again.");
         }
      }

      scanner.close();
   }

   /**
    * Show all comments.
    */
   private void showAllComments() {
      System.out.printf("%n%nListing all comments.%n");

      final List<Comment> comments = commentService.findAll();
      for (int index = 0; index < comments.size(); index++) {
         System.out.printf("%4d: %s%n", index, comments.get(index));
      }
   }

   /**
    * Display menu.
    */
   private void displayMenu() {
      System.out.printf("%n%n---%nMenu%n---%n%nEnter an option and press ENTER.%n");
      System.out.printf("1. Enter new comment.%n");
      System.out.printf("2. Display all comments.%n");
      System.out.printf("3. Exit.%n%n");
   }

   /**
    * Gets user choice: an integer.
    *
    * @param scanner the scanner we read user input from
    *
    * @return the user choice as in int or -1 if it was an invalid number.
    */
   private int getUserChoice(final Scanner scanner) {

      final String choiceString = scanner.nextLine();

      if (isEmpty(choiceString)) {
         return -1;
      }

      try {
         return Integer.parseInt(choiceString);
      } catch (NumberFormatException e) {
         return -1;
      }
   }

   /**
    * Create new comment.
    *
    * @param scanner the scanner
    */
   private void createNewComment(final Scanner scanner) {
      System.out.printf("%n%nComment text cannot be empty and must be between 1 - 200 characters.%n");
      System.out.println("Enter comment text and then press ENTER:");
      final String comment = scanner.nextLine();
      try {
         final Comment saved = commentService.save(comment);
         System.out.printf("Saved comment %d.", saved.getId());
      } catch (CommentException e) {
         System.out.printf("Invalid entry. %s", e.getMessage());
      }
   }

}
