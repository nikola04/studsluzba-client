package org.raflab.studsluzbadesktopclient.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public class SpringContextHelper {
    @Getter
    @Setter
    private static ApplicationContext context;

}
