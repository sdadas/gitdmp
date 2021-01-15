package pl.sdadas.gitdmp.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskComment implements Serializable {

    private TaskPerson author;

    private String created;

    private String body;

    public TaskPerson getAuthor() {
        return author;
    }

    public void setAuthor(TaskPerson author) {
        this.author = author;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String createInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("Author: ").append(author).append('\n');
        builder.append("Created: ").append(created).append('\n');
        builder.append(body);
        return builder.toString();
    }
}
