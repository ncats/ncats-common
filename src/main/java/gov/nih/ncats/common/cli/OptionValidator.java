package gov.nih.ncats.common.cli;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by katzelda on 6/21/17.
 */
public interface OptionValidator<T> {

    static <T> OptionValidator<T> noOp(){
        return  t-> true;
    }

    boolean isValid(T value) throws Throwable;
    
    
    default Consumer<T> validateConsumer(Consumer<T> consumer){
        Objects.requireNonNull(consumer);
        return t ->{
            try {
                if (isValid(t)) {
                    consumer.accept(t);
                }else{
                    throw new ValidationError("invalid argument " + t);
                }
            }catch (Throwable throwable){
                throw new ValidationError(throwable);
            }
        };
    }

}
