package pl.sdadas.gitdmp.model.config;

import java.io.Serializable;
import java.util.List;

public class GitdmpRepo implements Serializable {

    private String id;

    private String url;

    private String credentials;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
