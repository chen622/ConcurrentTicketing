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
    private ConcurrentHashMap<String, Route> routeMap;
    private final int routeNum;
    private final int stationNum;

    public TicketingDS(int routeNum, int coachNum, int seatNum, int stationNum, int threadNum) {
        this.routeNum = routeNum;
        this.stationNum = stationNum;
        this.routeMap = new ConcurrentHashMap<>(routeNum);
        for (int count = 0; count < routeNum; count++) {
            String routeId = "G" + (count + 1);
            this.routeMap.put(routeId, new Route(routeId, coachNum, seatNum));
        }
    }

    @Override
    public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
        return null;
    }

    @Override
    public int inquiry(int route, int departure, int arrival) {
        return 0;
    }

    @Override
    public boolean refundTicket(Ticket ticket) {
        return false;
    }
}
