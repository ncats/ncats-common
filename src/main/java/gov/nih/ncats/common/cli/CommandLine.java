package gov.nih.ncats.common.cli;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by katzelda on 6/21/17.
 */
public class CommandLine implements OptionVisitor{

    private final Options options = new Options();

    private Map<String, Consumer<String>> consumerMap = new HashMap<>();

    private List<ValidationBuilder> validationBuilders = new LinkedList<>();

    Deque<ValidationBuilder> validationStack = new ArrayDeque<>();

    public CommandLine(){
        BasicValidationBuilder builder = new BasicValidationBuilder();
        validationBuilders.add(builder);
        validationStack.push(builder);
    }

    public CommandLine addOptions(CliOption...options){

        for(CliOption o : options){
            o.visit(this);
        }



        return this;
    }
    public void parse(URL url) throws IOException{
        List<String> args = new ArrayList<>();

        String[] split = url.getQuery().split("&");
        if(split !=null){
            for(String s : split){
                int index = s.indexOf('=');
                if(index >0){
                    args.add("-"+ URLDecoder.decode( s.substring(0, index), "UTF-8"));
                    args.add(URLDecoder.decode( s.substring(index+1, s.length()), "UTF-8"));
                }else{
                    args.add("-"+ URLDecoder.decode( s, "UTF-8"));
                }
            }
        }

        parse(args.toArray(new String[args.size()]));
    }
    public void parse(String allArgs)  throws IOException{
        parse(allArgs.split(" "));
    }
    public void parse(String arg0, String... args) throws IOException{
        String[] combined = new String[args.length +1];

        combined[0] = arg0;
        System.arraycopy(args,0,combined,1,args.length);

        parse(combined);
    }
    public void parse(String[] args) throws IOException {

        CommandLineParser parser = new DefaultParser();
        try {
            org.apache.commons.cli.CommandLine cmd = parser.parse( options, args, true);
            //if we get this far, we've validated the args as far as apache cli is concerned

            //need to add extra validation now

            Set<String> seen = Arrays.stream(cmd.getOptions())
                                        .map(org.apache.commons.cli.Option::getOpt)
                                        .collect(Collectors.toSet());

            for(ValidationBuilder validation : validationBuilders){
                validation.validate(seen);
            }
            //need to call consumers
            for(String opt : seen){
                Consumer<String> consumer = consumerMap.get(opt);
                if(consumer !=null){
                    String optionValue = cmd.getOptionValue(opt);
                    //there's leading whitespace !!?
                    if(optionValue !=null){
                        optionValue = optionValue.trim();
                    }
                    consumer.accept(optionValue);
                }
            }
        } catch (ParseException e) {
           throw new IOException(e);
        }
    }

    @Override
    public void visit(org.apache.commons.cli.Option apacheOption, Consumer<String> consumer) {
        options.addOption(apacheOption);
        if(consumer !=null) {
            consumerMap.put(apacheOption.getOpt(), consumer);
        }
        if(apacheOption.isRequired()) {
            validationStack.peek().addRequired(apacheOption.getOpt());
        }else{
            validationStack.peek().addOptional(apacheOption.getOpt());
        }
    }

    @Override
    public void visit(Option option) {
        visit(option.asApacheOption(), option.getConsumer());
    }

    @Override
    public void preVisit(RadioGroup group) {
        RadioValidationBuilder builder = new RadioValidationBuilder(group.isRequired());
        validationBuilders.add(builder);
        validationStack.push(builder);
    }

    @Override
    public void postVisit(RadioGroup group) {
        validationStack.pop();
    }

    //have to have a "get seen list which is options that are in the parsed command line
    //need to recursevly search through sub options.
    //this is used for error reporting

    //need to have group validation to make sure that
    //options that are required are


    private static class RadioValidationBuilder implements ValidationBuilder{
        private Set<String> oneOf = new LinkedHashSet<>();

        private final boolean isRequired;

        public RadioValidationBuilder(boolean isRequired) {
            this.isRequired = isRequired;
        }

        private void add(String n){
            oneOf.add(n);
        }

        @Override
        public void addOptional(String name) {
            add(name);
        }

        @Override
        public void addRequired(String name) {
            add(name);
        }

        @Override
        public void validate(Set<String> seenOptions) throws ValidationError {
            List<String> seen = new ArrayList<>(1);
            for(String n : oneOf){
                if(seenOptions.contains(n)){
                    seen.add(n);
                }
            }

            int numSeen = seen.size();
            if(numSeen >1){
                throw new ValidationError("must choose at only 1 radio option but saw : " + seen);
            }
            if(numSeen==0 && isRequired){
                throw new ValidationError("must choose radio option of either : " + oneOf);
            }
        }
    }

    private static class BasicValidationBuilder implements ValidationBuilder{
        Set<String> required = new LinkedHashSet<>();
        Set<String> optional = new LinkedHashSet<>();

        @Override
        public void addOptional(String name) {
            optional.add(name);
        }

        @Override
        public void addRequired(String name) {
            required.add(name);
        }

        @Override
        public void validate(Set<String> seenOptions) {
            for(String n : required){
                if(!seenOptions.contains(n)){
                    //TODO make find all mising options?
                    throw new ValidationError("missing required option "+ n);
                }
            }
        }
    }

    private interface ValidationBuilder{
        void addRequired(String name);

        void addOptional(String name);

        void validate(Set<String> seenOptions) throws ValidationError;
    }
}
