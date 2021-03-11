package rest;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.File;

import com.google.gson.Gson;

import ws.WsHandler;

public class SparkAppMain {

	private static Gson g = new Gson();

	public static void main(String[] args) throws Exception {
		port(8080);

		webSocket("/ws", WsHandler.class);

		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		get("/rest/demo/test", (req, res) -> {
			return "Works";
		});
		
		get("/rest/demo/book/:isbn", (req, res) -> {
			String isbn = req.params("isbn");
			return "/rest/demo/book received PathParam 'isbn': " + isbn;
		});

		get("/rest/demo/books", (req, res) -> {
			String num = req.queryParams("num");
			return "/rest/demo/book received QueryParam 'num': " + num;
		});
		
		get("/rest/demo/testheader", (req, res) -> {
			String cookie = req.headers("Cookie");
			return "/rest/demo/testheader received HeaderParam 'Cookie': " + cookie;
		});
		
		get("/rest/demo/testcookie", (req, res) -> {
			String cookie = req.cookie("pera");
			if (cookie == null) {
				res.cookie("pera", "Perin kolacic");
				return "/rest/demo/testcookie <b>created</b> CookieParam 'pera': 'Perin kolacic'";  
			} else {
				return "/rest/demo/testcookie <i><u>received</u></i> CookieParam 'pera': " + cookie;
			}
		});


	}
}
