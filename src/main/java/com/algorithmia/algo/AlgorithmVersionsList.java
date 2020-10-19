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
public final class AlgorithmVersionsList {
    private String marker;
    private String next_link;
    private List<Algorithm> results = new LinkedList<Algorithm>();

    @Override
    public String toString() {
        return "AlgorithmVersionsList{" +
                "marker='" + marker + '\'' +
                ", next_link='" + next_link + '\'' +
                ", results=" + results +
                '}';
    }
}
