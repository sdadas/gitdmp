package pl.sdadas.gitdmp.jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sdadas.gitdmp.jira.model.TaskDetails;
import pl.sdadas.gitdmp.model.JiraRef;
import pl.sdadas.gitdmp.model.config.GitdmpCredentials;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteJiraClient implements JiraClient {

    private final static Logger LOG = LoggerFactory.getLogger(RemoteJiraClient.class);

    private final Map<String, TaskDetails> taskCache = new HashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();

    private final JiraRef ref;

    private final JiraTaskPattern pattern;

    private final OkHttpClient client = new OkHttpClient();

    public RemoteJiraClient(JiraRef ref) {
        this.ref = ref;
        this.pattern = new JiraTaskPattern(ref);
    }

    @Override
    public List<String> extractTaskIds(String message) {
        return pattern.extractTaskIds(message);
    }

    @Override
    public TaskDetails task(String taskId) {
        if(!taskCache.containsKey(taskId)) {
            TaskDetails details = findTask(taskId);
            taskCache.put(taskId, details);
        }
        return taskCache.get(taskId);
    }

    private TaskDetails findTask(String taskId) {
        String url = ref.jira().getUrl();
        if(!url.endsWith("/")) url += "/";
        url += "rest/api/latest/issue/" + taskId;
        GitdmpCredentials creds = ref.credentials();
        String userpass = creds.getUsername() + ":" + creds.getPassword();
        String auth = Base64.getEncoder().encodeToString(userpass.getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder().url(url).addHeader("Authorization", "Basic " + auth).get().build();
        LOG.info("fetching task {} info from jira", taskId);
        try(Response resp = client.newCall(request).execute()) {
            int code = resp.code();
            if(code == 200 && resp.body() != null) {
                String body = resp.body().string();
                return mapper.readValue(body, TaskDetails.class);
            } else if(code == 401) {
                throw new IllegalStateException(taskId + ": fetch task unauthorized (401) status");
            } else {
                LOG.info("fetch task received status code {}", code);
                return null;
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
