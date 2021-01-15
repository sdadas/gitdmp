package pl.sdadas.gitdmp.model.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDate;

public class GitdmpArgs implements Serializable {

    private LocalDate from;

    private LocalDate to;

    private String outputDir;

    public LocalDate getFrom() {
        return from;
    }

    @JsonIgnore
    public java.sql.Date getFromDate() {
        return java.sql.Date.valueOf(from);
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    @JsonIgnore
    public java.sql.Date getToDate() {
        return java.sql.Date.valueOf(to);
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
}
