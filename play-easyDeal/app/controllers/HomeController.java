package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Controller;
import views.html.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result signup(){

        JsonNode json = request().body().asJson();
        String fullname = json.get("fullname").asText();
        String email = json.get("email").asText();
        String phone = json.get("phone").asText();
        System.out.println(fullname);

        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("easydeal");
        MongoCollection<Document> users = db.getCollection("users");
        FindIterable<Document> iterable = users.find(eq("email", email));
        for (Document document : iterable){
            System.out.println("Repeated email");
            return ok("Repeated");
        }

        Document doc = new Document("fullname", fullname )
                .append("email", email)
                .append("phone_no", phone);

        users.insertOne(doc);
        response().setCookie("user", email);
        return ok("Done");

    }

    public Result login(String email, String password){

            System.out.println(email);
            System.out.println(password);

            MongoClient mongoClient = new MongoClient();
            MongoDatabase db = mongoClient.getDatabase("easydeal");
            MongoCollection<Document> users = db.getCollection("users");
            FindIterable<Document> iterable = users.find(and(eq("email", email), eq("password", password)));
            for (Document document : iterable){
                session().put("login", System.currentTimeMillis()+"");
                response().setCookie("name", document.getString("firstname") + "-" + document.getString("lastname"),null,"/","",false, false );
                response().setCookie("user", email, null, "/", "", false, false);

                return redirect("/SN/home");
            }

                String error = flash("error");
                flash("error", "Authentication Failed! The email or password entered is incorrect");





    return ok("Login Tested" + email + " " + password);
    }

}
