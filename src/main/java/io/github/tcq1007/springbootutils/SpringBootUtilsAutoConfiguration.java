package io.github.tcq1007.springbootutils;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = SpringBootUtilsAutoConfiguration.class)
public class SpringBootUtilsAutoConfiguration {
}
