package ticketingsystem;

import java.util.Calendar;
import java.util.Date;

class Ticket {
    static final int SEED_WIDTH = 27;
    static final int COACH_WIDTH = 14;
    static final int DAY_WIDTH = 5;
    static final int MONTH_WIDTH = 4;
    static final int YEAR_WIDTH = 14;
    long tid;
    String passenger;
    int route;
    int coach;
    int seat;
    int departure;
    int arrival;

    public Ticket() {
    }

    public Ticket(int tidSeed, int route, int coach, int seat, int departure, int arrival) {
        this.route = route;
        this.coach = coach;
        this.seat = seat;
        this.departure = departure;
        this.arrival = arrival;
        long id = 0;
        id |= tidSeed;
        Calendar cal = Calendar.getInstance();
        long year = (long) cal.get(Calendar.YEAR) << (SEED_WIDTH + COACH_WIDTH + DAY_WIDTH + MONTH_WIDTH);
        long month = (long) (cal.get(Calendar.MONTH) + 1) << (SEED_WIDTH + COACH_WIDTH + DAY_WIDTH);
        long day = (long) cal.get(Calendar.DAY_OF_MONTH) << (SEED_WIDTH + COACH_WIDTH);
        long routeBinary = (long) route << SEED_WIDTH;
        id = id | year | month | day | routeBinary;
        this.tid = id;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public Ticket copy(){
        Ticket copy = new Ticket();
        copy.tid = this.tid;
        copy.passenger = this.passenger;
        copy.route = this.route;
        copy.coach = this.coach;
        copy.seat = this.seat;
        copy.departure = this.departure;
        copy.arrival = this.arrival;
        return copy;
    }
}


public interface TicketingSystem {
    Ticket buyTicket(String passenger, int route, int departure, int arrival);

    int inquiry(int route, int departure, int arrival);

    boolean refundTicket(Ticket ticket);
}
