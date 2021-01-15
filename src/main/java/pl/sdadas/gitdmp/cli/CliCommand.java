package pl.sdadas.gitdmp.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine;
import pl.sdadas.gitdmp.git.GitReader;
import pl.sdadas.gitdmp.jira.EmptyJiraClient;
import pl.sdadas.gitdmp.jira.JiraClient;
import pl.sdadas.gitdmp.jira.RemoteJiraClient;
import pl.sdadas.gitdmp.model.JiraRef;
import pl.sdadas.gitdmp.model.TimeRange;
import pl.sdadas.gitdmp.model.config.GitdmpArgs;
import pl.sdadas.gitdmp.model.config.GitdmpConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@CommandLine.Command(name = "git")
public class CliCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--config"}, defaultValue = "config.json")
    private String config;

    @CommandLine.Option(names = {"-o", "--output-dir"}, defaultValue = "output")
    private String output;

    @CommandLine.Option(names = {"-t", "--time"}, defaultValue = "THIS_MONTH")
    private TimeRange time;

    @CommandLine.Option(names = {"--date-from"})
    private LocalDate from;

    @CommandLine.Option(names = {"--date-to"})
    private LocalDate to;

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        GitdmpConfig config = createConfig(mapper);
        config.setArgs(createArgs());
        JiraClient jiraClient = createJiraClient(config);
        GitReader git = new GitReader(config, jiraClient);
        try {
            git.readAll();
        } catch (GitAPIException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private GitdmpConfig createConfig(ObjectMapper mapper) {
        File configFile = new File(config);
        try(FileReader reader = new FileReader(configFile, StandardCharsets.UTF_8)) {
            return mapper.readValue(reader, GitdmpConfig.class);
        } catch (IOException ex) {
            throw new IllegalStateException("config file (" + config + ") not found", ex);
        }
    }

    private JiraClient createJiraClient(GitdmpConfig config) {
        if(config.getJira() == null) {
            return new EmptyJiraClient();
        } else {
            JiraRef ref = config.jiraRef();
            return new RemoteJiraClient(ref);
        }
    }

    private GitdmpArgs createArgs() {
        GitdmpArgs args = new GitdmpArgs();
        args.setOutputDir(output);
        if(TimeRange.CUSTOM.equals(time)) {
            if(from == null || to == null) {
                throw new IllegalArgumentException("--date-from and --date-to args are required with custom range");
            } else if(from.isAfter(to)) {
                throw new IllegalArgumentException("--date-from is after --date-to");
            }
            args.setFrom(from);
            args.setTo(to);
        } else {
            args.setFrom(time.getFrom());
            args.setTo(time.getTo());
        }
        return args;
    }
}
