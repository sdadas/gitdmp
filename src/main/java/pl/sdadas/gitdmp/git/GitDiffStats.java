package pl.sdadas.gitdmp.git;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GitDiffStats implements Serializable {

    private final LocalDate day;

    private int linesAdded;

    private int linesDeleted;

    private int filesChanged;

    public GitDiffStats(LocalDate day) {
        this.day = day;
    }

    public void add(EditList edits) {
        edits.forEach(this::add);
        this.filesChanged += 1;
    }

    public void add(Edit edit) {
        this.linesAdded += edit.getEndB() - edit.getBeginB();
        this.linesDeleted += edit.getEndA() - edit.getBeginA();
    }

    public void add(GitDiffStats other) {
        this.linesAdded += other.linesAdded;
        this.linesDeleted += other.linesDeleted;
        this.filesChanged += other.filesChanged;
    }

    public LocalDate day() {
        return day;
    }

    @Override
    public String toString() {
        String date = day.format(DateTimeFormatter.ISO_DATE);
        return String.format("%s     f:%-10d +%-15d -%-15d\n", date, filesChanged, linesAdded, linesDeleted);
    }
}
