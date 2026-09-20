package io.github.tcq1007.springbootutils.normal;

import java.util.function.Supplier;

public class ContextRunner {
    static <T> void run(ScopedValue<T> scopedValue, T context, Runnable runnable) {
        ScopedValue.where(scopedValue, context).run(runnable);
    }

    static <T> T call(ScopedValue<T> scopedValue, T context, Supplier<T> supplier) {
        return ScopedValue.where(scopedValue, context).call(supplier::get);
    }
}
