package pl.sdadas.gitdmp.jira;

import pl.sdadas.gitdmp.jira.model.TaskDetails;

import java.util.List;

public interface JiraClient {

    List<String> extractTaskIds(String message);

    TaskDetails task(String taskId);
}
