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
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

public class TicketingDS implements TicketingSystem {
    private final ConcurrentHashMap<Integer, Route> routeMap;
    private final int routeNum;
    private final int stationNum;
    private final int coachNum;
    private final int seatNum;

    public TicketingDS(int routeNum, int coachNum, int seatNum, int stationNum, int threadNum) {
        this.routeNum = routeNum;
        this.stationNum = stationNum;
        this.coachNum = coachNum;
        this.seatNum = seatNum;
        this.routeMap = new ConcurrentHashMap<>(routeNum);
        for (int count = 0; count < routeNum; count++) {
            this.routeMap.put(count, new Route(count, coachNum, seatNum, stationNum));
        }
    }

    @Override
    public Ticket buyTicket(String passenger, int routeNum, int departure, int arrival) {
        if (!checkLegal(routeNum, departure, arrival, 1, 1)) return null;
        Route route = routeMap.get(routeNum - 1);
        if (route == null) return null;
        Ticket ticket = route.buyTicket(departure - 1, arrival - 1);
        if (ticket == null) return null;
        ticket.passenger = passenger;
        ticket.tid |= (getHash(ticket) << (31L));
        return ticket;
    }

    @Override
    public int inquiry(int routeNum, int departure, int arrival) {
        if (!checkLegal(routeNum, departure, arrival, 1, 1)) return -1;
        Route route = routeMap.get(routeNum - 1);
        if (route == null) return -1;
        return route.query(departure - 1, arrival - 1);
    }

    @Override
    public boolean refundTicket(Ticket ticket) {
        if (!checkLegal(ticket.route, ticket.departure, ticket.arrival, ticket.coach, ticket.seat)) return false;
        Ticket copyTicket = copy(ticket);
        Route route = routeMap.get(copyTicket.route - 1);
        copyTicket.coach -= 1;
        copyTicket.seat -= 1;
        copyTicket.departure -= 1;
        copyTicket.arrival -= 1;
        if (copyTicket.seat < 0 || (getHash(ticket) << 31L >> 31L) != (ticket.tid >> 31) || !route.refundTicket(copyTicket))
            return false;
        else {
            ticket.seat = -1;
            return true;
        }
    }

    public Long getHash(Ticket ticket) {
        String hashString = ticket.passenger + ticket.route + ticket.coach + ticket.seat;
        return (hashString.hashCode() & (~(1L << 32)));
    }


    public Ticket copy(Ticket ticket) {
        Ticket copy = new Ticket();
        copy.tid = ticket.tid;
        copy.passenger = ticket.passenger;
        copy.route = ticket.route;
        copy.coach = ticket.coach;
        copy.seat = ticket.seat;
        copy.departure = ticket.departure;
        copy.arrival = ticket.arrival;
        return copy;
    }

    public boolean checkLegal(int route, int departure, int arrival, int coach, int seat) {
        return route > 0 && route <= this.routeNum && departure > 0 && departure <= this.stationNum && arrival > 0 && arrival <= this.stationNum && coach > 0 && coach <= this.coachNum && seat > 0 && seat <= this.seatNum;
    }
}
