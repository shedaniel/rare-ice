var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var FieldNode = Java.type("org.objectweb.asm.tree.FieldNode");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var TypeInsnNode = Java.type("org.objectweb.asm.tree.TypeInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");

function initializeCoreMod() {
    return {
        "smooth-scrolling-everywhere": {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.level.block.HalfTransparentBlock'
            },
            'transformer': function (classNode) {
                var isSideInvisible = ASMAPI.mapMethod("m_6104_");
                for (i in classNode.methods) {
                    var method = classNode.methods[i];
                    if (method.name === isSideInvisible) {
                        var instructions = method.instructions;
                        var insnArray = instructions.toArray();
                        for (j in insnArray) {
                            var instruction = insnArray[j];
                            if (instruction instanceof LabelNode) {
                                instructions.insertBefore(instruction, new LabelNode());
                                instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 0));
                                instructions.insertBefore(instruction, new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/world/level/block/Block"));
                                instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 2));
                                instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "me/shedaniel/rareice/forge/RareIceClient", "isSideInvisibleForIce", "(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/block/state/BlockState;)Z", false));
                                instructions.insertBefore(instruction, new JumpInsnNode(Opcodes.IFEQ, instruction));
                                instructions.insertBefore(instruction, new InsnNode(Opcodes.ICONST_1));
                                instructions.insertBefore(instruction, new InsnNode(Opcodes.IRETURN));
                                break;
                            }
                        }
                        break;
                    }
                }
                return classNode;
            }
        }
    }
}