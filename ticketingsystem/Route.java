/*
 * MIT License
 *
 * Copyright (c) 2020 Chenming C
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ticketingsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Route {
    private final Integer routeId;
    private final Integer coachNum;
    private final List<Integer> coachNumList = new ArrayList<>();
    private final Integer seatNum;
    private final List<Integer> seatNumList = new ArrayList<>();
    private final Integer stationNum;
    private final CopyOnWriteArrayList<CopyOnWriteArrayList<AtomicLong>> coachList = new CopyOnWriteArrayList<>();
    private final AtomicInteger seedGenerator = new AtomicInteger(0);

    public Route(int routeId, int coachNum, int seatNum, int stationNum) {
        this.routeId = routeId;
        this.coachNum = coachNum;
        this.seatNum = seatNum;
        this.stationNum = stationNum;
        for (int i = 0; i < coachNum; i++) {
            CopyOnWriteArrayList<AtomicLong> seatList = new CopyOnWriteArrayList<>();
            for (int j = 0; j < seatNum; j++) {
                long seat = 0;
                for (int k = 0; k < stationNum - 1; k++) {
                    seat |= (1 << k);
                }
                seatList.add(new AtomicLong(~seat));
                if (i == 0) seatNumList.add(j);
            }
            this.coachNumList.add(i);
            this.coachList.add(seatList);
        }
    }

    public Ticket buyTicket(int departure, int arrival) {
        if (departure >= stationNum || arrival >= stationNum) return null;
        List<Integer> tempCoachNumList = this.coachNumList.subList(0, this.coachNumList.size());
        Random tempRand = new Random(System.currentTimeMillis());
        Collections.shuffle(tempCoachNumList, tempRand);
        List<Integer> tempSeatNumList = this.seatNumList.subList(0, this.seatNumList.size());
        Collections.shuffle(tempSeatNumList, tempRand);
        for (int i = 0; i < tempCoachNumList.size(); i++) {
            for (int j = 0; j < tempSeatNumList.size(); j++) {
                long originSeat = coachList.get(i).get(j).get();
                long checkSeat = generateSeat(departure, arrival);
                if ((originSeat & checkSeat) == 0) {
                    if (coachList.get(i).get(j).compareAndSet(originSeat, originSeat | checkSeat)) {
                        return newTicket(seedGenerator.getAndIncrement(), this.routeId + 1, i + 1, j + 1, departure + 1, arrival + 1);
                    }
                }
            }
        }
        return null;
    }

    public int query(int departure, int arrival) {
        if (departure >= stationNum || arrival >= stationNum) return -1;
        int count = 0;
        for (CopyOnWriteArrayList<AtomicLong> coach : coachList) {
            for (AtomicLong seat : coach) {
                long originSeat = seat.get();
                long checkSeat = generateSeat(departure, arrival);
                // originSeat 000 1100 000
                // checkSeat 000 1111 000
                if ((originSeat & checkSeat) == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean refundTicket(Ticket ticket) {
        long originalSeat;
        long newSeat;
        do {
            originalSeat = coachList.get(ticket.coach).get(ticket.seat).get();
            long checkSeat = generateSeat(ticket.departure, ticket.arrival);
            // original 10011011001
            // ~o       01100100110
            // check    00011111000
            // result   00000100000
            if ((~originalSeat & checkSeat) != 0) return false;
            newSeat = originalSeat & ~generateSeat(ticket.departure, ticket.arrival);
        } while (!coachList.get(ticket.coach).get(ticket.seat).compareAndSet(originalSeat, newSeat));
        return true;
    }

    private long generateSeat(int departure, int arrival) {
        long seat = 0L;
        for (int i = departure; i < arrival; i++) {
            seat |= 1 << i;
        }
        return seat;
    }

    public Ticket newTicket(int tidSeed, int route, int coach, int seat, int departure, int arrival) {
        Ticket ticket = new Ticket();
        ticket.route = route;
        ticket.coach = coach;
        ticket.seat = seat;
        ticket.departure = departure;
        ticket.arrival = arrival;
        long id = 0;
        id |= tidSeed;
        id &= generateSeat(0, 23);
        id |= (route << 23);
        id &= generateSeat(0, 31);
//        Calendar cal = Calendar.getInstance();
//        long year = (long) cal.get(Calendar.YEAR) << (SEED_WIDTH + COACH_WIDTH + DAY_WIDTH + MONTH_WIDTH);
//        long month = (long) (cal.get(Calendar.MONTH) + 1) << (SEED_WIDTH + COACH_WIDTH + DAY_WIDTH);
//        long day = (long) cal.get(Calendar.DAY_OF_MONTH) << (SEED_WIDTH + COACH_WIDTH);
//        long routeBinary = (long) route << SEED_WIDTH;
//        id = id | year | month | day | routeBinary;
        ticket.tid = id;
        return ticket;
    }

}
