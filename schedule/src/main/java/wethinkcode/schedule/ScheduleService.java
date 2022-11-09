package wethinkcode.schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import io.javalin.Javalin;
import wethinkcode.schedule.transfer.DayDO;
import wethinkcode.schedule.transfer.ScheduleDO;
import wethinkcode.schedule.transfer.SlotDO;
import io.javalin.http.Context;

/**
 * I provide a REST API providing the current loadshedding schedule for a
 * given town (in a specific province) at a given loadshedding stage.
 */
public class ScheduleService
{
    public static final int DEFAULT_STAGE = 0; // no loadshedding. Ha!

    public static final int DEFAULT_PORT = 7002;

    private Javalin server;

    private int servicePort;

    private String[] provinces;

    public static void main( String[] args ){
        final ScheduleService svc = new ScheduleService().initialise();
        svc.start();
    }

    public boolean validProvince(String province) {
        provinces = new String[]{"gauteng", "mpumalanga", "free state", "limpopo", "kwakulu-natal", "north west", "northern cape", "western cape", "eastern cape"};

        for (String provin: provinces) {
            if (province.toLowerCase().equals(provin)) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    ScheduleService initialise(){
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

    // There *must* be a better way than this...
    // See Steps 4 and 5 (the optional ones!) in the course notes.
    Optional<ScheduleDO> getSchedule( String province, String town, int stage ){
        return province.equalsIgnoreCase( "Mars" )
            ? Optional.empty()
            : Optional.of( mockSchedule() );
    }

    /**
     * Answer with a hard-coded/mock Schedule.
     * @return A non-null, slightly plausible Schedule.
     */
    private static ScheduleDO mockSchedule(){
        final List<SlotDO> slots = List.of(
            new SlotDO( LocalTime.of( 2, 0 ), LocalTime.of( 4, 0 )),
            new SlotDO( LocalTime.of( 10, 0 ), LocalTime.of( 12, 0 )),
            new SlotDO( LocalTime.of( 18, 0 ), LocalTime.of( 20, 0 ))
        );
        final List<DayDO> days = List.of(
            new DayDO( slots ),
            new DayDO( slots ),
            new DayDO( slots ),
            new DayDO( slots )
        );
        return new ScheduleDO( days );
    }

    /**
     * Answer with a non-null but empty Schedule.
     * @return The empty Schedule.
     */
    private static ScheduleDO emptySchedule(){
        final List<SlotDO> slots = Collections.emptyList();
        final List<DayDO> days = Collections.emptyList();
        return new ScheduleDO( days );
    }

    public void getSchedule(Context context) {
        context.contentType("application/json");
        if (validProvince(context.pathParam("province")) && Integer.parseInt(context.pathParam("stage")) <= 6) {
            context.status(200);
            context.json(mockSchedule());
        } else if (Integer.parseInt(context.pathParam("stage")) > 7) {
            context.status(400);
        } else {
            context.status(404);
            context.json(emptySchedule());
        }
    }

    public void app() {
        server.get("/{province}/{town}/{stage}", this::getSchedule);
    }
}
