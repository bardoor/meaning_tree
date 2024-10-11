package org.vstu.meaningtree.exceptions;

public class MeaningTreeException extends RuntimeException {
    public MeaningTreeException(String msg) {
        super(msg);
    }

    public MeaningTreeException(Exception e) {
        super(e);
    }
}
