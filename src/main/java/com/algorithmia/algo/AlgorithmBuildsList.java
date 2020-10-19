package com.algorithmia.algo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public final class AlgorithmBuildsList {
    private String marker;
    private String next_link;
    private List<Algorithm.Build> results = new LinkedList<Algorithm.Build>();

    @Override
    public String toString() {
        return "AlgorithmBuildsList{" +
                "marker='" + marker + '\'' +
                ", next_link='" + next_link + '\'' +
                ", results=" + results +
                '}';
    }
}
