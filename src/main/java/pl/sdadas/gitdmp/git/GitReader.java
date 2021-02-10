package pl.sdadas.gitdmp.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sdadas.gitdmp.jira.JiraClient;
import pl.sdadas.gitdmp.jira.model.TaskDetails;
import pl.sdadas.gitdmp.model.RepoRef;
import pl.sdadas.gitdmp.model.config.GitdmpArgs;
import pl.sdadas.gitdmp.model.config.GitdmpConfig;
import pl.sdadas.gitdmp.model.config.GitdmpCredentials;
import pl.sdadas.gitdmp.model.config.GitdmpRepo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GitReader {

    private final static Logger LOG = LoggerFactory.getLogger(GitReader.class);

    private final GitdmpConfig config;

    private final JiraClient jiraClient;

    private final GitDiffStatsCommand stats;

    public GitReader(GitdmpConfig config, JiraClient jiraClient) {
        this.config = config;
        this.jiraClient = jiraClient;
        this.stats = new GitDiffStatsCommand();
    }

    public void readAll() throws GitAPIException, IOException {
        List<GitdmpRepo> repos = config.getRepos();
        if(repos == null) return;
        for (GitdmpRepo repo : repos) {
            read(repo.getId());
        }
    }

    public void read(String repoId) throws GitAPIException, IOException {
        RepoRef ref = config.repoRef(repoId);
        Git git = cloneOrFetch(ref);
        findCommits(git, ref);
    }

    public void logStats() {
        stats.call();
    }

    private void findCommits(Git git, RepoRef ref) throws IOException, GitAPIException {
        GitdmpArgs args = config.getArgs();
        Set<String> emails = config.getEmails().stream().map(String::toLowerCase).collect(Collectors.toSet());
        LogCommand log = git.log().all();
        log.setRevFilter(CommitTimeRevFilter.between(args.getFromDate(), args.getToDate()));
        Iterable<RevCommit> commits = log.call();
        for (RevCommit commit : commits) {
            String email = commit.getAuthorIdent().getEmailAddress();
            if(emails.contains(email.toLowerCase())) {
                exportDiff(git, commit, ref);
            }
        }
    }

    private void exportDiff(Git git, RevCommit commit, RepoRef ref) throws IOException {
        GitDiffExportCommand command = new GitDiffExportCommand(git, commit, config, ref);
        Map<String, TaskDetails> tasks = fetchTaskDetails(commit);
        command.setTasks(tasks);
        GitDiffStats commitStats = command.call();
        stats.add(commitStats);
    }

    private Map<String, TaskDetails> fetchTaskDetails(RevCommit commit) {
        String message = commit.getFullMessage();
        List<String> taskIds = jiraClient.extractTaskIds(message);
        Map<String, TaskDetails> tasks = new HashMap<>();
        for (String taskId : taskIds) {
            TaskDetails task = jiraClient.task(taskId);
            tasks.put(taskId, task);
        }
        return tasks;
    }

    private Git cloneOrFetch(RepoRef ref) throws GitAPIException, IOException {
        File dir = ref.dir();
        GitdmpCredentials c = ref.credentials();
        CredentialsProvider creds = CredentialsProvider.getDefault();
        if(c != null) creds = new UsernamePasswordCredentialsProvider(c.getUsername(), c.getPassword());
        return dir.exists() ? fetch(ref, creds) : clone(ref, creds);
    }

    private Git fetch(RepoRef ref, CredentialsProvider creds) throws IOException, GitAPIException {
        File dir = ref.dir();
        LOG.info("Fetching repo {} in {}", ref.repo().getId(), dir.getAbsolutePath());
        Git git = Git.open(dir);
        List<RemoteConfig> remotes = git.remoteList().call();
        for (RemoteConfig remote : remotes) {
            git.fetch()
                    .setCredentialsProvider(creds)
                    .setRemote(remote.getName())
                    .setRefSpecs(remote.getFetchRefSpecs()).call();
        }
        return git;
    }

    private Git clone(RepoRef ref, CredentialsProvider creds) throws GitAPIException {
        File dir = ref.dir();
        LOG.info("Cloning repo {} to {}", ref.repo().getId(), dir.getAbsolutePath());
        GitdmpRepo repo = ref.repo();
        return Git.cloneRepository()
                .setURI(repo.getUrl())
                .setDirectory(dir)
                .setCloneAllBranches(true)
                .setCredentialsProvider(creds)
                .call();
    }
}
