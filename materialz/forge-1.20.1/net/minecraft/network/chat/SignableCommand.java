package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.arguments.SignedArgument;

public record SignableCommand<S>(List<Argument<S>> arguments) {
   public SignableCommand(List<Argument<S>> arguments) {
      this.arguments = arguments;
   }

   public static <S> SignableCommand<S> of(ParseResults<S> p_250316_) {
      String $$1 = p_250316_.getReader().getString();
      CommandContextBuilder<S> $$2 = p_250316_.getContext();
      CommandContextBuilder<S> $$3 = $$2;

      List<Argument<S>> $$4;
      CommandContextBuilder<S> $$5;
      for($$4 = collectArguments($$1, $$2); ($$5 = $$3.getChild()) != null; $$3 = $$5) {
         boolean $$6 = $$5.getRootNode() != $$2.getRootNode();
         if (!$$6) {
            break;
         }

         $$4.addAll(collectArguments($$1, $$5));
      }

      return new SignableCommand<S>($$4);
   }

   private static <S> List<Argument<S>> collectArguments(String p_252055_, CommandContextBuilder<S> p_251770_) {
      List<Argument<S>> $$2 = new ArrayList();

      for(ParsedCommandNode<S> $$3 : p_251770_.getNodes()) {
         CommandNode var6 = $$3.getNode();
         if (var6 instanceof ArgumentCommandNode<S, ?> $$4) {
            if ($$4.getType() instanceof SignedArgument) {
               ParsedArgument<S, ?> $$5 = (ParsedArgument)p_251770_.getArguments().get($$4.getName());
               if ($$5 != null) {
                  String $$6 = $$5.getRange().get(p_252055_);
                  $$2.add(new Argument($$4, $$6));
               }
            }
         }
      }

      return $$2;
   }

   public static record Argument<S>(ArgumentCommandNode<S, ?> node, String value) {
      public Argument(ArgumentCommandNode<S, ?> node, String value) {
         this.node = node;
         this.value = value;
      }

      public String name() {
         return this.node.getName();
      }
   }
}
