package net.haesleinhuepf.clijx.plugins;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.utilities.AbstractCLIJxPlugin;
import org.scijava.plugin.Plugin;

@Deprecated
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_subtractBackground3D")
public class SubtractBackground3D extends AbstractCLIJxPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number sigmaX, Number sigmaY, Number sigmaZ";
    }

    @Override
    public boolean executeCL() {
        Object[] args = openCLBufferArgs();
        boolean result = subtractBackground(getCLIJx(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asFloat(args[2]), asFloat(args[3]), asFloat(args[4]));
        releaseBuffers(args);
        return result;
    }

    public static boolean subtractBackground3D(CLIJx clijx, ClearCLImageInterface input, ClearCLImageInterface output, Float sigmaX, Float sigmaY, Float sigmaZ) {
        return subtractBackground(clijx, input, output, sigmaX, sigmaY, sigmaZ);
    }

    public static boolean subtractBackground(CLIJx clijx, ClearCLImageInterface input, ClearCLImageInterface output, Float sigmaX, Float sigmaY, Float sigmaZ) {

        ClearCLBuffer background = clijx.create(input.getDimensions(), input.getNativeType());

        clijx.blur(input, background, sigmaX, sigmaY, sigmaZ);

        clijx.subtractImages(input, background, output);

        clijx.release(background);
        return true;
    }

    @Override
    public String getDescription() {
        return "Applies Gaussian blur to the input image and subtracts the result from the original image.\n\n" +
                "Deprecated: Use topHat() or differenceOfGaussian() instead.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "3D";
    }
}
