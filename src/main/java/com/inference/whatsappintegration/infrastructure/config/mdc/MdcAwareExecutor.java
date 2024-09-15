package com.inference.whatsappintegration.infrastructure.config.mdc;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class MdcAwareExecutor {

    private final ExecutorService executorService = Executors.newFixedThreadPool(15);

    public <T> Future<T> submit(Callable<T> task) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return executorService.submit(wrapWithMdcContext(task, context));
    }

    public void execute(Runnable task) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        executorService.execute(wrapWithMdcContext(task, context));
    }

    private <T> Callable<T> wrapWithMdcContext(Callable<T> task, Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                return task.call();
            } finally {
                MDC.clear();
            }
        };
    }

    private Runnable wrapWithMdcContext(Runnable task, Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                task.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
