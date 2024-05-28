package vn.com.ecommerceapi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegexUtils {

    RegexUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexUtils.class);

    public static final String EMAIL = "/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$/gm";

    public static boolean matches(String input, String regex) {
        return Pattern.compile(regex).matcher(input).matches();
    }

}
