# Spring JPA Command Line Runner

This section: [Spring JPA Command Line Runner](readme.md#spring-jpa-command-line-runner)

A relatively simple example of a `Spring Boot` command line runner that writes to a DB using `JPA`. This application shows a CLI (command line interface) menu, letting the user enter "comments" into a database and view them.

When a `@SpringBootApplication` sees any `org.springframework.boot.CommandLineRunner` implementations, it doesn't start a server but runs those command line runner classes.

This app is in my GitHub: [ce-comments](https://github.com/robertmarkbram/ce-comments)

Resources: 

- [Spring Boot Console Application](https://www.baeldung.com/spring-boot-console-app)

# Creating the app

This section: [Creating the app](readme.md#creating-the-app)

Instructions below.

1. Generate the base app using [Spring Initializr](https://start.spring.io/)
    1. [Link to base app](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.3.3.RELEASE&packaging=jar&jvmVersion=14&groupId=com.rmb&artifactId=ce-comments&name=ce-comments&description=Example%20of%20a%20Spring%20Boot%20app%20Command%20Line%20Runner%20using%20JPA.&packageName=com.rmb.ce-comments&dependencies=data-jpa,h2,mysql,lombok), which uses
        1. Spring Data JPA.
        2. H2 Database for an in-memory test DB.
        3. MySQL Driver for a PROD DB.
        4. Lombok.
        5. Java 14.
    2. Expanded to `C:\Users\Robert Bram\work\personal_projects\Coding-Exercises\ce-comments`

        ```
        .
        |-- ce-comments.iml
        |-- HELP.md
        |-- mvnw
        |-- mvnw.cmd
        |-- pom.xml
        `-- src
            |-- main
            |   |-- java
            |   |   `-- com
            |   |       `-- rmb
            |   |           `-- cecomments
            |   |               `-- CeCommentsApplication.java
            |   `-- resources
            |       `-- application.properties
            `-- test
                `-- java
                    `-- com
                        `-- rmb
                            `-- cecomments
                                `-- CeCommentsApplicationTests.java

        12 directories, 8 files
        ```

2. [Create the database](readme.md#create-the-database).
3. The `pom.xml` file: [pom.xml](readme.md#pomxml)
4. Create the [Domain Object](readme.md#domain-object) - a `Comment`.
5. Create the [Repository](readme.md#repository) for accessing comments in a data store.
    1. [Test the repository](readme.md#test-the-repository)
6. Create the [Service](readme.md#service), which is the _business layer of logic_ that 
    1. Should be the only thing accessing the DAO layer (the repository).
    2. Should be the only thing implementing `@Transactional` methods.
    3. [Test the service](readme.md#test-the-service)
7. Create the [Spring Boot Application](readme.md#spring-boot-application).
8. Create a [Command Line Runner](readme.md#command-line-runner).
9.  Set up [Logging configuration](readme.md#logging-configuration).
10. Set up [Application properties](readme.md#application-properties).
11. Set up [Test application properties](readme.md#test-application-properties).
12. [Build and run the app](readme.md#build-and-run-the-app).

# Create the database

This section: [Create the database](readme.md#create-the-database)

I have an SQL file, `tools\db\createDb.sql`, to create the database.

```sql
DROP DATABASE IF EXISTS comments;
DROP USER IF EXISTS 'comments-user'@'localhost';
CREATE DATABASE comments;
CREATE USER 'comments-user'@'localhost' IDENTIFIED WITH mysql_native_password BY '7#@aO*&W^u*8C8T29HyK7foOqd$euzi2jFc5SgP#';
GRANT ALL PRIVILEGES ON comments.* TO 'comments-user'@'localhost';
USE `comments`;
```

and run it in MySQL Workbench.

# pom.xml

This section: [pom.xml](readme.md#pomxml)

1. Added `org.apache.commons:commons-lang3`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.rmb</groupId>
    <artifactId>ce-comments</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>ce-comments</name>
    <description>Example of a Spring Boot app Command Line Runner using JPA.</description>

    <properties>
        <java.version>14</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.11</version>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

# Domain Object

This section: [Domain Object](readme.md#domain-object)

A comment.

```java
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
```

**Notes**.

1. `@Builder.Default` enables a default value to be used when the object is created through lombok's builder interfaces and via the `new` keyword.

# Repository

This section: [Repository](readme.md#repository)

```java
package com.rmb.cecomments.repo;

import com.rmb.cecomments.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Comment repository.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
```

**Notes**.

1. This is a super-simple repository - I am only going to use the default `findAll()` and `save()` methods.

## Test the repository

This section: [Test the repository](readme.md#test-the-repository)

```java
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
   void testSaving() {
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
   void testFindAll() {
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
```

**Notes**.

1. This test depends on [Test application properties](readme.md#test-application-properties) to ensure that it uses an in-memory H2 database.
2. The `@DataJpaTest` annotation in this test is **very important**.
    1. In [Testing in Spring Boot](https://www.baeldung.com/spring-boot-testing), you can see various annotations to test parts of a Spring Boot application, specifically:
        1. `@DataJpaTest`
        2. `@SpringBootTest`
    2. Normally I go straight ahead and use `@SpringBootTest` by default, which will set up the complete Spring Context. But in this example, something very bad goes wrong if I use `@SpringBootTest`:
        1. As Spring scans the class path for all the beans it needs to instantiate for the Spring Context, it picks up **and then runs** the [Spring JPA Command Line Runner](readme.md#spring-jpa-command-line-runner)! This is bad - the command line runner asks displays a command line UI, asks for user input etc. We don't want to do that during this test - all we want is access to the repository and database part of Spring.
3. If the `@SpringBootApplication` also `implements CommandLineRunner`, the situation is still bad even if I use `@DataJpaTest`:
    1. The Spring Context still tries to pick up `@SpringBootApplication` object and because it is only looking for JPA related beans, doesn't instantiate `@Service CommentService`, which generates an error because the `@SpringBootApplication` class now has an unsatisfied dependency for the service object.
    2. So it is better to have two separate classes for `@SpringBootApplication` and the one that `implements CommandLineRunner`.
        1. It's also worth nothing that you can have several `CommandLineRunner`s in a single app. Running a `@SpringBootApplication` will look for all `CommandLineRunner`s from the package tree in which it belongs, so if you need to do several jobs you can do it like that.


# Service

This section: [Service](readme.md#service)

The service is simple enough, just offering a single query to find all comments and a method to save a comment.

```java
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
```

**Notes**.

1. At this level of simplicity (both in service and application design), we don't need interfaces here. Interfaces don't give us any advantage here.
2. Validation is handled in the `save()` method.
    1. In a more complicated application, we could use a validation framework and annotate fields on the domain object with validation rules.
        1. See [Java Bean Validation Basics](https://www.baeldung.com/javax-validation).
        2. See: [Coding Exercise: Spring Form with JPA, Validation and front-end presentation](https://github.com/robertmarkbram/ce-spring-form).

## Test the service

This section: [Test the service](readme.md#test-the-service)

Test the service.

```java
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
```

**Notes**.

1. This is still a **unit test**. It is not relying on a Spring Context, database, external files etc and as such will be _quick_.
2. The key here is that I am **not testing the database** i.e. I am mocking the repository object.
    1. This is useful for testing the `com.rmb.cecomments.service.CommentService#save` method, because it actually has logic in it.
        1. It means I need to be careful to mock the repository so that it gives back results I would expect from the real repository.
    2. Testing the `com.rmb.cecomments.service.CommentService#findAll` is a bit different, because this service method is just a pass-through method: it has no logic of it's own and just calls the repository method, returning whatever it returns.
        1. In this case, there is no logic that I am testing except for one thing - verify that the repository method actually gets called.


# Spring Boot Application

This section: [Spring Boot Application](readme.md#spring-boot-application)

```java
package com.rmb.cecomments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CeCommentsApplication {

   /**
    * The entry point of application.
    *
    * @param args the input arguments
    */
   public static void main(String[] args) {
      log.info("STARTING THE APPLICATION");
      SpringApplication.run(CeCommentsApplication.class, args);
      log.info("APPLICATION FINISHED");
   }

}
```

**Notes**.

1. There is no logic here, but a heck of a lot going on under the covers thanks to `Spring Boot`.
2. In this example, we are not creating a server - so Spring will look for `CommandLineRunner`s and run them.

# Command Line Runner

This section: [Command Line Runner](readme.md#command-line-runner)

This is the guts of the application - it runs a simple command line UI to allow a use to enter "comments" into a database and see a list of them.

```java
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
```

**Notes**.

1. I am using `System.out` a lot here, as well as a logger. I have made a deliberate separation here between where output is seen.
    1. Things I want the user to see on the console goes through `System.out`.
    2. Logging I might use to debug the application etc goes to a log (and not the console), as per [logging configuration](readme.md#logging-configuration).
2. In this excellent resource: [Spring Boot Console Application](https://www.baeldung.com/spring-boot-console-app), it shows the `@SpringBootApplication` also being a `CommandLineRunner`.
    1. That makes the application much simpler, but has side effects on JPA tests, as seen in [Test the repository](readme.md#test-the-repository).

# Logging configuration

This section: [Logging configuration](readme.md#logging-configuration)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOGS" value="./logs"/>

    <appender name="Console"
              class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </pattern>
        </encoder>
    </appender>

    <appender name="RollingFile"
              class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOGS}/comments.log</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%d %p %C{1.} [%t] %m%n</Pattern>
        </encoder>

        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- rollover daily -->
            <fileNamePattern>${LOGS}/comments-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- each file should be at most 10MB, keep 60 days worth of history, but at most 20GB -->
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>60</maxHistory>
            <totalSizeCap>100GB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="RollingFile"/>
    </root>

    <!-- LOG "com.rmb.cecomments*" at DEBUG level -->
    <logger name="com.rmb.cecomments" level="debug" additivity="false">
        <appender-ref ref="RollingFile"/>
    </logger>

</configuration>

```

**Notes**.

1. In a normal web-app, I would have my loggers write to the `Console`, but since this is a command line app, I have loggers write to file only.
2. I have a test logging configuration file as well, `src/test/resources/logback-spring.xml`, where I have all logging go to console and a log file, so I can see all logging in my IDE etc.

    ```xml
    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="RollingFile"/>
        <appender-ref ref="Console"/>
    </root>

    <!-- LOG "com.rmb.cecomments*" at DEBUG level -->
    <logger name="com.rmb.cecomments" level="debug" additivity="false">
        <appender-ref ref="RollingFile"/>
        <appender-ref ref="Console"/>
    </logger>
    ```

# Application properties

This section: [Application properties](readme.md#application-properties)

```properties
debug=false
# Tells Spring that we really really don't want to run a web-app.. 
spring.main.web-application-type=none

spring.datasource.url=jdbc:mysql://localhost:3306/comments?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC
spring.datasource.username=comments-user
# suppress inspection "SpellCheckingInspection"
spring.datasource.password=7#@aO*&W^u*8C8T29HyK7foOqd$euzi2jFc5SgP#

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
# Will save data across re-boots and attempt to update schema if changed - for DEV.
spring.jpa.hibernate.ddl-auto=update
# For PROD
# spring.jpa.hibernate.ddl-auto=none
# suppress inspection "SpellCheckingInspection"
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
```

**Notes**.

1. I specify a MySQL database when running the app. Tests will use an in-memory H2 database.
2. I have specified `spring.jpa.hibernate.ddl-auto=update`, which will let Spring/Hibernate update the database if it detects any changes in the domain objects. (`@Entity` objects.)
    1. This is generally ok when you are developing the application, but once it is in Production, it isn't such a good idea. You generally want database changes to be much more controlled so you can see when changes were made, who made them and be able to roll them back etc.
    2. Consider tools such as [Liquibase](https://www.liquibase.org/) or [Flyway](https://flywaydb.org/).
        1. Or at the very least, keep DDL and DML in the script that [creates the database](readme.md#create-the-database).
            1. DDL is Data Definition Language - SQL that creates the schema etc.
            2. DML is Data Manipulation Language - SQL that creates data.
    3. Turn off auto-update with this property instead: `spring.jpa.hibernate.ddl-auto=none`.

# Test application properties

This section: [Test application properties](readme.md#test-application-properties)

```properties
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
```

**Notes**.

1. These properties ensure that tests will use an in-memory H2 database.

# Build and run the app

This section: [Build and run the app](readme.md#build-and-run-the-app)

Compile the project.

```bash
./mvnw clean package
```

Run it with either of these.

```bash
./mvnw spring-boot:run # through maven
java -jar target/ce-comments-0.0.1-SNAPSHOT.jar # running the jar.
```
