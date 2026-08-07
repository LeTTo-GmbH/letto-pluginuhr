package at.letto.basespringboot.cmd;

import at.letto.tools.Datum;
import at.letto.tools.threads.ThreadStatus;
import lombok.Getter;
import org.unbescape.html.HtmlEscape;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.interrupted;

/**
 * Objekt, das für jedes asynchron gestartete Kommando erzeugt wird.
 */
@Getter
public class CmdThread implements Runnable {

    public enum CmdMode {
        NORMAL,
        CMD,
        BATCH,
        BASH,
        SH
    }

    private static final AtomicLong ID_COUNTER = new AtomicLong();
    private static final Pattern COMMAND_MARKER =
            Pattern.compile("^XXX(\\d+):(.*)$");

    protected final CmdMode cmdMode;
    protected final long id;
    protected final String homedir;
    protected final String[] cmd;
    protected final String command;
    protected final Thread thread;
    protected final long starttime;
    protected final Date startdate;

    protected String charset;
    protected long stoptime = 0;
    protected volatile ThreadStatus threadStatus = ThreadStatus.NEW;
    protected volatile Throwable error = null;

    private final Vector<Vector<String>> out = new Vector<>();
    private final Vector<Vector<String>> err = new Vector<>();
    protected final Vector<String> htmlOutput = new Vector<>();

    protected String backlink = "";
    protected String template = "";

    public volatile File batchfile;
    protected volatile Process p = null;

    public CmdThread(
            String homedir,
            String charset,
            CmdMode cmdMode,
            String... cmd
    ) {
        this.id = ID_COUNTER.incrementAndGet();
        this.homedir = homedir == null ? "" : homedir;
        this.charset = charset == null || charset.isBlank()
                ? StandardCharsets.UTF_8.name()
                : charset;
        this.cmdMode = Objects.requireNonNullElse(cmdMode, CmdMode.NORMAL);

        List<String> commands = new ArrayList<>();

        if (cmd != null) {
            for (String commandText : cmd) {
                if (commandText == null) {
                    continue;
                }

                String normalized = commandText.replace("\r", "").trim();

                for (String line : normalized.split("\n")) {
                    String trimmed = line.trim();

                    if (trimmed.isEmpty()
                            || trimmed.startsWith("#")
                            || trimmed.startsWith("/*")
                            || trimmed.startsWith("//")) {
                        continue;
                    }

                    commands.add(trimmed);
                }
            }
        }

        this.cmd = commands.toArray(String[]::new);
        this.command = String.join(", ", this.cmd);

        this.thread = new Thread(this, "CmdThread-" + id);
        this.starttime = System.currentTimeMillis();
        this.startdate = new Date();
    }

    public static CmdThread createThread(
            String homedir,
            String charset,
            String... cmd
    ) {
        CmdThread thread =
                new CmdThread(homedir, charset, CmdMode.NORMAL, cmd);
        thread.start();
        return thread;
    }

    public static CmdThread createThread(
            String homedir,
            String charset,
            CmdMode cmdMode,
            String... cmd
    ) {
        CmdThread thread =
                new CmdThread(homedir, charset, cmdMode, cmd);
        thread.start();
        return thread;
    }

    public static CmdThread createThreadMessage(String message) {
        CmdThread thread = new CmdThread(
                "",
                StandardCharsets.UTF_8.name(),
                CmdMode.NORMAL,
                "message"
        );

        thread.htmlOutput.add(
                "<div style=\"color:blue;\">"
                        + HtmlEscape.escapeHtml5(message)
                        + "</div>"
        );
        thread.threadStatus = ThreadStatus.FINISHED;
        thread.stoptime = System.currentTimeMillis();

        return thread;
    }

    public CmdThread backlink(String backlink) {
        this.backlink = backlink == null ? "" : backlink;
        return this;
    }

    public CmdThread template(String template) {
        this.template = template == null ? "" : template;
        return this;
    }

    public void start() {
        thread.start();
    }

    /**
     * Gibt ein Kommando blau und HTML-escaped aus.
     */
    public void htmlCmd(String cmd) {
        htmlOutput.add(
                "<div style=\"color:blue;\">"
                        + HtmlEscape.escapeHtml5(cmd)
                        + "</div>"
        );
    }

    /**
     * Gibt ein Kommando blau aus.
     *
     * Der Name wurde aus Kompatibilitätsgründen beibehalten.
     */
    protected void htmlCmdPlain(String cmd) {
        htmlOutput.add(
                "<div style=\"color:blue;\">"
                        + HtmlEscape.escapeHtml5(cmd)
                        + "</div>"
        );
    }

    /**
     * Gibt eine normale Ausgabe HTML-escaped aus.
     */
    public void htmlOut(String text) {
        htmlOutput.add(
                "<div style=\"color:black;\">"
                        + HtmlEscape.escapeHtml5(text)
                        + "</div>"
        );
    }

    /**
     * Gibt eine normale Ausgabe aus.
     *
     * Der Name wurde aus Kompatibilitätsgründen beibehalten.
     */
    protected void htmlOutPlain(String text) {
        htmlOutput.add(
                "<div style=\"color:black;\">"
                        + HtmlEscape.escapeHtml5(text)
                        + "</div>"
        );
    }

    /**
     * Gibt eine Fehlerausgabe rot und HTML-escaped aus.
     */
    public void htmlErr(String text) {
        htmlOutput.add(
                "<div style=\"color:red;\">"
                        + HtmlEscape.escapeHtml5(text)
                        + "</div>"
        );
    }

    /**
     * Gibt eine Fehlerausgabe rot aus.
     *
     * Der Name wurde aus Kompatibilitätsgründen beibehalten.
     */
    protected void htmlErrPlain(String text) {
        htmlOutput.add(
                "<div style=\"color:red;\">"
                        + HtmlEscape.escapeHtml5(text)
                        + "</div>"
        );
    }

    /**
     * Führt mehrere Kommandos aus.
     */
    public final void runCmd(String... commands) {
        if (commands == null || commands.length == 0) {
            return;
        }

        if (isScriptMode()) {
            runAsScript(commands);
            return;
        }

        for (String currentCommand : commands) {
            if (currentCommand == null || currentCommand.isBlank()) {
                continue;
            }

            htmlCmd(currentCommand);

            Vector<String> currentOut = new Vector<>();
            Vector<String> currentErr = new Vector<>();

            out.add(currentOut);
            err.add(currentErr);

            systemcall(
                    currentCommand,
                    charset,
                    currentOut,
                    currentErr
            );

            if (Thread.currentThread().isInterrupted()) {
                return;
            }
        }
    }

    private boolean isScriptMode() {
        return cmdMode == CmdMode.BATCH
                || cmdMode == CmdMode.BASH
                || cmdMode == CmdMode.SH;
    }

    private void runAsScript(String[] commands) {
        Vector<String> currentOut = new Vector<>();
        Vector<String> currentErr = new Vector<>();

        out.add(currentOut);
        err.add(currentErr);

        try {
            batchfile = createScriptFile(commands);
            htmlCmd(batchfile.getAbsolutePath());

            List<String> processCommand = switch (cmdMode) {
                case BASH -> List.of(
                        "/bin/bash",
                        "-e",
                        batchfile.getAbsolutePath()
                );
                case SH -> List.of(
                        "/bin/sh",
                        "-e",
                        batchfile.getAbsolutePath()
                );
                case BATCH -> List.of(
                        "cmd.exe",
                        "/c",
                        batchfile.getAbsolutePath()
                );
                default -> throw new IllegalStateException(
                        "Kein Script-Modus: " + cmdMode
                );
            };

            executeProcess(
                    processCommand,
                    charset,
                    currentOut,
                    currentErr
            );
        } catch (IOException ex) {
            registerStartError(
                    "Script kann nicht erstellt oder gestartet werden",
                    ex,
                    currentOut,
                    currentErr
            );
        } finally {
            deleteBatchFile();
        }
    }

    private File createScriptFile(String[] commands) throws IOException {
        Path directory = resolveWorkingDirectoryForTempFile();
        String suffix = cmdMode == CmdMode.BATCH ? ".bat" : ".sh";

        Path scriptPath = Files.createTempFile(
                directory,
                "letto-cmd-",
                suffix
        );

        List<String> lines = new ArrayList<>();

        if (cmdMode == CmdMode.BATCH) {
            lines.add("@echo off");
        } else if (cmdMode == CmdMode.BASH) {
            lines.add("#!/bin/bash");
        } else {
            lines.add("#!/bin/sh");
        }

        for (int i = 0; i < commands.length; i++) {
            String currentCommand =
                    commands[i] == null ? "" : commands[i];

            if (cmdMode == CmdMode.BATCH) {
                lines.add("echo XXX" + i + ":" + currentCommand);
            } else {
                lines.add(
                        "printf '%s\\n' "
                                + shellQuote(
                                "XXX" + i + ":" + currentCommand
                        )
                );
            }

            lines.add(currentCommand);
        }

        Files.write(
                scriptPath,
                lines,
                resolveCharset(charset),
                StandardOpenOption.TRUNCATE_EXISTING
        );

        File scriptFile = scriptPath.toFile();

        if (cmdMode != CmdMode.BATCH) {
            scriptFile.setExecutable(true, true);
        }

        return scriptFile;
    }

    private Path resolveWorkingDirectoryForTempFile() throws IOException {
        if (homedir != null && !homedir.isBlank()) {
            Path path = Path.of(homedir).toAbsolutePath().normalize();

            if (!Files.isDirectory(path)) {
                throw new IOException(
                        "Arbeitsverzeichnis existiert nicht "
                                + "oder ist kein Verzeichnis: "
                                + path
                );
            }

            return path;
        }

        Path currentDirectory =
                Path.of(System.getProperty("user.dir"))
                        .toAbsolutePath()
                        .normalize();

        if (Files.isDirectory(currentDirectory)
                && Files.isWritable(currentDirectory)) {
            return currentDirectory;
        }

        return Path.of(System.getProperty("java.io.tmpdir"))
                .toAbsolutePath()
                .normalize();
    }

    private static String shellQuote(String value) {
        return "'"
                + value.replace("'", "'\"'\"'")
                + "'";
    }

    public void task() {
        runCmd(cmd);
    }

    @Override
    public void run() {
        threadStatus = ThreadStatus.RUNNING;

        try {
            task();

            if (Thread.currentThread().isInterrupted()) {
                threadStatus = ThreadStatus.STOPPED;
            } else if (threadStatus != ThreadStatus.ERROR) {
                threadStatus = ThreadStatus.FINISHED;
            }
        } catch (Throwable throwable) {
            error = throwable;
            threadStatus = ThreadStatus.ERROR;
            htmlErr(
                    throwable.getMessage() == null
                            ? throwable.getClass().getName()
                            : throwable.getMessage()
            );
        } finally {
            deleteBatchFile();
            stoptime = System.currentTimeMillis();
        }
    }

    public String getHtmlOutput() {
        StringBuilder result = new StringBuilder();

        synchronized (htmlOutput) {
            for (String line : htmlOutput) {
                result.append(line);
            }
        }

        return result.toString();
    }

    public void systemcall(
            String cmd,
            String charset,
            Vector<String> out
    ) {
        systemcall(cmd, charset, out, null);
    }

    /**
     * Führt ein Kommando aus und wartet auf dessen Beendigung.
     */
    public void systemcall(
            String cmd,
            String charset,
            Vector<String> out,
            Vector<String> err
    ) {
        Objects.requireNonNull(out, "out darf nicht null sein");

        try {
            List<String> processCommand = buildProcessCommand(cmd);

            executeProcess(
                    processCommand,
                    charset,
                    out,
                    err
            );
        } catch (IOException ex) {
            registerStartError(
                    cmd + " kann nicht gestartet werden",
                    ex,
                    out,
                    err
            );
        }
    }

    private List<String> buildProcessCommand(String commandText)
            throws IOException {

        if (commandText == null || commandText.isBlank()) {
            throw new IOException("Das Kommando ist leer.");
        }

        if (cmdMode == CmdMode.CMD) {
            return List.of("cmd.exe", "/c", commandText);
        }

        List<String> arguments = parseCommandLine(commandText);

        if (arguments.isEmpty()) {
            throw new IOException("Das Kommando ist leer.");
        }

        return arguments;
    }

    /**
     * Zerlegt eine Kommandozeile in Programm und Argumente.
     *
     * Unterstützt einfache und doppelte Anführungszeichen sowie Backslash-
     * Escaping. Shell-Operatoren wie {@code |}, {@code >}, {@code &&} oder
     * Variablenexpansion werden
     * im NORMAL-Modus absichtlich nicht ausgewertet.
     */
    private static List<String> parseCommandLine(String commandText)
            throws IOException {

        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean escaped = false;
        boolean tokenStarted = false;

        for (int i = 0; i < commandText.length(); i++) {
            char c = commandText.charAt(i);

            if (escaped) {
                current.append(c);
                tokenStarted = true;
                escaped = false;
                continue;
            }

            if (c == '\\' && !inSingleQuotes) {
                escaped = true;
                tokenStarted = true;
                continue;
            }

            if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                tokenStarted = true;
                continue;
            }

            if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
                tokenStarted = true;
                continue;
            }

            if (Character.isWhitespace(c)
                    && !inSingleQuotes
                    && !inDoubleQuotes) {

                if (tokenStarted) {
                    result.add(current.toString());
                    current.setLength(0);
                    tokenStarted = false;
                }

                continue;
            }

            current.append(c);
            tokenStarted = true;
        }

        if (escaped) {
            current.append('\\');
        }

        if (inSingleQuotes || inDoubleQuotes) {
            throw new IOException(
                    "Nicht geschlossenes Anführungszeichen im Kommando: "
                            + commandText
            );
        }

        if (tokenStarted) {
            result.add(current.toString());
        }

        return result;
    }

    private void executeProcess(
            List<String> processCommand,
            String charsetName,
            Vector<String> out,
            Vector<String> err
    ) throws IOException {

        Charset processCharset = resolveCharset(charsetName);
        ProcessBuilder processBuilder =
                new ProcessBuilder(processCommand);

        if (err == null) {
            processBuilder.redirectErrorStream(true);
        }

        configureWorkingDirectory(processBuilder);

        Process process = processBuilder.start();
        p = process;

        Thread stdoutReader = Thread.ofVirtual()
                .name("CmdThread-" + id + "-stdout")
                .start(() -> readStream(
                        process.getInputStream(),
                        processCharset,
                        out,
                        false
                ));

        Thread stderrReader = null;

        if (err != null) {
            stderrReader = Thread.ofVirtual()
                    .name("CmdThread-" + id + "-stderr")
                    .start(() -> readStream(
                            process.getErrorStream(),
                            processCharset,
                            err,
                            true
                    ));
        }

        try {
            int exitCode = process.waitFor();

            stdoutReader.join();

            if (stderrReader != null) {
                stderrReader.join();
            }

            if (exitCode != 0) {
                String message =
                        "Kommando wurde mit Exit-Code "
                                + exitCode
                                + " beendet: "
                                + String.join(" ", processCommand);

                if (err == null) {
                    out.add(message);
                    htmlErr(message);
                } else {
                    err.add(message);
                    htmlErr(message);
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            process.destroy();

            try {
                if (!process.waitFor(
                        500,
                        java.util.concurrent.TimeUnit.MILLISECONDS
                )) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException interruptedAgain) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
        } finally {
            p = null;
        }
    }

    private void configureWorkingDirectory(
            ProcessBuilder processBuilder
    ) throws IOException {

        if (homedir == null || homedir.isBlank()) {
            return;
        }

        Path directory =
                Path.of(homedir).toAbsolutePath().normalize();

        if (!Files.isDirectory(directory)) {
            throw new IOException(
                    "Arbeitsverzeichnis existiert nicht "
                            + "oder ist kein Verzeichnis: "
                            + directory
            );
        }

        processBuilder.directory(directory.toFile());
    }

    private void readStream(
            InputStream inputStream,
            Charset charset,
            Vector<String> target,
            boolean errorStream
    ) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, charset)
        )) {
            String line;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = COMMAND_MARKER.matcher(line);

                if (!errorStream && matcher.matches()) {
                    htmlCmd(matcher.group(2));
                } else {
                    target.add(line);

                    if (errorStream) {
                        htmlErr(line);
                    } else {
                        htmlOut(line);
                    }
                }
            }
        } catch (IOException ex) {
            if (p != null && p.isAlive()) {
                String message =
                        "Fehler beim Lesen der Prozessausgabe: "
                                + ex.getMessage();

                target.add(message);
                htmlErr(message);
            }
        }
    }

    private static Charset resolveCharset(String charsetName) {
        if (charsetName == null || charsetName.isBlank()) {
            return StandardCharsets.UTF_8;
        }

        return Charset.forName(charsetName);
    }

    private void registerStartError(
            String prefix,
            IOException ex,
            Vector<String> out,
            Vector<String> err
    ) {
        error = ex;
        threadStatus = ThreadStatus.ERROR;

        String message = prefix + ": " + ex.getMessage();

        if (err == null) {
            out.add(message);
        } else {
            err.add(message);
        }

        htmlErr(message);
    }

    private void deleteBatchFile() {
        File file = batchfile;
        batchfile = null;

        if (file == null) {
            return;
        }

        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException ex) {
            htmlErr(
                    "Temporäre Scriptdatei konnte nicht gelöscht werden: "
                            + file.getAbsolutePath()
                            + " – "
                            + ex.getMessage()
            );
        }
    }

    public void stop() {
        if (threadStatus != ThreadStatus.RUNNING) {
            return;
        }

        thread.interrupt();

        Process process = p;

        if (process != null && process.isAlive()) {
            process.destroy();

            try {
                if (!process.waitFor(
                        500,
                        java.util.concurrent.TimeUnit.MILLISECONDS
                )) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
        }

        try {
            thread.join(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        if (thread.isAlive()) {
            threadStatus = ThreadStatus.ZOMBIE;
        } else {
            threadStatus = ThreadStatus.STOPPED;
        }

        deleteBatchFile();
    }

    public String getTimeInfoHTML() {
        long end = isFinished()
                ? stoptime
                : System.currentTimeMillis();

        return "<span style=\"color:orange\">"
                + Datum.formatDateTime(startdate)
                + "</span> - <span style=\"color:blue\">"
                + (end - starttime) / 1000.0
                + " s </span>";
    }

    public boolean isFinished() {
        return switch (threadStatus) {
            case NEW, RUNNING -> false;
            case ZOMBIE, ERROR, STOPPED, FINISHED -> true;
            default -> true;
        };
    }

    public CmdDto getCmdDto() {
        CmdDto cmdDto = new CmdDto();

        cmdDto.setCmd(String.join("\n", getCmd()));
        cmdDto.setHomedir(getHomedir());
        cmdDto.setBacklink(getBacklink());
        cmdDto.setId(getId());
        cmdDto.setUserAction("");

        return cmdDto;
    }

    public String lastOutputLine(int lines) {
        if (lines <= 0 || out.isEmpty()) {
            return "";
        }

        Vector<String> lastCommand = out.lastElement();

        if (lastCommand == null || lastCommand.isEmpty()) {
            return "";
        }

        int start = Math.max(0, lastCommand.size() - lines);
        StringBuilder result = new StringBuilder();

        for (int i = start; i < lastCommand.size(); i++) {
            if (!result.isEmpty()) {
                result.append(' ');
            }

            result.append(lastCommand.get(i));
        }

        return result.toString();
    }

    /**
     * Wartet die angegebene Anzahl Sekunden.
     */
    public void wait(int seconds) {
        waitms(seconds * 1000);
    }

    /**
     * Wartet die angegebene Anzahl Millisekunden.
     */
    public void waitms(int milliseconds) {
        if (milliseconds <= 0) {
            return;
        }

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
