package pl.sdadas.gitdmp.model.config;

import pl.sdadas.gitdmp.model.JiraRef;
import pl.sdadas.gitdmp.model.RepoRef;

import java.io.Serializable;
import java.util.List;

public class GitdmpConfig implements Serializable {

    private List<String> emails;

    private List<GitdmpCredentials> credentials;

    private List<GitdmpRepo> repos;

    private String storageDir;

    private GitdmpArgs args;

    private GitdmpJira jira;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<GitdmpCredentials> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<GitdmpCredentials> credentials) {
        this.credentials = credentials;
    }

    public List<GitdmpRepo> getRepos() {
        return repos;
    }

    public void setRepos(List<GitdmpRepo> repos) {
        this.repos = repos;
    }

    public String getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    public GitdmpArgs getArgs() {
        return args;
    }

    public void setArgs(GitdmpArgs args) {
        this.args = args;
    }

    public GitdmpJira getJira() {
        return jira;
    }

    public void setJira(GitdmpJira jira) {
        this.jira = jira;
    }

    public GitdmpRepo findRepo(String repoId) {
        if(repos == null) return null;
        return repos.stream().filter(val -> repoId.equals(val.getId())).findAny().orElse(null);
    }

    public GitdmpCredentials findCredentials(String credentialsId) {
        if(credentials == null) return null;
        return credentials.stream().filter(val -> credentialsId.equals(val.getId())).findAny().orElse(null);
    }

    public RepoRef repoRef(String repoId) {
        return new RepoRef(this, repoId);
    }

    public JiraRef jiraRef() {
        if(jira == null) {
            return null;
        } else {
            GitdmpCredentials credentials = findCredentials(jira.getCredentials());
            return new JiraRef(jira, credentials);
        }
    }
}
