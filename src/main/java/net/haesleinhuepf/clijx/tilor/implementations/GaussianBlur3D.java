package net.haesleinhuepf.clijx.tilor.implementations;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJxt_gaussianBlur3D")
public class GaussianBlur3D extends AbstractTileWiseProcessableCLIJ2Plugin {

    public GaussianBlur3D() {
        master = new net.haesleinhuepf.clijx.clij2wrappers.GaussianBlur3D();
    }
}