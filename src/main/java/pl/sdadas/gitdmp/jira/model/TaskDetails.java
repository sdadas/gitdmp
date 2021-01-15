package pl.sdadas.gitdmp.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDetails implements Serializable {

    private String key;

    private TaskFields fields;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TaskFields getFields() {
        return fields;
    }

    public void setFields(TaskFields fields) {
        this.fields = fields;
    }

    public String createInfo() {
        return fields.createInfo();
    }
}
