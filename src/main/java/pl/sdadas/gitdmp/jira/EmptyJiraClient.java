package pl.sdadas.gitdmp.jira;

import pl.sdadas.gitdmp.jira.model.TaskDetails;

import java.util.Collections;
import java.util.List;

public class EmptyJiraClient implements JiraClient {

    @Override
    public List<String> extractTaskIds(String message) {
        return Collections.emptyList();
    }

    @Override
    public TaskDetails task(String taskId) {
        return null;
    }
}
