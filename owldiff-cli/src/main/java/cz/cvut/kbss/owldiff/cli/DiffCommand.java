package cz.cvut.kbss.owldiff.cli;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(name = "diff", mixinStandardHelpOptions = true,
        description = "Compares OWL ontologies.")
public class DiffCommand implements Callable<Integer> {

    @CommandLine.Parameters(description = "Original ontology file.")
    private String original;

    @CommandLine.Parameters(description = "Updated ontology file.")
    private String updated;

    public static void main(String[] args) {
        new CommandLine(new DiffCommand()).execute(args);
    }

    public Integer call() {
        try {
            new Diff().run(original, updated);
        } catch (Exception e) {
            log.error("Exception during command execution: {}", e.getMessage());
            log.debug("Details:", e);
            return -1;
        }
        return 0;
    }
}
