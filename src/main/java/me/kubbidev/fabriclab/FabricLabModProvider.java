package me.kubbidev.fabriclab;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public final class FabricLabModProvider {

    private static FabricLabMod instance = null;

    public static FabricLabMod getInstance() {
        FabricLabMod instance = FabricLabModProvider.instance;
        if (instance == null) {
            throw new NotLoadedException();
        }
        return instance;
    }

    @ApiStatus.Internal
    static void register(FabricLabMod instance) {
        FabricLabModProvider.instance = instance;
    }

    @ApiStatus.Internal
    static void unregister() {
        FabricLabModProvider.instance = null;
    }

    @ApiStatus.Internal
    private FabricLabModProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }


    private static final class NotLoadedException extends IllegalStateException {

        private static final String MESSAGE = """
            The FabricLab API isn't loaded yet!
            This could be because:
              a) the FabricLab mod is not installed or it failed to enable
              b) the mod in the stacktrace does not declare a dependency on FabricLab
              c) the mod in the stacktrace is retrieving the API before the mod 'initialize' phase
                 (call the #get method in onInitialize, not the constructor!)
            """;

        NotLoadedException() {
            super(MESSAGE);
        }
    }

}
