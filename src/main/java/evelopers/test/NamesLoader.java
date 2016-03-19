package evelopers.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class NamesLoader {

    private static final String DELIMETER = ",";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Month, List> map = new HashMap<>();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public NamesLoader() throws IOException, URISyntaxException {
            URI uri = Thread.currentThread().getContextClassLoader().getResource("people.txt").toURI();
            Path path = Paths.get(uri);
            try (Stream<String> lines = Files.lines(path);) {
                lines.forEach(str -> {
                    String[] split = str.split(DELIMETER);
                    LocalDate date = LocalDate.parse(split[0], formatter);
                    Month month = date.getMonth();
                    List list = map.get(month);
                    if(list == null) {
                        list = new LinkedList<String>();
                        map.put(month, list);
                    }
                    list.add(split[1]);
                    logger.info("added " + split[1]);
                });
            }
    }

    public List getNames(Month month) {
        return map.get(month);
    }
}
