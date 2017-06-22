package gov.nih.ncats.common.cli;

import org.apache.commons.cli.*;

import java.util.function.Consumer;

/**
 * Created by katzelda on 6/21/17.
 */
interface OptionVisitor {

    void visit(org.apache.commons.cli.Option apacheOption , Consumer<String> consumer);

    void visit(Option option);

    void preVisit(RadioGroup group);

    void postVisit(RadioGroup group);
}
