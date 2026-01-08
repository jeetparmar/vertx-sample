package com.vertx;

import com.vertx.api.UserHandler;
import com.vertx.service.UserService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() {

		UserService userService = new UserService(vertx);
		UserHandler userHandler = new UserHandler(userService);

		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		router.get("/users").handler(userHandler::getUsers);
		router.post("/users").handler(userHandler::createUser);
		router.get("/users/:id").handler(userHandler::getUserById);

		vertx.createHttpServer().requestHandler(router).listen(8080);

		System.out.println("Server running on port 8080");
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(new MainVerticle());
	}
}
