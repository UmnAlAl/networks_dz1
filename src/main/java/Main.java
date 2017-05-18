import ShannonFanoCode.MyShannonFanoImpl;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by Installed on 15.05.2017.
 */
public class Main {

    public static Vertx vertxInstance;

    public static void main(String [] args)
    {
        vertxInstance = Vertx.vertx();
        StartHttpServer();
    }

    //for testing via postman
    public  static void StartHttpServer()
    {
        HttpServer server = vertxInstance.createHttpServer();
        Router router = Router.router(vertxInstance);
        router.route().handler(BodyHandler.create());

        router.route(HttpMethod.POST, "/api/CRCcount/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            String crc = MyCRCImpl.countCRC32(body);
            response.write(body+"|"+crc);
            response.end();
        });

        router.route(HttpMethod.POST, "/api/CRCcheck/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            if(MyCRCImpl.checkCRC32(body)) {
                response.write("good");
            }
            else {
                response.write("bad");
            }
            response.end();
        });

        router.route(HttpMethod.POST, "/api/HammingEncrypt/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            //get val from bit str
            int x = Integer.parseInt(body, 2);
            int y = MyHammingImpl.HammingEncode(x);
            response.write(Integer.toBinaryString(y));
            response.end();
        });

        router.route(HttpMethod.POST, "/api/HammingDecrypt/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            int y = Integer.parseInt(body, 2);
            int x = MyHammingImpl.HammingDecode(y);
            response.write(Integer.toBinaryString(x));
            response.end();
        });

        router.route(HttpMethod.POST, "/api/ShannonFanoEncode/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            String coded = MyShannonFanoImpl.encode(body);
            response.write(coded);
            response.end();
        });

        router.route(HttpMethod.POST, "/api/ShannonFanoDecode/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            String decoded = MyShannonFanoImpl.decode(body);
            response.write(decoded);
            response.end();
        });

        router.route(HttpMethod.POST, "/api/LZDecompress/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            response.write("/api/LZDecompress/");
            response.end();
        });

        server.requestHandler(router::accept).listen(8089);
    }

}
