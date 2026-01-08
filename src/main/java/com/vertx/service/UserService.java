package com.vertx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.vertx.model.User;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class UserService {

  private final Vertx vertx;
  private final Map<String, User> store = new ConcurrentHashMap<>();

  public UserService(Vertx vertx) {
    this.vertx = vertx;
  }

  public Future<List<User>> getAllUsers() {
    Promise<List<User>> promise = Promise.promise();

    vertx.setTimer(20, id ->
      promise.complete(new ArrayList<>(store.values()))
    );

    return promise.future();
  }

  public Future<User> createUser(User user) {
    Promise<User> promise = Promise.promise();

    vertx.setTimer(20, id -> {
      String userId = UUID.randomUUID().toString();
      user.setId(userId);
      store.put(userId, user);
      promise.complete(user);
    });

    return promise.future();
  }

  public Future<User> getUserById(String id) {
    Promise<User> promise = Promise.promise();

    vertx.setTimer(20, t -> {
      User user = store.get(id);
      if (user == null) {
        promise.fail("User not found");
      } else {
        promise.complete(user);
      }
    });

    return promise.future();
  }
}