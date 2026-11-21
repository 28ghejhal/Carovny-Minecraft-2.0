package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public interface StrictQueue<T, F> {
   @Nullable
   F pop();

   boolean push(T var1);

   boolean isEmpty();

   int size();

   public static final class QueueStrictQueue<T> implements StrictQueue<T, T> {
      private final Queue<T> queue;

      public QueueStrictQueue(Queue<T> p_18792_) {
         this.queue = p_18792_;
      }

      @Nullable
      public T pop() {
         return (T)this.queue.poll();
      }

      public boolean push(T p_18795_) {
         return this.queue.add(p_18795_);
      }

      public boolean isEmpty() {
         return this.queue.isEmpty();
      }

      public int size() {
         return this.queue.size();
      }
   }

   public static final class IntRunnable implements Runnable {
      final int priority;
      private final Runnable task;

      public IntRunnable(int p_18786_, Runnable p_18787_) {
         this.priority = p_18786_;
         this.task = p_18787_;
      }

      public void run() {
         this.task.run();
      }

      public int getPriority() {
         return this.priority;
      }
   }

   public static final class FixedPriorityQueue implements StrictQueue<IntRunnable, Runnable> {
      private final Queue<Runnable>[] queues;
      private final AtomicInteger size = new AtomicInteger();

      public FixedPriorityQueue(int p_18773_) {
         this.queues = new Queue[p_18773_];

         for(int $$1 = 0; $$1 < p_18773_; ++$$1) {
            this.queues[$$1] = Queues.newConcurrentLinkedQueue();
         }

      }

      @Nullable
      public Runnable pop() {
         for(Queue<Runnable> $$0 : this.queues) {
            Runnable $$1 = (Runnable)$$0.poll();
            if ($$1 != null) {
               this.size.decrementAndGet();
               return $$1;
            }
         }

         return null;
      }

      public boolean push(IntRunnable p_18778_) {
         int $$1 = p_18778_.priority;
         if ($$1 < this.queues.length && $$1 >= 0) {
            this.queues[$$1].add(p_18778_);
            this.size.incrementAndGet();
            return true;
         } else {
            throw new IndexOutOfBoundsException(String.format(Locale.ROOT, "Priority %d not supported. Expected range [0-%d]", $$1, this.queues.length - 1));
         }
      }

      public boolean isEmpty() {
         return this.size.get() == 0;
      }

      public int size() {
         return this.size.get();
      }

      // $FF: synthetic method
      @Nullable
      public Object pop() {
         return this.pop();
      }
   }
}
