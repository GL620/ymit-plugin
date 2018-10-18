package io.jenkins.plugins.sample.envInject.service;

import javax.annotation.Nonnull;

/**
 * Created by phy on 2018/10/17.
 */
public class EnvInjectException extends Exception{
    public EnvInjectException(@Nonnull String s) {
        super(s);
    }

    public EnvInjectException(@Nonnull Throwable throwable) {
        super(throwable);
    }

    public EnvInjectException(@Nonnull String s, @Nonnull Throwable throwable) {
        super(s, throwable);
    }
}
