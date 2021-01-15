package pl.sdadas.gitdmp.jira;

import pl.sdadas.gitdmp.model.JiraRef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraTaskPattern implements Serializable {

    private final Pattern pattern;

    public JiraTaskPattern(JiraRef ref) {
        this.pattern = createPattern(ref);
    }

    private Pattern createPattern(JiraRef ref) {
        String url = ref.jira().getUrl().strip();
        if(!url.endsWith("/")) url += "/";
        url += "browse/";
        String re = String.format("(\\s|^)(%s)?([A-Z]+\\-\\d+)(\\s|$)", Pattern.quote(url));
        return Pattern.compile(re, Pattern.MULTILINE | Pattern.UNICODE_CASE);
    }

    public List<String> extractTaskIds(String value) {
        List<String> results = new ArrayList<>();
        if(value == null) return results;
        Matcher matcher = pattern.matcher(value);
        while(matcher.find()) {
            String taskId = matcher.group(3);
            results.add(taskId);
        }
        return results;
    }
}
