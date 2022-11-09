package wethinkcode.stage;

import com.google.common.annotations.VisibleForTesting;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * I provide a REST API that reports the current loadshedding "stage". I provide
 * two endpoints:
 * <dl>
 * <dt>GET /stage
 * <dd>report the current stage of loadshedding as a JSON serialisation
 *      of a {@code StageDO} data/transfer object
 * <dt>POST /stage
 * <dd>set a new loadshedding stage/level by POSTing a JSON-serialised {@code StageDO}
 *      instance as the body of the request.
 * </ul>
 */
public class StageService
{
    public static final int DEFAULT_STAGE = 0; // no loadshedding. Ha!

    public static final int DEFAULT_PORT = 7001;

    public static void main( String[] args ){
        final StageService svc = new StageService().initialise();
        svc.start();
    }

    private int loadSheddingStage;

    private Javalin server;

    private int servicePort;

    private StageDO stageDO;

    @VisibleForTesting
    StageService initialise(){
        return initialise( DEFAULT_STAGE );
    }

    @VisibleForTesting
    StageService initialise( int initialStage ){
        loadSheddingStage = initialStage;
        stageDO = new StageDO(initialStage);

        assert loadSheddingStage >= 0;

        server = initHttpServer();
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
        app();
    }

    private Javalin initHttpServer(){
        return server = Javalin.create();
    }

    public void getLoadSheddingStage(Context context) {
        context.contentType("application/json");
        HashMap<String, Integer> loadSheddingMap = new HashMap<>();

        initialise(stageDO.getStage());

        loadSheddingMap.put("stage", stageDO.getStage());
        context.json(loadSheddingMap);

    }

    public void postLoadSheddingStage(Context context) {
        context.contentType("application/json");
        HashMap<String, Integer> loadSheddingMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject(context.body());

        int stage = jsonObject.getInt("stage");

        if (stage < 0) {
            context.status(400);
        } else {
            initialise(stage);
            loadSheddingMap.put("stage", stageDO.getStage());
            context.json(loadSheddingMap);
        }

    }

    public void app() {
        this.server.get("/stage", this::getLoadSheddingStage);
        this.server.post("/stage", this::postLoadSheddingStage);
    }
}
