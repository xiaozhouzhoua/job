package com.me.job.problem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.management.ManagementFactory;
import java.util.stream.Collectors;

@RestController
public class ArgsErrorController {
    @GetMapping("args")
    public void error() {
        //wrong: java -jar job-SNAPSHOT.jar -Xms1g -Xmx1g
        //right: java -Xms1g -Xmx1g -jar job-0.0.1-SNAPSHOT.jar
        System.out.println("VM Options");
        System.out.println(ManagementFactory.getRuntimeMXBean().getInputArguments()
                .stream()
                .collect(Collectors.joining(System.lineSeparator())));
    }
}
