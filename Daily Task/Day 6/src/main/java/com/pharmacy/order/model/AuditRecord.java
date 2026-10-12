package com.pharmacy.order.model;

import java.time.LocalDateTime;

public class AuditRecord {
    private final long recordId;
    private final String orderId;
    private final String action;
    private final OrderStatus finalStatus;
    private final LocalDateTime timestamp;
    private final String outcomeSummary;
    private final String exceptionType;
    private final String rootCauseMessage;

    public AuditRecord(long recordId, String orderId, String action, OrderStatus finalStatus,
                       String outcomeSummary, String exceptionType, String rootCauseMessage) {
        this.recordId = recordId;
        this.orderId = orderId;
        this.action = action;
        this.finalStatus = finalStatus;
        this.timestamp = LocalDateTime.now();
        this.outcomeSummary = outcomeSummary;
        this.exceptionType = exceptionType;
        this.rootCauseMessage = rootCauseMessage;
    }

    public long getRecordId() {
        return recordId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAction() {
        return action;
    }

    public OrderStatus getFinalStatus() {
        return finalStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getOutcomeSummary() {
        return outcomeSummary;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getRootCauseMessage() {
        return rootCauseMessage;
    }

    @Override
    public String toString() {
        if (exceptionType == null) {
            return String.format("[%s] AUDIT #%d | Order: %s | Action: %s | Status: %s | Outcome: %s",
                    timestamp, recordId, orderId, action, finalStatus, outcomeSummary);
        } else {
            return String.format("[%s] AUDIT #%d | Order: %s | Action: %s | Status: %s | Error: %s | RootCause: %s",
                    timestamp, recordId, orderId, action, finalStatus, exceptionType, rootCauseMessage);
        }
    }
}
