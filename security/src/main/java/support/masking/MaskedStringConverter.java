package support.masking;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MaskedStringConverter implements Converter<String, MaskedString> {

    @Override
    public MaskedString convert(String source) {
        return new MaskedString(source);
    }
}
