package com.vertx;

import com.vertx.api.UserHandler;
import com.vertx.service.UserService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class MainVerticle extends AbstractVerticle {

	private static final int PORT = 8080;
	private static final String JWT_SECRET = "super-secret-key";

	@Override
	public void start() {
		// 1️⃣ Create JWT Auth
		JWTAuth jwtAuth = createJwtAuth();

		// 2️⃣ Initialize services and handlers
		UserService userService = new UserService(vertx);
		UserHandler userHandler = new UserHandler(userService, jwtAuth);
		JWTAuthHandler jwtHandler = JWTAuthHandler.create(jwtAuth);

		// 3️⃣ Create main router
		Router mainRouter = Router.router(vertx);

		// 4️⃣ Serve OpenAPI YAML
		mainRouter.get("/openapi.yaml")
				.handler(ctx -> ctx.response().putHeader("Content-Type", "application/yaml").sendFile("openapi.yaml"));

		// 5️⃣ Serve Swagger UI
		mainRouter.get("/docs").handler(ctx -> serveSwaggerUI(ctx));

		// 6️⃣ Setup OpenAPI routes
		setupOpenApiRoutes(mainRouter, userHandler, jwtHandler);

		// 7️⃣ Start HTTP server
		vertx.createHttpServer().requestHandler(mainRouter).listen(PORT, ar -> {
			if (ar.succeeded()) {
				System.out.println("Server running at http://localhost:" + PORT);
				System.out.println("Swagger UI at http://localhost:" + PORT + "/docs");
			} else {
				ar.cause().printStackTrace();
			}
		});
	}

	// ------------------- Main -------------------
	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(new MainVerticle());
	}

	// ------------------- Helper Methods -------------------
	private JWTAuth createJwtAuth() {
		return JWTAuth.create(vertx,
				new JWTAuthOptions().addPubSecKey(new PubSecKeyOptions().setAlgorithm("HS256").setBuffer(JWT_SECRET)));
	}

	private void serveSwaggerUI(RoutingContext ctx) {
		String html = """
				<!DOCTYPE html>
				<html>
				<head>
				  <meta charset="UTF-8">
				  <title>Swagger UI</title>
				  <link rel="stylesheet"
				        href="https://unpkg.com/swagger-ui-dist/swagger-ui.css">
				</head>
				<body>
				  <div id="swagger-ui"></div>

				  <script src="https://unpkg.com/swagger-ui-dist/swagger-ui-bundle.js"></script>
				  <script src="https://unpkg.com/swagger-ui-dist/swagger-ui-standalone-preset.js"></script>

				  <script>
				    window.onload = () => {
				      SwaggerUIBundle({
				        url: '/openapi.yaml',
				        dom_id: '#swagger-ui',
				        deepLinking: true,
				        presets: [
				          SwaggerUIBundle.presets.apis,
				          SwaggerUIStandalonePreset
				        ],
				      });
				    };
				  </script>
				</body>
				</html>
				""";
		ctx.response().putHeader("Content-Type", "text/html").end(html);
	}

	private void setupOpenApiRoutes(Router mainRouter, UserHandler userHandler, JWTAuthHandler jwtHandler) {
		OpenAPI3RouterFactory.create(vertx, "openapi.yaml").onSuccess(factory -> {
			// 1️⃣ JWT security for secured endpointsÏÍ
			factory.addSecurityHandler("bearerAuth", jwtHandler);

			// 2️⃣ Map operations
			factory.addHandlerByOperationId("login", userHandler::login);
			factory.addHandlerByOperationId("getUsers", userHandler::getUsers);
			factory.addHandlerByOperationId("createUser", userHandler::createUser);
			factory.addHandlerByOperationId("getUserById", userHandler::getUserById);

			// 3️⃣ Mount OpenAPI router under /api
			mainRouter.mountSubRouter("/api", factory.getRouter());

		}).onFailure(Throwable::printStackTrace);
	}
}
