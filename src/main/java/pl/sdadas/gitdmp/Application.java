package pl.sdadas.gitdmp;

import picocli.CommandLine;
import pl.sdadas.gitdmp.cli.CliCommand;

public class Application {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CliCommand()).execute(args);
        System.exit(exitCode);
    }
}
