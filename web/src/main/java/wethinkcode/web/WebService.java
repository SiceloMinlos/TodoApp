package wethinkcode.web;

import com.google.common.annotations.VisibleForTesting;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

/**
 * I am the front-end web server for the LightSched project.
 * <p>
 * Remember that we're not terribly interested in the web front-end part of this
 * server, more in the way it communicates and interacts with the back-end
 * services.
 */
public class WebService
{

    public static final int DEFAULT_PORT = 8000;

    public static void main( String[] args ){
        final WebService svc = new WebService().initialise();
        svc.start();
    }

    private Javalin server;

    private int servicePort;

    @VisibleForTesting
    WebService initialise(){
        this.server = Javalin.create(javalinConfig -> {
            javalinConfig.addStaticFiles("/templates", Location.CLASSPATH);
        });
//        configureHttpClient();
        return this;
    }

    public void start(){
        start( DEFAULT_PORT );
    }

    @VisibleForTesting
    void start( int networkPort ){
        servicePort = networkPort;
        run();
    }

    public void stop(){
        server.stop();
    }

    public void run(){
        server.start( servicePort );
    }

    private void configureHttpClient(){
        try {
            BufferedReader bufferedReader = null;
            URL url = new URL("http://localhost:7000/provinces");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Javalin configureHttpServer(){
        throw new UnsupportedOperationException( "TODO" );
    }


}
