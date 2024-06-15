package vn.com.ecommerceapi.configurations;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@Configuration
public class ExecutorConfiguration {

    @Bean
    @Primary
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("AsyncThreadPool-");
        executor.setTaskDecorator(new AsyncTaskDecorator());
        executor.initialize();
        return executor;
    }

    // Class này sử dụng với mục đích truyền thông tin của Request gốcvà sessionId vào Thread mới được tạo
    static class AsyncTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (servletRequestAttributes != null) {
                        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
                    }
                    MDC.setContextMap(contextMap);
                    runnable.run();
                } finally {
                    MDC.clear();
                    if (servletRequestAttributes != null) {
                        RequestContextHolder.setRequestAttributes(null);
                    }
                }
            };
        }
    }

}
