package ticketingsystem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Test {

    final static int threadnums[] = {1, 4, 8, 16, 32, 64, 128};
    final static int routenum = 20; // route is designed from 1 to 3
    final static int coachnum = 10; // coach is arranged from 1 to 5
    final static int seatnum = 100; // seat is allocated from 1 to 20
    final static int stationnum = 16; // station is designed from 1 to 5

    final static int testnum = 100000;
    final static int retpc = 10; // return ticket operation is 10% percent
    final static int buypc = 30; // buy ticket operation is 20% percent
    final static int inqpc = 100; //inquiry ticket operation is 70% percent

    static String passengerName() {
        Random rand = new Random();
        long uid = rand.nextInt(testnum);
        return "passenger" + uid;
    }

    public static void main(String[] args) throws InterruptedException {

        for (int count = 0; count < threadnums.length; count++) {
            int threadnum = threadnums[count];
            Thread[] threads = new Thread[threadnum];

            final TicketingDS tds = new TicketingDS(routenum, coachnum, seatnum, stationnum, threadnum);

            final long startTime = System.currentTimeMillis();
            //long preTime = startTime;

            for (int i = 0; i < threadnum; i++) {
                threads[i] = new Thread(new Runnable() {
                    public void run() {
                        Random rand = new Random();
                        Ticket ticket = new Ticket();
                        ArrayList<Ticket> soldTicket = new ArrayList<Ticket>();
                        for (int i = 0; i < testnum; i++) {
                            int sel = rand.nextInt(inqpc);
                            if (0 <= sel && sel < retpc && soldTicket.size() > 0) { // return ticket
                                int select = rand.nextInt(soldTicket.size());
                                long preTime = System.currentTimeMillis() - startTime;
                                if ((ticket = soldTicket.remove(select)) != null) {
                                    preTime = System.currentTimeMillis() - startTime;
                                    if (tds.refundTicket(ticket)) {
                                        long postTime = System.currentTimeMillis() - startTime;
//                                    out.write(preTime + " " + postTime + " " + ThreadId.get() + " " + "TicketRefund" + " " + ticket.tid + " " + ticket.passenger + " " + ticket.route + " " + ticket.coach + " " + ticket.departure + " " + ticket.arrival + " " + ticket.seat + "\n");
                                    } else {
                                        long postTime = System.currentTimeMillis() - startTime;
//                                    out.write(preTime + " " + postTime + " " + ThreadId.get() + " " + "ErrOfRefund" + "\n");
                                    }
                                } else {
                                    long postTime = System.currentTimeMillis() - startTime;
//                                out.write(preTime + " " + postTime + " " + ThreadId.get() + " " + "ErrOfRefund" + "\n");
                                }
                            } else if (retpc <= sel && sel < buypc) { // buy ticket
                                String passenger = passengerName();
                                int route = rand.nextInt(routenum) + 1;
                                int departure = rand.nextInt(stationnum - 1) + 1;
                                int arrival = departure + rand.nextInt(stationnum - departure) + 1; // arrival is always greater than departure
                                long preTime = System.currentTimeMillis() - startTime;
                                if ((ticket = tds.buyTicket(passenger, route, departure, arrival)) != null) {
                                    long postTime = System.currentTimeMillis() - startTime;
//                                out.write(preTime + " " + postTime + " " + ThreadId.get() + " " + "TicketBought" + " " + ticket.tid + " " + ticket.passenger + " " + ticket.route + " " + ticket.coach + " " + ticket.departure + " " + ticket.arrival + " " + ticket.seat + "\n");
                                    soldTicket.add(ticket);
                                } else {
                                    long postTime = System.currentTimeMillis() - startTime;
//                                out.write(preTime + " " + postTime + " " + ThreadId.get() + " " + "TicketSoldOut" + " " + route + " " + departure + " " + arrival + "\n");
                                }
                            } else if (buypc <= sel && sel < inqpc) { // inquiry ticket
                                int route = rand.nextInt(routenum) + 1;
                                int departure = rand.nextInt(stationnum - 1) + 1;
                                int arrival = departure + rand.nextInt(stationnum - departure) + 1; // arrival is always greater than departure
                                long preTime = System.currentTimeMillis() - startTime;
                                int leftTicket = tds.inquiry(route, departure, arrival);
                                long postTime = System.currentTimeMillis() - startTime;
//                            out.write(preTime + " " + postTime + " " + ThreadId.get() + " " + "RemainTicket" + " " + leftTicket + " " + route + " " + departure + " " + arrival + "\n");
                            }
                        }
                    }
                });
                threads[i].start();
            }

            for (int i = 0; i < threadnum; i++) {
                threads[i].join();
            }
            long postTime = (System.currentTimeMillis() - startTime);
            System.out.println("Using " + threadnum + " threads: " + postTime + "ms");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Sleep finish!");
        }


    }
}
