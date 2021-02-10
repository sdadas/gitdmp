package pl.sdadas.gitdmp.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

public class GitDiffStatsCommand implements Callable<Void> {

    private final static Logger LOG = LoggerFactory.getLogger(GitDiffStatsCommand.class);

    private Map<LocalDate, GitDiffStats> days = new TreeMap<>();

    public void add(GitDiffStats stats) {
        LocalDate day = stats.day();
        GitDiffStats found = days.get(day);
        if(found == null) {
            found = new GitDiffStats(day);
            days.put(day, found);
        }
        found.add(stats);
    }

    @Override
    public Void call() {
        StringBuilder builder = new StringBuilder("Commits summary: \n");
        days.values().forEach(val -> builder.append(val.toString()));
        LOG.info(builder.toString());
        return null;
    }
}
