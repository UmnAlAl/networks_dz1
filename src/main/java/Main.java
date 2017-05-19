import LZWDeCompress.LZW;
import ShannonFanoCode.MyShannonFanoImpl;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.DataInputStream;
import java.io.File;

/**
 * Created by Installed on 15.05.2017.
 */
public class Main {

    public static Vertx vertxInstance;
    public static int port = 8089;

    public static void main(String [] args)
    {
        vertxInstance = Vertx.vertx();
        if(args.length >= 1) {
            if(args[0].equals("client")) {
                StartHttpClient();
                System.out.println("client started");
            } else  if (args[0].equals("server")) {
                StartHttpServer();
                System.out.println("server started");
            }
        }
        else {
            StartTestApiHttpServer();
            System.out.println("server test api started");
        }
    }

    //for testing via postman
    public  static void StartTestApiHttpServer()
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

        router.route(HttpMethod.POST, "/api/LZWCompress/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            String compressed = LZW.compress(body);
            response.write(compressed);
            response.end();
        });

        router.route(HttpMethod.POST, "/api/LZWDecompress/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            String decompressed = LZW.expand(body);
            response.write(decompressed);
            response.end();
        });

        // NOT WORKING!
        router.route(HttpMethod.POST, "/api/JPEGProcess/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            try {

                String body = routingContext.getBodyAsString();
                BufferedImage bim = ImageIO.read(new ByteArrayInputStream(body.getBytes()));
                bim = MyImageOpsImpl.process(bim);
                response.write(bim.toString());
            }
            catch (Exception ex) {

            }
            response.end();
        });

        server.requestHandler(router::accept).listen(port);
    }

    public static void StartHttpClient() {
        HttpClient httpClient = vertxInstance.createHttpClient();

        String strToCheckCRC32 = "0123456789";
        httpClient.post(port, "localhost", "/api/CRCcheck/", httpClientResponse -> {
            httpClientResponse.bodyHandler(buffer -> {
                System.out.println("CRCCheck " + buffer.toString());
            });
        }).setChunked(true).write(strToCheckCRC32 + "|" + MyCRCImpl.countCRC32(strToCheckCRC32)).end();


        Integer intToCheckHamming = 123456;
        httpClient.post(port, "localhost", "/api/HammingDecrypt/", httpClientResponse -> {
            httpClientResponse.bodyHandler(buffer -> {
                System.out.println("HammingDecrypt " + buffer.toString());
            });
        }).setChunked(true).write( Integer.toString(MyHammingImpl.HammingEncode(intToCheckHamming)) ).end();


        String strToCheckShannonFano = "abbcdbeeffffffefefeeef";
        httpClient.post(port, "localhost", "/api/ShannonFanoDecode/", httpClientResponse -> {
            httpClientResponse.bodyHandler(buffer -> {
                System.out.println("ShannonFanoDecode " + buffer.toString());
            });
        }).setChunked(true).write( MyShannonFanoImpl.encode(strToCheckShannonFano) ).end();


        String strToCheckLZW = "aabacababcabac";
        httpClient.post(port, "localhost", "/api/LZWDecompress/", httpClientResponse -> {
            httpClientResponse.bodyHandler(buffer -> {
                System.out.println("LZWDecompress " + buffer.toString());
            });
        }).setChunked(true).write( LZW.compress(strToCheckLZW) ).end();

        httpClient.close();

    }

    public static void StartHttpServer() {

        HttpServer server = vertxInstance.createHttpServer();
        Router router = Router.router(vertxInstance);
        router.route().handler(BodyHandler.create());

        router.route(HttpMethod.POST, "/api/CRCcheck/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            if(MyCRCImpl.checkCRC32(body)) {
                System.out.println(body + "   good CRC");
                response.write("good");
            }
            else {
                System.out.println(body + "   bad CRC");
                response.write("bad");
            }
            response.end();
        });

        router.route(HttpMethod.POST, "/api/HammingDecrypt/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            int y = Integer.parseInt(body);
            int x = MyHammingImpl.HammingDecode(y);
            response.write(Integer.toString(x));
            response.end();
            System.out.println("HammingDecrypted " + Integer.toString(x));
        });

        router.route(HttpMethod.POST, "/api/ShannonFanoDecode/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            String decoded = MyShannonFanoImpl.decode(body);
            response.write(decoded);
            response.end();
            System.out.println("ShannonFanoDecoded " + decoded);
        });

        router.route(HttpMethod.POST, "/api/LZWDecompress/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            String body = routingContext.getBodyAsString();
            String decompressed = LZW.expand(body);
            response.write(decompressed);
            response.end();
            System.out.println("LZWDecompressed " + decompressed);
        });

        server.requestHandler(router::accept).listen(port);
    }

}
