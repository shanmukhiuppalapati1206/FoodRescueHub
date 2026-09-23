package com.foodrescue.hub;

import org.springframework.stereotype.Component;
import java.util.LinkedList;
import java.util.Queue;

@Component
public class DonationQueue {
    // FIFO Queue for incoming NGO requests
    private final Queue<String> requestQueue = new LinkedList<>();

    public void addRequest(String request) {
        requestQueue.add(request);
        System.out.println("Emergency Request Added to FIFO Queue: " + request);
    }

    public String processNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasRequests() {
        return !requestQueue.isEmpty();
    }

    public int getRequestCount() {
        return requestQueue.size();
    }
}