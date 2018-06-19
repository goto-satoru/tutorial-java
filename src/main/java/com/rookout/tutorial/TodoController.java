package com.rookout.tutorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class TodoController {
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    private TodoStorage todos = TodoStorage.getInstance();

    @RequestMapping(value = "/todos", method = RequestMethod.GET)
    public TodoRecord[] getTodos() {
        return todos.getAll();
    }

    @RequestMapping(value = "/todos", method = RequestMethod.POST)
    public ResponseEntity<?> addTodo(@RequestBody TodoRecord newTodoRecord) {
        newTodoRecord.setId(UUID.randomUUID().toString());
        logger.info("Adding a new todo: {}", newTodoRecord);
        todos.add(newTodoRecord);
        Map<String, String> entities = new HashMap<>();
        entities.put("status", "ok");
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @RequestMapping(value = "/todos", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTodo(@RequestBody TodoRecord updatingTodoRecord) {
        TodoRecord tempTodoRecord = todos.findById(updatingTodoRecord.getId());
        if (tempTodoRecord != null) {
            tempTodoRecord.setTitle(updatingTodoRecord.getTitle());
            tempTodoRecord.setCompleted(updatingTodoRecord.isCompleted());
            logger.info("Updating Todo record: {}", tempTodoRecord);
        }
        Map<String, String> entities = new HashMap<>();
        entities.put("status", "ok");
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @RequestMapping(value = "/todos/{todoId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTodo(@PathVariable("todoId") String todoId) {
        logger.info("Removing Todo record id: {}", todoId);
        TodoRecord tempTodoRecord = todos.findById(todoId);
        if (tempTodoRecord != null) {
            logger.info("Removing Todo record: {}", tempTodoRecord);
            todos.remove(tempTodoRecord);
        }
        Map<String, String> entities = new HashMap<>();
        entities.put("status", "ok");
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @RequestMapping(value = "/todos/clear_completed", method = RequestMethod.DELETE)
    public ResponseEntity<?> clearCompletedTodos() {
        logger.info("Removing completed todo records");
        for (TodoRecord todoRecord : todos.getAll()) {
            if (todoRecord.isCompleted()) {
                if (todos.remove(todoRecord)) {
                    logger.info("Removing Todo record: {}", todoRecord);
                }
            }
        }
        Map<String, String> entities = new HashMap<>();
        entities.put("status", "ok");
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @RequestMapping(value = "/todos/dup/{todoId}", method = RequestMethod.POST)
    public ResponseEntity<?> duplicateTodo(@PathVariable("todoId") String todoId) {
        logger.info("Duplicating todo: {}", todoId);
        TodoRecord tempTodoRecord = todos.findById(todoId);
        if (tempTodoRecord != null) {
            TodoRecord newTodoRecord = new TodoRecord(tempTodoRecord);
            newTodoRecord.setId(UUID.randomUUID().toString());
            logger.info("Duplicating todo record: {}", tempTodoRecord);
            todos.add(newTodoRecord);
        }
        Map<String, String> entities = new HashMap<>();
        entities.put("status", "ok");
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }
}