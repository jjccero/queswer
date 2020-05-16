package com.gzu.queswer.util;

import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class AnalysisUtil {
    private AnalysisUtil() {
    }

    private static Set<String> unexpectedNature;

    static {
        //过滤null 标点符号
        String naturesString = "null w";
        unexpectedNature = new HashSet<>(Arrays.asList(naturesString.split(" ")));
    }

    public static Set<String> analysisString(String string) {
        Result result = ToAnalysis.parse(string.toLowerCase());
        List<Term> terms = result.getTerms();
        Set<String> strings = new HashSet<>(terms.size());
        for (Term term : terms) {
            String natureStr=term.getNatureStr();
            if (unexpectedNature.contains(natureStr)) continue;
            strings.add(term.getName());
        }
        return strings;
    }
}
