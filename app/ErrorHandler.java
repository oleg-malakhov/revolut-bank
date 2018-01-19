import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.google.inject.Singleton;

import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import ru.legatus.revolut.controllers.ApiResponse;

@Singleton
public class ErrorHandler implements HttpErrorHandler {
   @Override
   public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
      ApiResponse<Void> apiResponse = new ApiResponse<>(statusCode, message, null);
      return CompletableFuture.supplyAsync(() -> Results.status(statusCode, Json.toJson(apiResponse)));
   }

   @Override
   public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
      ApiResponse<String> apiResponse = new ApiResponse<>(Http.Status.INTERNAL_SERVER_ERROR, exception.getMessage(), exception.toString());
      return CompletableFuture.supplyAsync(() -> Results.status(Http.Status.INTERNAL_SERVER_ERROR, Json.toJson(apiResponse)));
   }
}
