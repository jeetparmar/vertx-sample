package com.vertx;

import com.vertx.api.UserHandler;
import com.vertx.service.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {

    UserService userService = new UserService(vertx);
    UserHandler userHandler = new UserHandler(userService);

    Router mainRouter = Router.router(vertx);

    // 1️⃣ Serve OpenAPI YAML
    mainRouter.get("/openapi.yaml").handler(ctx ->
      ctx.response()
        .putHeader("Content-Type", "application/yaml")
        .sendFile("openapi.yaml")
    );

    // 2️⃣ Swagger UI via CDN (NO STATIC FILES)
    mainRouter.get("/docs").handler(ctx -> {

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

      ctx.response()
        .putHeader("Content-Type", "text/html")
        .end(html);
    });

    // 3️⃣ OpenAPI Router
    OpenAPI3RouterFactory.create(vertx, "openapi.yaml")
      .onSuccess(factory -> {

        factory.addHandlerByOperationId("getUsers", userHandler::getUsers);
        factory.addHandlerByOperationId("createUser", userHandler::createUser);
        factory.addHandlerByOperationId("getUserById", userHandler::getUserById);

        mainRouter.mountSubRouter("/api", factory.getRouter());

        vertx.createHttpServer()
          .requestHandler(mainRouter)
          .listen(8080);

        System.out.println("Server running at http://localhost:8080");
        System.out.println("Swagger UI at http://localhost:8080/docs");
      })
      .onFailure(Throwable::printStackTrace);
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
