package com.me.job.april.string;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InputStreamToStringTest {
    /**
     * 使用普通的 Java，一个 InputStream 和一个简单的 StringBuilder
     */
    @Test
    public void convertingAnInputStreamToAString() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        assertThat(textBuilder.toString(), is(originalString));
    }

    /**
     * Java 8 给 BufferedReader 带来了一个新的 lines() 方法。
     * 让我们看看如何利用它将一个 InputStream 转换为一个字符串。
     */
    @Test
    public void convertingAnInputStreamToAStringInJavaEight() {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        String text = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        assertThat(text, is(originalString));
    }

    /**
     * 如果我们在 Java 9 或以上版本，
     * 我们可以利用一个新的 readAllBytes 方法添加到 InputStream 中。
     * 我们需要注意的是，这段简单的代码是为那些方便将所有字节读入字节数组的简单情况准备的。
     * 我们不应该用它来读取有大量数据的输入流。
     */
    @Test
    public void convertingAnInputStreamToAStringInJavaNine() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        assertThat(text, is(originalString));
    }

    /**
     * 使用标准文本扫描器的普通Java例子
     * InputStream 将被关闭的 Scanner 关闭。
     * 同样值得澄清的是 useDelimiter("\A") 的作用。这里我们传递了'\A'，
     * 它是一个边界标记重码，表示输入的开始。本质上，这意味着next()调用读取了整个输入流。
     * \A means "start of string", and \z means "end of string".
     * You might have seen ^ and $ in this context, but their meaning can vary:
     * If you compile a regex using Pattern.MULTILINE,
     * then they change their meaning to "start of line" and "end of line".
     * The meaning of \A and \z never changes.
     * There also exists \Z which means "end of string,
     * before any trailing newlines",
     * which is similar to what $ does in multiline mode
     * (where it matches right before the line-ending newline character, if it's there).
     *
     * scanner.useDelimiter命令在于设置当前scanner的分隔符,默认是空格,\A为正则表达式,表示从字符头开始
     * 这条语句的整体意思就是读取所有输入,包括回车换行符
     *
     */
    @Test
    public void convertingAnInputStreamToAStringInScanner() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        String text = null;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }

        assertThat(text, is(originalString));
    }

    /**
     * InputStream 通过读写字节块被转换为 ByteArrayOutputStream。
     * 然后 OutputStream 被转换为一个字节数组，用来创建一个字符串。
     */
    @Test
    public void convertingAnInputStreamToAStringInOutputStream() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int nRead;
        byte[] buffer = new byte[1024];
        while ((nRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, nRead);
        }

        outputStream.flush();

        String text = outputStream.toString(StandardCharsets.UTF_8);
        assertThat(text, is(originalString));
    }

    /**
     * 将 InputStream 的内容复制到一个文件中，然后将其转换为一个字符串。
     * 这里我们使用 java.nio.file.Files 类来创建一个临时文件，
     * 同时将 InputStream 的内容复制到文件中。
     * 然后用同一个类用 readAllBytes() 方法将文件内容转换为一个字符串。
     */
    @Test
    public void convertingAnInputStreamToAStringInNio() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        Path tempFile = Files.createTempDirectory("").resolve(UUID.randomUUID() + ".tmp");

        String defaultTempDir = System.getProperty("java.io.tmpdir");

        System.out.println(defaultTempDir);
        System.out.println(tempFile);

        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        String result = new String(Files.readAllBytes(tempFile));
        tempFile.toFile().deleteOnExit();
        assertThat(result, is(originalString));
    }

    /**
     * 用Guava进行转换
     * 首先，我们把我们的 InputStream 包装成一个 ByteSource.
     * 其次，我们把 ByteSource 看作是一个具有 UTF8 字符集的 CharSource。
     * 最后，我们使用 CharSource 将其作为一个字符串来读取。
     *
     * 注意：需要明确地关闭流
     */
    @Test
    public void convertingAnInputStreamToAStringInGuava() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return inputStream;
            }
        };

        String text = byteSource.asCharSource(Charsets.UTF_8).read();

        assertThat(text, is(originalString));
    }

    /**
     *  用Guava CharStreams进行转换
     *  使用 try-with-resources 语法来处理流的关闭
     */
    @Test
    public void convertingAnInputStreamToAStringInGuava2() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        String text = null;
        try (Reader reader = new InputStreamReader(inputStream)) {
            text = CharStreams.toString(reader);
        }

        assertThat(text, is(originalString));
    }

    /**
     * 用 Apache Commons IO 进行转换
     */
    @Test
    public void convertingAnInputStreamToAStringInCommonIO() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        assertThat(text, is(originalString));
    }

    @Test
    public void convertingAnInputStreamToAStringInCommonIO2() throws IOException {
        String originalString = RandomString.randomString(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        StringWriter writer = new StringWriter();
        String encoding = StandardCharsets.UTF_8.name();
        IOUtils.copy(inputStream, writer, encoding);

        assertThat(writer.toString(), is(originalString));
    }
}
