package org.test.jagger.practice;

import com.griddynamics.jagger.invoker.v2.JHttpQuery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResponseHeadersQueriesProvider implements Iterable {

    private List<JHttpQuery> queries = new ArrayList<>();

    public ResponseHeadersQueriesProvider() {
        Path path = Paths.get("testdata.txt");
        try {
            for (String line : Files.readAllLines(path)) {
                String[] pair = line.trim().split("=");

                queries.add(new JHttpQuery()
                        .get()
                        .path("/response-headers")
                        .queryParam(pair[0], pair[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator iterator() {
        return queries.iterator();
    }
}
