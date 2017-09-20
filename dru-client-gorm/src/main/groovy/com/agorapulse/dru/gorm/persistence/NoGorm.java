package com.agorapulse.dru.gorm.persistence;

import java.lang.annotation.*;

/**
 * Emphasizes that given test is not using any of GORM features.
 *
 * Apply this annotation to unit test class to hide ignore warning coming from {@link Gorm}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface NoGorm {


}
