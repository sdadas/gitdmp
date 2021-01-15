package pl.sdadas.gitdmp.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskFields implements Serializable {

    private String summary;

    private String description;

    private String created;

    private String updated;

    private TaskPerson assignee;

    private TaskPerson creator;

    private TaskComments comment;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public TaskPerson getAssignee() {
        return assignee;
    }

    public void setAssignee(TaskPerson assignee) {
        this.assignee = assignee;
    }

    public TaskPerson getCreator() {
        return creator;
    }

    public void setCreator(TaskPerson creator) {
        this.creator = creator;
    }

    public TaskComments getComment() {
        return comment;
    }

    public void setComment(TaskComments comment) {
        this.comment = comment;
    }

    public String createInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("Title: ").append(summary).append('\n');
        builder.append("Reporter: ").append(creator).append('\n');
        builder.append("Assignee: ").append(assignee).append('\n');
        builder.append("Created: ").append(created).append('\n');
        builder.append("Updated: ").append(updated).append('\n');
        builder.append("\n========== Description ==========\n");
        builder.append(description);
        if(comment != null && !comment.isEmpty()) {
            builder.append("\n\n========== Comments ==========\n");
            comment.getComments().forEach(val -> builder.append(val.createInfo()).append("\n\n"));
        }
        return builder.toString();
    }
}
