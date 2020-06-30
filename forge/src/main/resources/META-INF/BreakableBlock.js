var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var FieldNode = Java.type("org.objectweb.asm.tree.FieldNode");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");

function initializeCoreMod() {
    return {
        "smooth-scrolling-everywhere": {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.block.BreakableBlock'
            },
            'transformer': function (classNode) {
                var isSideInvisible = ASMAPI.mapMethod("func_200122_a");
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
                                instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 0));
                                instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/BreakableBlock", "getBlock", "()Lnet/minecraft/block/Block;", false));
                                instructions.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 2));
                                instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "me/shedaniel/rareice/forge/RareIceClient", "isSideInvisibleForIce", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/BlockState;)Z", false));
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