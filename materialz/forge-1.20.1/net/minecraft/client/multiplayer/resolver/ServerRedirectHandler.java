package net.minecraft.client.multiplayer.resolver;

import com.mojang.logging.LogUtils;
import java.util.Hashtable;
import java.util.Optional;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface ServerRedirectHandler {
   Logger LOGGER = LogUtils.getLogger();
   ServerRedirectHandler EMPTY = (p_171897_) -> Optional.empty();

   Optional<ServerAddress> lookupRedirect(ServerAddress var1);

   static ServerRedirectHandler createDnsSrvRedirectHandler() {
      DirContext $$2;
      try {
         String $$0 = "com.sun.jndi.dns.DnsContextFactory";
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable<String, String> $$1 = new Hashtable();
         $$1.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         $$1.put("java.naming.provider.url", "dns:");
         $$1.put("com.sun.jndi.dns.timeout.retries", "1");
         $$2 = new InitialDirContext($$1);
      } catch (Throwable $$3) {
         LOGGER.error("Failed to initialize SRV redirect resolved, some servers might not work", $$3);
         return EMPTY;
      }

      return (p_171900_) -> {
         if (p_171900_.getPort() == 25565) {
            try {
               Attributes $$2 = $$2.getAttributes("_minecraft._tcp." + p_171900_.getHost(), new String[]{"SRV"});
               Attribute $$3 = $$2.get("srv");
               if ($$3 != null) {
                  String[] $$4 = $$3.get().toString().split(" ", 4);
                  return Optional.of(new ServerAddress($$4[3], ServerAddress.parsePort($$4[2])));
               }
            } catch (Throwable var5) {
            }
         }

         return Optional.empty();
      };
   }
}
