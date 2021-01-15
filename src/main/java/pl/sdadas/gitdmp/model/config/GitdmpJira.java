package pl.sdadas.gitdmp.model.config;

import java.io.Serializable;

public class GitdmpJira implements Serializable {

    private String url;

    private String credentials;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
