package com.LGB.global.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            Map<String, String> previousContextMap = MDC.getCopyOfContextMap();

            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                } else {
                    MDC.clear();
                }

                runnable.run();
            } finally {
                if (previousContextMap != null) {
                    MDC.setContextMap(previousContextMap);
                } else {
                    MDC.clear();
                }
            }
        };
    }
}