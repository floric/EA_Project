package org.floric.studies.evo.project2.io;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExportResult {
    private List<Double> score = Lists.newLinkedList();
    private List<Double> validIndividuumsRatio = Lists.newLinkedList();
    private List<Double> avgScore = Lists.newLinkedList();
    private List<Double> minScore = Lists.newLinkedList();
    private List<Integer> bestIndividuum = Lists.newLinkedList();
    private Map<Integer, List<Integer>> solutions = Maps.newHashMap();
}
