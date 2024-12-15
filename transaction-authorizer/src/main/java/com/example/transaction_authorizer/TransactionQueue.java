package com.example.transaction_authorizer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TransactionQueue {
    private static TransactionQueue instance;
    private BlockingQueue<Transaction> queue;

    private TransactionQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    public static synchronized TransactionQueue getInstance() {
        if (instance == null) {
            instance = new TransactionQueue();
        }
        return instance;
    }

    public void enqueue(Transaction transaction) {
        queue.offer(transaction);
    }

    public Transaction dequeue() throws InterruptedException {
        return queue.take();
    }

}
