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
import java.util.concurrent.ConcurrentHashMap;

public class TicketingDS implements TicketingSystem {
    private final ConcurrentHashMap<Integer, Route> routeMap;
    private final int routeNum;
    private final int stationNum;

    public TicketingDS(int routeNum, int coachNum, int seatNum, int stationNum, int threadNum) {
        this.routeNum = routeNum;
        this.stationNum = stationNum;
        this.routeMap = new ConcurrentHashMap<>(routeNum);
        for (int count = 0; count < routeNum; count++) {
            this.routeMap.put(count, new Route(count, coachNum, seatNum, stationNum));
        }
    }

    @Override
    public Ticket buyTicket(String passenger, int routeNum, int departure, int arrival) {
        Route route = routeMap.get(routeNum - 1);
        if (route == null) return null;
        Ticket ticket = route.buyTicket(departure - 1, arrival - 1);
        if (ticket == null) return null;
        ticket.setPassenger(passenger);
        return ticket;
    }

    @Override
    public int inquiry(int routeNum, int departure, int arrival) {
        Route route = routeMap.get(routeNum - 1);
        if (route == null) return -1;
        return route.query(departure - 1, arrival - 1);
    }

    @Override
    public boolean refundTicket(Ticket ticket) {
        Route route = routeMap.get(ticket.route - 1);
        ticket.coach -= 1;
        ticket.seat -= 1;
        ticket.departure -= 1;
        ticket.arrival -= 1;
        return route.refundTicket(ticket);
    }
}
