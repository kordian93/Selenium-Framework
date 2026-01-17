package support.utils.reprostepslogger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReproStepsStage {
    NOT_STARTED("******* NOT STARTED *******"),
    SETUP("******* SETUP *******"),
    TEST("******* TEST *******"),
    TEARDOWN("******* TEARDOWN *******");

    private final String text;
}
