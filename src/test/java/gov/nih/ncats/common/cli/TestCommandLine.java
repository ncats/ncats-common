package gov.nih.ncats.common.cli;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;
/**
 * Created by katzelda on 6/22/17.
 */
public class TestCommandLine {

    @Test
    public void singleRequiredOption() throws IOException {
        new CommandLine()
                .addOptions(Option.required("foo"))
                .parse("-foo bar");
    }
    @Test
    public void singleRequiredFlag() throws IOException {
        new CommandLine()
                .addOptions(Option.required("foo").isFlag(true))
                .parse("-foo");
    }



    @Test
    public void withSetter() throws IOException{
        Example ex = new Example();
        new CommandLine()
                .addOptions(Option.required("foo")
                                .setter(ex::setFoo))
                .parse("-foo bar");

        assertEquals("bar", ex.getFoo());
    }

    @Test
    public void withFileSetter() throws IOException{
        Example ex = new Example();
        new CommandLine()
                .addOptions(Option.required("path")
                                    .setToFile(ex::setMyFile))

                .parse("-path /usr/local/foo/bar/baz.txt");

        assertEquals("/usr/local/foo/bar/baz.txt", ex.getMyFile().getAbsolutePath());
    }

    @Test
    public void multipleOptions() throws IOException{
        Example ex = new Example();
        new CommandLine()
                .addOptions(Option.required("path")
                                    .setToFile(ex::setMyFile),

                            Option.optional("a")
                        )

                .parse("-path /usr/local/foo/bar/baz.txt");

        assertEquals("/usr/local/foo/bar/baz.txt", ex.getMyFile().getAbsolutePath());
    }

    @Test
    public void asUrl() throws IOException{
        Example ex = new Example();
        new CommandLine()
                .addOptions(Option.required("path")
                                .setToFile(ex::setMyFile),

                        Option.optional("a").setToInt(ex::setA)
                )
                .parse(new URL("http://example.com?path=/usr/local/foo/bar/baz.txt&a=2"));

        assertEquals("/usr/local/foo/bar/baz.txt", ex.getMyFile().getAbsolutePath());
        assertEquals(2, ex.getA());

    }

    @Test
    public void optionalRadioGroup() throws IOException{

        new CommandLine()
                .addOptions( RadioGroup.optional(Option.optional("foo"),
                                                Option.optional("bar")
                        ))

                .parse(toArgList("-foo x"));
    }

    @Test(expected = ValidationError.class)
    public void optionalRadioGroupMultipleShouldThrowException() throws IOException{

        new CommandLine()
                .addOptions( RadioGroup.optional(Option.optional("foo"),
                                                    Option.optional("bar")
                ))

                .parse(new String[]{"-foo", "x", "-bar","y"});
    }
    @Test
    public void radioWithOtherOptions() throws IOException{
        Example ex = new Example();
        new CommandLine()
                .addOptions( RadioGroup.optional(Option.optional("foo"),
                                                Option.optional("bar")),
                        Option.required("path")
                                        .setToFile(ex::setMyFile)


                                )

                .parse(new String[]{"-path","/usr/local/foo/bar/baz.txt"});

        assertEquals("/usr/local/foo/bar/baz.txt", ex.getMyFile().getAbsolutePath());
    }

    @Test
    public void requireRadioWithOtherOptions() throws IOException{
        Example ex = new Example();
        new CommandLine()
                .addOptions( RadioGroup.required(Option.optional("foo"),
                        Option.optional("bar")),
                        Option.required("path")
                                .setToFile(ex::setMyFile)


                )

                .parse(new String[]{"-foo", "x", "-path","/usr/local/foo/bar/baz.txt"});

        assertEquals("/usr/local/foo/bar/baz.txt", ex.getMyFile().getAbsolutePath());
    }
    @Test
    public void radioWithOtherOptions2SeparateCalls() throws IOException{
        Example ex = new Example();
        new CommandLine()
                .addOptions( RadioGroup.optional(Option.optional("foo"),
                        Option.optional("bar")) )

                .addOptions( Option.required("path")
                        .setToFile(ex::setMyFile))

                .parse(new String[]{"-foo", "x", "-path","/usr/local/foo/bar/baz.txt"});

        assertEquals("/usr/local/foo/bar/baz.txt", ex.getMyFile().getAbsolutePath());
    }

    private static String[] toArgList(String s){
        return s.split(" ");
    }

    private static class Example{
        public String foo;

        public File myFile;

        public int a;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public File getMyFile() {
            return myFile;
        }

        public void setMyFile(File myFile) {
            this.myFile = myFile;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }
}
