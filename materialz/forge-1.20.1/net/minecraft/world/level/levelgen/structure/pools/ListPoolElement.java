package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ListPoolElement extends StructurePoolElement {
   public static final Codec<ListPoolElement> CODEC = RecordCodecBuilder.create((p_210367_) -> p_210367_.group(StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter((p_210369_) -> p_210369_.elements), projectionCodec()).apply(p_210367_, ListPoolElement::new));
   private final List<StructurePoolElement> elements;

   public ListPoolElement(List<StructurePoolElement> p_210363_, StructureTemplatePool.Projection p_210364_) {
      super(p_210364_);
      if (p_210363_.isEmpty()) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = p_210363_;
         this.setProjectionOnEachElement(p_210364_);
      }
   }

   public Vec3i getSize(StructureTemplateManager p_227283_, Rotation p_227284_) {
      int $$2 = 0;
      int $$3 = 0;
      int $$4 = 0;

      for(StructurePoolElement $$5 : this.elements) {
         Vec3i $$6 = $$5.getSize(p_227283_, p_227284_);
         $$2 = Math.max($$2, $$6.getX());
         $$3 = Math.max($$3, $$6.getY());
         $$4 = Math.max($$4, $$6.getZ());
      }

      return new Vec3i($$2, $$3, $$4);
   }

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager p_227290_, BlockPos p_227291_, Rotation p_227292_, RandomSource p_227293_) {
      return ((StructurePoolElement)this.elements.get(0)).getShuffledJigsawBlocks(p_227290_, p_227291_, p_227292_, p_227293_);
   }

   public BoundingBox getBoundingBox(StructureTemplateManager p_227286_, BlockPos p_227287_, Rotation p_227288_) {
      Stream<BoundingBox> $$3 = this.elements.stream().filter((p_210371_) -> p_210371_ != EmptyPoolElement.INSTANCE).map((p_227298_) -> p_227298_.getBoundingBox(p_227286_, p_227287_, p_227288_));
      Objects.requireNonNull($$3);
      return (BoundingBox)BoundingBox.encapsulatingBoxes($$3::iterator).orElseThrow(() -> new IllegalStateException("Unable to calculate boundingbox for ListPoolElement"));
   }

   public boolean place(StructureTemplateManager p_227272_, WorldGenLevel p_227273_, StructureManager p_227274_, ChunkGenerator p_227275_, BlockPos p_227276_, BlockPos p_227277_, Rotation p_227278_, BoundingBox p_227279_, RandomSource p_227280_, boolean p_227281_) {
      for(StructurePoolElement $$10 : this.elements) {
         if (!$$10.place(p_227272_, p_227273_, p_227274_, p_227275_, p_227276_, p_227277_, p_227278_, p_227279_, p_227280_, p_227281_)) {
            return false;
         }
      }

      return true;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.LIST;
   }

   public StructurePoolElement setProjection(StructureTemplatePool.Projection p_210373_) {
      super.setProjection(p_210373_);
      this.setProjectionOnEachElement(p_210373_);
      return this;
   }

   public String toString() {
      Stream var10000 = this.elements.stream().map(Object::toString);
      return "List[" + (String)var10000.collect(Collectors.joining(", ")) + "]";
   }

   private void setProjectionOnEachElement(StructureTemplatePool.Projection p_210407_) {
      this.elements.forEach((p_210376_) -> p_210376_.setProjection(p_210407_));
   }
}
