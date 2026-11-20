package com.mojang.renderpearl.api.pipeline;

public enum BlendOp {
   ADD,
   SUBTRACT,
   REVERSE_SUBTRACT,
   MIN,
   MAX;

   // $FF: synthetic method
   private static BlendOp[] $values() {
      return new BlendOp[]{ADD, SUBTRACT, REVERSE_SUBTRACT, MIN, MAX};
   }
}
