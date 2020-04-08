package me.shedaniel.rareice.forge.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import cpw.mods.modlauncher.api.*;
import net.minecraftforge.coremod.api.ASMAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class RareIceTransformationService implements ITransformationService {
    @Nonnull
    @Override
    public String name() {
        return "RareIceTransformationService";
    }
    
    @Override
    public void initialize(IEnvironment environment) {}
    
    @Override
    public void beginScanning(IEnvironment environment) {}
    
    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {}
    
    @SuppressWarnings("rawtypes")
    @Nonnull
    @Override
    public List<ITransformer> transformers() {
        return ImmutableList.of(
                new BreakableBlockTransformer()
        );
    }
    
    private static class BreakableBlockTransformer implements ITransformer<ClassNode> {
        private static final Logger LOGGER = LogManager.getLogger();
        
        @Nonnull
        @Override
        public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
            LOGGER.debug("Transforming " + input.name);
            String isSideInvisible = ASMAPI.mapMethod("func_200122_a");
            for (MethodNode method : input.methods) {
                if (method.name.equals(isSideInvisible)) {
                    for (AbstractInsnNode instruction : method.instructions) {
                        if (instruction instanceof LabelNode) {
                            method.instructions.insertBefore(instruction, new LabelNode());
                            method.instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 0));
                            method.instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 0));
                            method.instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/BreakableBlock", "getBlock", "()Lnet/minecraft/block/Block;", false));
                            method.instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 2));
                            method.instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "me/shedaniel/rareice/forge/RareIceClient", "isSideInvisibleForIce", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/BlockState;)Z", false));
                            method.instructions.insertBefore(instruction, new JumpInsnNode(Opcodes.IFEQ, (LabelNode) instruction));
                            method.instructions.insertBefore(instruction, new InsnNode(Opcodes.ICONST_1));
                            method.instructions.insertBefore(instruction, new InsnNode(Opcodes.IRETURN));
                            break;
                        }
                    }
                    break;
                }
            }
            return input;
        }
        
        @Nonnull
        @Override
        public TransformerVoteResult castVote(ITransformerVotingContext context) {
            return TransformerVoteResult.YES;
        }
        
        @Nonnull
        @Override
        public Set<Target> targets() {
            return ImmutableSet.of(
                    Target.targetClass("net.minecraft.block.BreakableBlock")
            );
        }
    }
}
