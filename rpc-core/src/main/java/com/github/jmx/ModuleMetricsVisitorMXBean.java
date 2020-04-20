package com.github.jmx;

import java.util.List;


public interface ModuleMetricsVisitorMXBean {
    List<ModuleMetricsVisitor> getModuleMetricsVisitor();

    void addModuleMetricsVisitor(ModuleMetricsVisitor visitor);
}

