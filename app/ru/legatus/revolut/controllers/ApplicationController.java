package ru.legatus.revolut.controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class ApplicationController extends Controller {

   public Result rootOptions() {
      return options("/");
   }

   public Result options(String url) {
      return ok().withHeaders(
         "Access-Control-Allow-Origin", "*",
         "Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE, PUT",
         "Access-Control-Max-Age", "3600",
         "Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization",
         "Access-Control-Allow-Credentials", "true"
      );
   }

   public Result redirectDocs() {
      return redirect("/assets/lib/swagger-ui/index.html?url=/swagger.yml");
   }


}
