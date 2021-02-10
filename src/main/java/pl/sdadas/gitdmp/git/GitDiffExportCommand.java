package pl.sdadas.gitdmp.git;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sdadas.gitdmp.jira.model.TaskDetails;
import pl.sdadas.gitdmp.model.RepoRef;
import pl.sdadas.gitdmp.model.config.GitdmpConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class GitDiffExportCommand implements Callable<GitDiffStats> {

    private final static Logger LOG = LoggerFactory.getLogger(GitDiffExportCommand.class);

    private final Git git;

    private final RevCommit commit;

    private final GitdmpConfig config;

    private final RepoRef repo;

    private final GitDiffStats stats;

    private Map<String, TaskDetails> tasks;

    public GitDiffExportCommand(Git git, RevCommit commit, GitdmpConfig config, RepoRef repo) {
        this.git = git;
        this.commit = commit;
        this.config = config;
        this.repo = repo;
        Instant commitTime = Instant.ofEpochSecond(commit.getCommitTime());
        LocalDateTime dateTime = LocalDateTime.ofInstant(commitTime, TimeZone.getDefault().toZoneId());
        this.stats = new GitDiffStats(dateTime.toLocalDate());
    }

    @Override
    public GitDiffStats call() throws IOException {
        int numParents = commit.getParentCount();
        if(numParents > 1) return null;
        File outputDir = new File(config.getArgs().getOutputDir());
        File projectDir = new File(outputDir, repo.repo().getId());
        File commitDir = new File(projectDir, commit.getId().name());
        if(commitDir.exists()) FileUtils.deleteDirectory(commitDir);
        LOG.info("exporting commit {}", commit.getFullMessage().replace("\n", " "));
        writeDiffFile(commitDir);
        writeCommitInfoFile(commitDir);
        handleTaskGrouping(commitDir);
        return stats;
    }

    private void handleTaskGrouping(File commitDir) throws IOException {
        if(tasks == null || tasks.isEmpty()) return;
        for (TaskDetails details : tasks.values()) {
            writeTaskInfoFile(commitDir, details);
        }
        for (Map.Entry<String, TaskDetails> entry : tasks.entrySet()) {
            moveCommitToTaskDir(commitDir, entry.getKey());
        }
        FileUtils.deleteDirectory(commitDir);
    }

    private void writeTaskInfoFile(File commitDir, TaskDetails details) throws IOException {
        if(details == null) return;
        File infoFile = new File(commitDir, details.getKey() + ".txt");
        String text = details.createInfo();
        FileUtils.writeStringToFile(infoFile, text, StandardCharsets.UTF_8);
    }

    private void moveCommitToTaskDir(File commitDir, String taskId) throws IOException {
        File taskDir = new File(commitDir.getParentFile(), taskId);
        File newCommitDir = new File(taskDir, commitDir.getName());
        if(newCommitDir.exists()) FileUtils.deleteDirectory(newCommitDir);
        FileUtils.copyDirectory(commitDir, newCommitDir);
    }

    private void writeDiffFile(File commitDir) {
        File diffFile = new File(commitDir, "commit.diff");
        diffFile.getParentFile().mkdirs();
        AnyObjectId parent = parentId();
        try(FileOutputStream fos = new FileOutputStream(diffFile)) {
            DiffFormatter df = new DiffFormatter(fos);
            df.setRepository(git.getRepository());
            List<DiffEntry> entries = df.scan(parent, commit);
            for (DiffEntry entry : entries) {
                FileHeader header = df.toFileHeader(entry);
                stats.add(header.toEditList());
                df.format(header);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeCommitInfoFile(File commitDir) {
        File infoFile = new File(commitDir, "commit.txt");
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZ");
        try(PrintWriter writer = new PrintWriter(infoFile, StandardCharsets.UTF_8)) {
            PersonIdent author = commit.getAuthorIdent();
            Date when = author.getWhen();
            String header = String.format("%s <%s> %s\n", author.getName(), author.getEmailAddress(), df.format(when));
            writer.write(repo.repo().getUrl());
            writer.write("\n");
            writer.write(header);
            writer.write(commit.getFullMessage());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private AnyObjectId parentId() {
        int numParents = commit.getParentCount();
        if(numParents > 1) {
            return null; // merge commit
        } else if(numParents == 0) {
            return null; // initial commit
        } else {
            return commit.getParents()[0];
        }
    }

    public void setTasks(Map<String, TaskDetails> tasks) {
        this.tasks = tasks;
    }
}
