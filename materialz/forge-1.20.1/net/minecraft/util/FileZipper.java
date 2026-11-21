package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import org.slf4j.Logger;

public class FileZipper implements Closeable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path outputFile;
   private final Path tempFile;
   private final FileSystem fs;

   public FileZipper(Path p_144697_) {
      this.outputFile = p_144697_;
      this.tempFile = p_144697_.resolveSibling(p_144697_.getFileName().toString() + "_tmp");

      try {
         this.fs = Util.ZIP_FILE_SYSTEM_PROVIDER.newFileSystem(this.tempFile, ImmutableMap.of("create", "true"));
      } catch (IOException $$1) {
         throw new UncheckedIOException($$1);
      }
   }

   public void add(Path p_144704_, String p_144705_) {
      try {
         Path $$2 = this.fs.getPath(File.separator);
         Path $$3 = $$2.resolve(p_144704_.toString());
         Files.createDirectories($$3.getParent());
         Files.write($$3, p_144705_.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
      } catch (IOException $$4) {
         throw new UncheckedIOException($$4);
      }
   }

   public void add(Path p_144701_, File p_144702_) {
      try {
         Path $$2 = this.fs.getPath(File.separator);
         Path $$3 = $$2.resolve(p_144701_.toString());
         Files.createDirectories($$3.getParent());
         Files.copy(p_144702_.toPath(), $$3);
      } catch (IOException $$4) {
         throw new UncheckedIOException($$4);
      }
   }

   public void add(Path p_144699_) {
      try {
         Path $$1 = this.fs.getPath(File.separator);
         if (Files.isRegularFile(p_144699_, new LinkOption[0])) {
            Path $$2 = $$1.resolve(p_144699_.getParent().relativize(p_144699_).toString());
            Files.copy($$2, p_144699_);
         } else {
            Stream<Path> $$3 = Files.find(p_144699_, Integer.MAX_VALUE, (p_144707_, p_144708_) -> p_144708_.isRegularFile(), new FileVisitOption[0]);

            try {
               for(Path $$4 : (List)$$3.collect(Collectors.toList())) {
                  Path $$5 = $$1.resolve(p_144699_.relativize($$4).toString());
                  Files.createDirectories($$5.getParent());
                  Files.copy($$4, $$5);
               }
            } catch (Throwable var8) {
               if ($$3 != null) {
                  try {
                     $$3.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if ($$3 != null) {
               $$3.close();
            }

         }
      } catch (IOException $$6) {
         throw new UncheckedIOException($$6);
      }
   }

   public void close() {
      try {
         this.fs.close();
         Files.move(this.tempFile, this.outputFile);
         LOGGER.info("Compressed to {}", this.outputFile);
      } catch (IOException $$0) {
         throw new UncheckedIOException($$0);
      }
   }
}
