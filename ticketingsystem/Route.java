package ticketingsystem;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Route {
    private final String routeId;
    private final Integer coachNum;
    private final Integer seatNum;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<AtomicLong>> coachList;
    public Route(String routeId, int coachNum, int seatNum) {
        this.routeId = routeId;
        this.coachNum = coachNum;
        this.seatNum = seatNum;
        this.coachList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < coachNum; i++) {
            CopyOnWriteArrayList<AtomicLong> seatList = new CopyOnWriteArrayList<>();
            for (int j = 0; j < seatNum; j++) {
                seatList.add(new AtomicLong(0));
            }
        }
    }
}
