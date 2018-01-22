package org.pinguin.gf.facade.common;

import java.util.Calendar;

public class PeriodTO {

    private Calendar start;
    private Calendar end;

    public PeriodTO() {
    }

    public PeriodTO(Calendar start, Calendar end) {
        this.start = start;
        this.end = end;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }
}
