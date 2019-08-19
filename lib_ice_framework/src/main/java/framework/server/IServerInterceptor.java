package framework.server;

/**
 * @Author: leeping
 * @Date: 2019/3/7 13:37
 * 拦截器
 */

public interface IServerInterceptor {

    class InterceptorResult{
        private final boolean isInterceptor;
        private final String cause;
        public InterceptorResult(boolean isInterceptor, String cause) {
            this.isInterceptor = isInterceptor;
            this.cause = cause;
        }

        public boolean isInterceptor() {
            return isInterceptor;
        }

        public String getCause() {
            return cause;
        }
    }

    /**
     * 如果拦截返回结果,否则NULL
     */
    InterceptorResult interceptor(IceSessionContext context);
    int getPriority();
}
