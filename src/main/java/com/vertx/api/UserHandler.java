package com.vertx.api;

import com.vertx.model.User;
import com.vertx.service.UserService;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

public class UserHandler {

	private final UserService userService;
	private final JWTAuth jwtAuth;

	public UserHandler(UserService userService, JWTAuth jwtAuth) {
		this.userService = userService;
		this.jwtAuth = jwtAuth;
	}

	public void login(RoutingContext ctx) {
		JsonObject body = ctx.getBodyAsJson();
		if (body == null) {
			ctx.response().setStatusCode(400).end("Request body is missing or invalid JSON");
			return;
		}
		String username = body.getString("username");
		if (username == null) {
			ctx.response().setStatusCode(400).end("Username is required");
			return;
		}
		String token = jwtAuth.generateToken(new JsonObject().put("sub", username),
				new JWTOptions().setExpiresInMinutes(60));
		ctx.response().putHeader("Content-Type", "application/json").end(new JsonObject().put("token", token).encode());
	}

	public void getUsers(RoutingContext ctx) {
		userService.getAllUsers().onSuccess(users -> {
			JsonArray result = new JsonArray();
			users.forEach(u -> result.add(u.toJson()));
			ctx.json(result);
		}).onFailure(err -> ctx.fail(500, err));
	}

	public void createUser(RoutingContext ctx) {
		if (ctx.getBodyAsJson() == null) {
			ctx.response().setStatusCode(400).end("Invalid JSON");
			return;
		}

		User user = User.fromJson(ctx.getBodyAsJson());

		userService.createUser(user).onSuccess(created -> {
			ctx.response().setStatusCode(201);
			ctx.json(created.toJson());
		}).onFailure(err -> ctx.fail(500, err));
	}

	public void getUserById(RoutingContext ctx) {
		String id = ctx.pathParam("id");

		userService.getUserById(id).onSuccess(user -> ctx.json(user.toJson()))
				.onFailure(err -> ctx.response().setStatusCode(404).end(err.getMessage()));
	}
}
