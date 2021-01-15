package pl.sdadas.gitdmp.model;

import pl.sdadas.gitdmp.model.config.GitdmpConfig;
import pl.sdadas.gitdmp.model.config.GitdmpCredentials;
import pl.sdadas.gitdmp.model.config.GitdmpRepo;

import java.io.File;
import java.io.Serializable;

public class RepoRef implements Serializable {

    private final GitdmpRepo repo;

    private final GitdmpCredentials credentials;

    private final File dir;

    public RepoRef(GitdmpConfig config, String repoId) {
        this.repo = config.findRepo(repoId);
        this.credentials = config.findCredentials(repo.getCredentials());
        this.dir = new File(config.getStorageDir(), repo.getId());
    }

    public GitdmpRepo repo() {
        return repo;
    }

    public GitdmpCredentials credentials() {
        return credentials;
    }

    public File dir() {
        return dir;
    }
}
