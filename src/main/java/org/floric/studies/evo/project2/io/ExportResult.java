package org.floric.studies.evo.project2.io;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExportResult {
    private List<Double> score = Lists.newArrayList();
    private List<Double> avgScore = Lists.newArrayList();
    private List<Double> minScore = Lists.newArrayList();
    private Map<Integer, List<Integer>> solutions = Maps.newHashMap();
}
