package pl.sdadas.gitdmp.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskComments implements Serializable {

    private List<TaskComment> comments;

    public List<TaskComment> getComments() {
        return comments;
    }

    public void setComments(List<TaskComment> comments) {
        this.comments = comments;
    }

    public boolean isEmpty() {
        return comments == null || comments.isEmpty();
    }
}
