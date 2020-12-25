package ticketingsystem;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Test {

    final static int threadnums[] = {1, 4, 8, 16, 32, 64, 100, 128};
    final static int routenum = 100; // route is designed from 1 to 3
    final static int coachnum = 14; // coach is arranged from 1 to 5
    final static int seatnum = 100; // seat is allocated from 1 to 20
    final static int stationnum = 50; // station is designed from 1 to 5

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

            //long preTime = startTime;
            AtomicInteger totalBuyCount = new AtomicInteger(0), totalReturnCount = new AtomicInteger(0), totalQueryCount = new AtomicInteger(0);
            AtomicLong totalBuyPeriod = new AtomicLong(0), totalReturnPeriod = new AtomicLong(0), totalQueryPeriod = new AtomicLong(0);

            final long startTime = System.currentTimeMillis();
            for (int i = 0; i < threadnum; i++) {
                threads[i] = new Thread(new Runnable() {
                    public void run() {
                        Random rand = new Random();
                        Ticket ticket = new Ticket();
                        ArrayList<Ticket> soldTicket = new ArrayList<Ticket>();

                        int buyCount = 0, returnCount = 0, queryCount = 0;
                        long buyPeriod = 0, returnPeriod = 0, queryPeriod = 0;

                        for (int i = 0; i < testnum; i++) {
                            int sel = rand.nextInt(inqpc);
                            long periodStart = System.currentTimeMillis();
                            if (0 <= sel && sel < retpc && soldTicket.size() > 0) { // return ticket
                                int select = rand.nextInt(soldTicket.size());
                                long preTime = System.currentTimeMillis() - startTime;
                                if ((ticket = soldTicket.remove(select)) != null) {
                                    preTime = System.currentTimeMillis() - startTime;
                                    if (tds.refundTicket(ticket)) {
                                        long postTime = System.currentTimeMillis() - startTime;
                                        returnCount++;
                                        returnPeriod += System.currentTimeMillis() - periodStart;
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
                                    buyCount++;
                                    buyPeriod += System.currentTimeMillis() - periodStart;
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
                                queryCount++;
                                queryPeriod += System.currentTimeMillis() - periodStart;
//                            out.write(preTime + " " + postTime + " " + ThreadId.get() + " " + "RemainTicket" + " " + leftTicket + " " + route + " " + departure + " " + arrival + "\n");
                            }
                        }
                        totalBuyCount.addAndGet(buyCount);
                        totalQueryCount.addAndGet(queryCount);
                        totalReturnCount.addAndGet(returnCount);
                        totalBuyPeriod.addAndGet(buyPeriod);
                        totalQueryPeriod.addAndGet(queryPeriod);
                        totalReturnPeriod.addAndGet(returnPeriod);
                    }
                });
                threads[i].start();
            }

            for (int i = 0; i < threadnum; i++) {
                threads[i].join();
            }
            long postTime = (System.currentTimeMillis() - startTime);
            System.out.println("Using " + threadnum + " threads: " + postTime + "ms");
            System.out.printf("Buy method:\t count %7d | period %5d | average %.5f ms\n", totalBuyCount.get(), totalBuyPeriod.get(), (float) totalBuyPeriod.get() / totalBuyCount.get());
            System.out.printf("Return method:\t count %7d | period %5d | average %.5f ms\n", totalReturnCount.get(), totalReturnPeriod.get(), (float) totalReturnPeriod.get() / totalReturnCount.get());
            System.out.printf("Query method:\t count %7d | period %5d | average %.5f ms\n\n", totalQueryCount.get(), totalQueryPeriod.get(), (float) totalQueryPeriod.get() / totalQueryCount.get());
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Sleep finish!");
        }


    }
}
