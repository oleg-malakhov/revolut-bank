package ru.legatus.revolut.services.jdbi;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import com.google.inject.Inject;

import akka.actor.ActorSystem;
import scala.Function0;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class DatabaseExecutionContext implements ExecutionContextExecutor {
   private final ExecutionContext executionContext;

   private static final String name = "database.dispatcher";

   @Inject
   public DatabaseExecutionContext(ActorSystem actorSystem) {
      this.executionContext = actorSystem.dispatchers().lookup(name);
   }

   public <R> CompletionStage<R> call(Function0<R> f) {
      return supplyAsync(f::apply, this);
   }

   @Override
   public ExecutionContext prepare() {
      return executionContext.prepare();
   }

   @Override
   public void execute(Runnable command) {
      executionContext.execute(command);
   }

   @Override
   public void reportFailure(Throwable cause) {
      executionContext.reportFailure(cause);
   }
}
