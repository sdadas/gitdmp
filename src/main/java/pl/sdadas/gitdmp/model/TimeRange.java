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
            return date.withDayOfMonth(date.getMonth().length(date.isLeapYear()));
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
            LocalDate date = LocalDate.now().minusMonths(1);
            return date.withDayOfMonth(date.getMonth().length(date.isLeapYear()));
        }
    },

    CUSTOM {
        @Override
        public LocalDate getFrom() {
            return null;
        }

        @Override
        public LocalDate getTo() {
            return null;
        }
    };

    public abstract LocalDate getFrom();

    public abstract LocalDate getTo();
}
