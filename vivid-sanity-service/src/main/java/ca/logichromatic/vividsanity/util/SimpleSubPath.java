package ca.logichromatic.vividsanity.util;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class SimpleSubPath {
    protected String path;
    public static SimpleSubPathBuilder builder() {
        return new SimpleSubPathBuilder();
    }

    public static class SimpleSubPathBuilder {
        List<String> paths = new ArrayList<>();

        public SimpleSubPathBuilder path(String path) {
            if (path == null) {
                return this;
            }
            paths.add(path.replace("/", ""));
            return this;
        }

        public SimpleSubPath build() {
            return new SimpleSubPath().setPath(paths.stream().collect(Collectors.joining("/", "/", "")));
        }
    }
}
