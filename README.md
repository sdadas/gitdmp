# gitdmp
A tool for automatic export of commits made by a particular user to a list of predefined git repositories. 
Commits are exported in diff file format along with their metadata. The application can be integrated with Jira, which allows fetching additional information about tasks mentioned in commit messages.
Useful for generating reports of work performed by a user within a specified time period.

### Getting started

#### Command line interface

Before the first run, create a new config file according to the format described in the next section. Then run the app using the following command:

```java -jar gitdump.jar [...arguments]```

| Argument              | Description |
|-----------------------|-------------|
| `-c, --config`        | Path to the configuration file (default value: `config.json`) |
| `-o, --output-dir`    | Output directory (default value: `output_{timestamp}`)|
| `-t, --time`          | Date range mode for filtering commits. Possible values are: <br/>`THIS_MONTH` (default value, fetches only commits for the current month),<br/>`LAST_MONTH` (fetches commits for the previous month),<br/>`CUSTOM` (custom date range, `--date-from` and `--date-to` args are required for this option) |
| `--date-from`         | Starting date (expected format `yyyy-MM-dd`)|
| `--date-to`           | Ending date (expected format `yyyy-MM-dd`)|
| `--stats`             | Print daily stats after export (number of files changed, lines addded, lines removed)

#### Configuration file

Configuration file provides a list of git repositories to scan, a list of users' e-mail addresses to identify which commits should be exported, and optionally Jira specific configuration.
Example configuration file in the JSON format:

```
{
  "emails": ["myemail@domain.com", "mysecondemail@example.com"],
  "credentials": [
    {
      "id": "git-creds",
      "username": "github_user",
      "password": "passwordForGit"
    },
    {
      "id": "jira-creds",
      "username": "jira_user",
      "password": "passwordForJira"
    }
  ],
  "repos": [
    {
      "id": "gitdmp",
      "url": "https://github.com/sdadas/gitdmp",
      "credentials": "git-creds"
    }
  ],
  "jira": {
    "url": "https://jira.myprivatedomain.com",
    "credentials": "jira-creds"
  },
  "storageDir": ".repos"
}
```

| Section               | Description |
|-----------------------|-------------|
| `emails`              | List of e-mail addresses of user whose commits should be extracted |
| `credentials`         | List of credentials objects. Single object includes an identifier, username and password. |
| `repos`               | List of git repository objects. Repository should consist of an identifier, repository url and optionally a reference to credentials object (if access to the repository required authorization). |
| `jira`                | Optional jira object. The section should include Jira url and reference to credentials object. |
| `storageDir`          | Path to the directory where git repositories should be cloned to. On the first run application creates this directory and clones all repositories provided in the `repos` section. During subsequent runs application reuses downloaded repositories and only fetches new commits. |