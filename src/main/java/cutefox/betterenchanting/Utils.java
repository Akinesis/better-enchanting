package cutefox.betterenchanting;

import net.minecraft.util.Identifier;

public class Utils {

    public static Identifier id(String path) {
        return Identifier.of("better-enchanting", path);
    }

    public static Identifier id() {
        return Identifier.of("better-enchanting");
    }
}