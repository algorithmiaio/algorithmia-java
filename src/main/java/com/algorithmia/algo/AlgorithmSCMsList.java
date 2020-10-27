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
public final class AlgorithmSCMsList {
    private List<Algorithm.SCM> results = new LinkedList<Algorithm.SCM>();

    @Override
    public String toString() {
        return "AlgorithmSCMsList{" +
                "results=" + results +
                '}';
    }
}
