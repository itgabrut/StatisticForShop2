package com.ilya.interceptors.annotations;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by ilya on 14.10.2016.
 */



@InterceptorBinding
@Target( { METHOD, TYPE } )
@Retention( RUNTIME )
public @interface Audited {
}
