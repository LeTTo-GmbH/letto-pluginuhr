package at.letto.queue;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Generische, threadsichere Task-Queue mit:
 *  - insertTop(item):   Einfügen am Kopf → als nächstes verarbeitet
 *  - insertEnd(item):   Einfügen am Ende → FIFO
 *  - insAtTime(item,m): geplanter Start frühestens in m Minuten
 *
 * Verzögerte Items (insAtTime) werden in eine DelayQueue gelegt und bei Fälligkeit
 * in die Ready-Deque verschoben. Duplikate (gleicher Schlüssel) in der DelayQueue
 * werden beim Einfügen entfernt und aktualisiert (Replanen).
 *
 * Siehe auch main am Ende für Beispielnutzung.
 *
 * @param <T> Task-Typ
 * @param <K> Schlüsseltyp zur Duplikatserkennung (muss equals/hashCode korrekt implementieren)
 */
public class TaskQueue<T, K> implements AutoCloseable {

    /** Verarbeitet ein Item. Darf Exceptions werfen – werden geloggt. */
    public interface TaskProcessor<T> {
        void process(T item) throws Exception;
    }

    /** Optionaler Fehler-Callback. */
    public interface ErrorHandler<T> {
        void onError(T item, Exception ex);
    }

    // ---------------- Konfiguration ----------------

    private final TaskProcessor<T> processor;
    private final ErrorHandler<T> errorHandler; // optional
    private final Function<T, K> keyExtractor;  // zur Duplikatserkennung bei Delays

    private final Duration idleStop;      // Worker endet nach dieser Leerlaufzeit
    private final Duration restartPeriod; // Watchdog-Takt

    // ---------------- Datenstrukturen ----------------

    /** Sofort verarbeitbare Items. */
    private final BlockingDeque<T> readyDeque = new LinkedBlockingDeque<>();

    /** Verzögerte Items. */
    private final DelayQueue<DelayedItem<T, K>> delayedQueue = new DelayQueue<>();

    /** Genau ein Worker gleichzeitig. */
    private final AtomicBoolean workerRunning = new AtomicBoolean(false);

    private final ExecutorService workerPool = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "generic-queue-worker");
        t.setDaemon(true);
        return t;
    });

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "generic-queue-scheduler");
        t.setDaemon(true);
        return t;
    });

    private final ScheduledExecutorService watchdog =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "generic-queue-watchdog");
                t.setDaemon(true);
                return t;
            });

    // ---------------- Konstruktion ----------------

    /**
     * Erzeugt eine generische Task-Queue.
     *
     * @param processor      Callback zur Verarbeitung eines Items
     * @param keyExtractor   extrahiert den eindeutigen Schlüssel aus einem Item (für Delay-Deduplikation)
     * @param idleStop       Leerlaufdauer bis der Worker stoppt (z. B. 2s)
     * @param restartPeriod  Prüfintervall, ob neu gestartet werden soll (z. B. 5min)
     * @param errorHandler   optionaler Fehler-Callback (kann null sein)
     */
    public TaskQueue(TaskProcessor<T> processor,
                     Function<T, K> keyExtractor,
                     Duration idleStop,
                     Duration restartPeriod,
                     ErrorHandler<T> errorHandler) {
        this.processor = Objects.requireNonNull(processor, "processor");
        this.keyExtractor = Objects.requireNonNull(keyExtractor, "keyExtractor");
        this.errorHandler = errorHandler;
        this.idleStop = idleStop != null ? idleStop : Duration.ofSeconds(2);
        this.restartPeriod = restartPeriod != null ? restartPeriod : Duration.ofMinutes(5);

        // Scheduler verschiebt fällige Items in die Ready-Deque
        scheduler.submit(this::runScheduler);

        // Watchdog stößt periodisch neu an (nur wenn Arbeit anliegt & kein Worker läuft)
        watchdog.scheduleAtFixedRate(this::ensureWorkerIfNeeded,
                this.restartPeriod.toMillis(), this.restartPeriod.toMillis(), TimeUnit.MILLISECONDS);
    }

    // ---------------- Öffentliche API ----------------

    /** Einfügen am Kopf → wird als nächstes verarbeitet. */
    public void insertTop(T item) {
        Objects.requireNonNull(item, "item");
        readyDeque.addFirst(item);
        ensureWorkerIfNeeded();
    }

    /** Einfügen am Ende (FIFO). */
    public void insertEnd(T item) {
        Objects.requireNonNull(item, "item");
        readyDeque.addLast(item);
        ensureWorkerIfNeeded();
    }

    /**
     * Einfügen mit frühestem Ausführungszeitpunkt (jetzt + minutes).
     * Existiert bereits ein verzögertes Item mit gleichem Schlüssel, wird es entfernt
     * und durch das neue (mit aktualisierter Fälligkeit) ersetzt.
     */
    public void insAtTime(T item, int minutes) {
        Objects.requireNonNull(item, "item");
        if (minutes <= 0) {
            insertEnd(item);
            return;
        }
        final Instant dueAt = Instant.now().plus(Duration.ofMinutes(minutes));
        final K key = keyExtractor.apply(item);

        // Deduplikation in DelayQueue (synchronisiert, da Iterator-basierte removeIf nicht threadsicher ist)
        synchronized (delayedQueue) {
            delayedQueue.removeIf(existing -> Objects.equals(existing.key, key));
            delayedQueue.put(DelayedItem.of(item, key, dueAt));
        }
        // Scheduler übernimmt das spätere Verschieben automatisch
    }

    /** Anzahl sofort verarbeitbarer Items. */
    public int readySize() { return readyDeque.size(); }

    /** Anzahl geplanter (noch nicht fälliger) Items. */
    public int delayedSize() { return delayedQueue.size(); }

    /** Ordnungsgemäßes Beenden. */
    @Override
    public void close() {
        watchdog.shutdownNow();
        scheduler.shutdownNow();
        workerPool.shutdownNow();
    }

    // ---------------- Scheduler & Worker ----------------

    private void runScheduler() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                DelayedItem<T, K> due = delayedQueue.take(); // blockiert bis fällig
                readyDeque.addLast(due.item);                // alternativ addFirst(), falls gewünscht
                ensureWorkerIfNeeded();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /** Startet Worker, wenn Arbeit anliegt und keiner läuft. */
    private void ensureWorkerIfNeeded() {
        if (!readyDeque.isEmpty() && workerRunning.compareAndSet(false, true)) {
            workerPool.submit(this::runWorker);
        }
    }

    /** Abarbeitung der Ready-Deque mit Idle-Abbruch. */
    private void runWorker() {
        try {
            final long idleMillis = Math.max(250L, idleStop.toMillis());
            while (true) {
                T item = readyDeque.poll(idleMillis, TimeUnit.MILLISECONDS);
                if (item == null) {
                    // Leerlauf → Worker beendet sich, Watchdog stößt später ggf. neu an
                    return;
                }
                try {
                    processor.process(item);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (Exception ex) {
                    if (errorHandler != null) {
                        errorHandler.onError(item, ex);
                    } else {
                        System.err.println("Fehler bei Verarbeitung: " + ex.getMessage());
                    }
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            workerRunning.set(false);
            if (!readyDeque.isEmpty()) {
                ensureWorkerIfNeeded();
            }
        }
    }

    // ---------------- DelayedItem ----------------

    /** Wrapper für DelayQueue – vergleicht nach Fälligkeit; Gleichheit über Schlüssel. */
    private static final class DelayedItem<T, K> implements Delayed {
        private static final AtomicLong SEQ = new AtomicLong(0);

        final T item;
        final K key;
        final long dueNanos; // absolute Zeit in nanoTime-Skala
        final long seq;      // FIFO bei gleicher Fälligkeit

        static <T, K> DelayedItem<T, K> of(T item, K key, Instant dueAt) {
            long delayNanos = Math.max(0, Duration.between(Instant.now(), dueAt).toNanos());
            return new DelayedItem<>(item, key, System.nanoTime() + delayNanos);
        }

        private DelayedItem(T item, K key, long dueNanos) {
            this.item = item;
            this.key = key;
            this.dueNanos = dueNanos;
            this.seq = SEQ.getAndIncrement();
        }

        @Override public long getDelay(TimeUnit unit) {
            return unit.convert(dueNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override public int compareTo(Delayed o) {
            DelayedItem<?, ?> other = (DelayedItem<?, ?>) o;
            int c = Long.compare(this.dueNanos, other.dueNanos);
            return (c != 0) ? c : Long.compare(this.seq, other.seq);
        }

        // equals/hashCode NUR über key → erlaubt removeIf anhand des Schlüssels
        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof DelayedItem<?, ?> di)) return false;
            return Objects.equals(this.key, di.key);
        }
        @Override public int hashCode() {
            return Objects.hashCode(key);
        }
    }

    // ---------------- Builder (optional) ----------------

    public static final class Builder<T, K> {
        private TaskProcessor<T> processor;
        private Function<T, K> keyExtractor;
        private Duration idleStop = Duration.ofSeconds(2);
        private Duration restartPeriod = Duration.ofMinutes(5);
        private ErrorHandler<T> errorHandler;

        public Builder<T, K> processor(TaskProcessor<T> p) { this.processor = p; return this; }
        public Builder<T, K> keyExtractor(Function<T, K> k) { this.keyExtractor = k; return this; }
        public Builder<T, K> idleStop(Duration d)           { this.idleStop = d;    return this; }
        public Builder<T, K> restartPeriod(Duration d)      { this.restartPeriod = d; return this; }
        public Builder<T, K> errorHandler(ErrorHandler<T> h){ this.errorHandler = h; return this; }

        public TaskQueue<T, K> build() {
            return new TaskQueue<>(processor, keyExtractor, idleStop, restartPeriod, errorHandler);
        }
    }



    // ---------------- Demo ----------------

    private static class Entry {
        private final int school;
        private final String idQuestion;

        public Entry(int school, String idQuestion) {
            this.school = school;
            this.idQuestion = idQuestion;
        }

        public int getSchool() { return school; }
        public String getIdQuestion() { return idQuestion; }
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        // Prozessor:
        TaskQueue.TaskProcessor<Entry> proc = e -> {
            int sec = (int)((System.currentTimeMillis()-start)/1000);
            System.out.println(Instant.now() + " Render: " + e.getSchool() + "-" + e.getIdQuestion() + " (" + sec + ")");
            Thread.sleep(2000);
        };
        // Optionaler Error-Handler:
        TaskQueue.ErrorHandler<Entry> err = (e, ex) ->
                System.err.println("Fehler bei " + e + ": " + ex.getMessage());

        // Schlüssel: school-idQuestion (z. B. "testschule-123")
        Function<Entry, String> keyFn = e -> e.getSchool() + "-" + e.getIdQuestion();

        // Queue bauen:
        var queue = new TaskQueue.Builder<Entry, String>()
                .processor(proc)
                .keyExtractor(keyFn)
                .idleStop(Duration.ofSeconds(2))
                .restartPeriod(Duration.ofMinutes(3))
                .errorHandler(err)
                .build();

        // Sofortige Jobs
        queue.insertEnd(new Entry(1, "immediate-1"));
        queue.insertEnd(new Entry(2, "immediate-2"));
        queue.insertTop(new Entry(3, "prio 1")); // kommt als erstes
        Thread.sleep(5000); // Demo warten
        queue.insertTop(new Entry(4, "prio 2")); // kommt als erstes
        queue.insertEnd(new Entry(5, "end-1"));

        Thread.sleep(5000); // Demo warten
        queue.insertTop(new Entry(99, "prio 3")); // kommt als erstes


        // Delay-Job: frühestens in 1 Minute
        queue.insAtTime(new Entry(2, "delay-1"), 1);
        Thread.sleep(10000); // Demo warten
        queue.insAtTime(new Entry(2, "delay-1"), 1);

        // Noch ein sofortiger Job
        queue.insertEnd(new Entry(6, "end-2"));

        Thread.sleep(100000); // Demo warten
        System.out.println("Demo Ende.");

    }

}
