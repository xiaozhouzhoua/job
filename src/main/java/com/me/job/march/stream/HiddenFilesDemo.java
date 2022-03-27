package com.me.job.march.stream;

import java.io.File;
import java.util.Arrays;

public class HiddenFilesDemo {
    public static void main(String[] args) {
        File[] hiddenFiles = new File(".").listFiles(File::isHidden);
        assert hiddenFiles != null;
        Arrays.stream(hiddenFiles).forEach(System.out::println);
    }
}
