package com.pharmacy.order.service;

import com.pharmacy.order.model.AuditRecord;
import com.pharmacy.order.model.OrderStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Audit service that guarantees audit recording during order processing lifecycle.
 * In a production architecture, this writes to an immutable database or compliance ledger.
 */
public class AuditService {
    private final List<AuditRecord> auditRecords = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong recordCounter = new AtomicLong(1);

    /**
     * Records an audit entry. This method is called from the 'finally' block
     * of order operations to ensure audit trail immutability.
     */
    public AuditRecord recordAudit(String orderId, String action, OrderStatus status,
                                   String summary, Throwable ex) {
        String exceptionType = null;
        String rootCauseMessage = null;

        if (ex != null) {
            exceptionType = ex.getClass().getSimpleName();
            Throwable root = getRootCause(ex);
            rootCauseMessage = String.format("[%s: %s]", root.getClass().getSimpleName(), root.getMessage());
        }

        AuditRecord record = new AuditRecord(
                recordCounter.getAndIncrement(),
                orderId,
                action,
                status,
                summary,
                exceptionType,
                rootCauseMessage
        );

        auditRecords.add(record);
        return record;
    }

    public List<AuditRecord> getAuditRecords() {
        synchronized (auditRecords) {
            return new ArrayList<>(auditRecords);
        }
    }

    public List<AuditRecord> getAuditRecordsForOrder(String orderId) {
        synchronized (auditRecords) {
            return auditRecords.stream()
                    .filter(r -> r.getOrderId().equals(orderId))
                    .toList();
        }
    }

    public void clear() {
        auditRecords.clear();
        recordCounter.set(1);
    }

    /**
     * Recursively traces the root cause of an exception chain.
     */
    public static Throwable getRootCause(Throwable throwable) {
        if (throwable == null) return null;
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }
}
