package pl.sdadas.gitdmp.model;

import java.time.LocalDate;
public enum TimeRange {

    THIS_MONTH() {
        @Override
        public LocalDate getFrom() {
            LocalDate date = LocalDate.now();
            return date.withDayOfMonth(1);
        }

        @Override
        public LocalDate getTo() {
            LocalDate date = LocalDate.now();
            return date.plusMonths(1).withDayOfMonth(1);
        }
    },

    LAST_MONTH {
        @Override
        public LocalDate getFrom() {
            LocalDate date = LocalDate.now().minusMonths(1);
            return date.withDayOfMonth(1);
        }

        @Override
        public LocalDate getTo() {
            LocalDate date = LocalDate.now();
            return date.withDayOfMonth(1);
        }
    },

    CUSTOM {
        @Override
        public LocalDate getFrom() {
            return null;
        }

        @Override
        public LocalDate getTo() {
            LocalDate date = LocalDate.now();
            return date.plusMonths(1).withDayOfMonth(1);
        }
    };

    public abstract LocalDate getFrom();

    public abstract LocalDate getTo();
}
