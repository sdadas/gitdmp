package pl.sdadas.gitdmp.model;

import pl.sdadas.gitdmp.model.config.GitdmpCredentials;
import pl.sdadas.gitdmp.model.config.GitdmpJira;

import java.io.Serializable;

public class JiraRef implements Serializable {

    private final GitdmpJira jira;

    private final GitdmpCredentials credentials;

    public JiraRef(GitdmpJira jira, GitdmpCredentials credentials) {
        this.jira = jira;
        this.credentials = credentials;
    }

    public GitdmpJira jira() {
        return jira;
    }

    public GitdmpCredentials credentials() {
        return credentials;
    }
}
