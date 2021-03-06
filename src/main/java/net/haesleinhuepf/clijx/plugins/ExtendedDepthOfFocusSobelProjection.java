package net.haesleinhuepf.clijx.plugins;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clij.utilities.CLIJUtilities.assertDifferent;

/**
 * Author: @haesleinhuepf
 * July 2020
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_extendedDepthOfFocusSobelProjection")
public class ExtendedDepthOfFocusSobelProjection extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput {
    @Override
    public String getInputType() {
        return "Image";
    }

    @Override
    public String getOutputType() {
        return "Image";
    }


    @Override
    public boolean executeCL() {
        return extendedDepthOfFocusVarianceProjection(getCLIJ2() ,(ClearCLBuffer)( args[0]), (ClearCLBuffer)(args[1]), asFloat(args[2]));
    }

    public static boolean extendedDepthOfFocusVarianceProjection(CLIJ2 clij2, ClearCLImageInterface src, ClearCLImageInterface dst, Float sigma) {
        assertDifferent(src, dst);

        ClearCLBuffer sobel = clij2.create(src.getDimensions(), NativeTypeEnum.Float);
        SobelSliceBySlice.sobelSliceBySlice(clij2, src, sobel);

        ClearCLBuffer temp = clij2.create(src.getDimensions(), NativeTypeEnum.Float);
        clij2.gaussianBlur3D(sobel, temp, sigma, sigma, 0);

        ClearCLBuffer altitude = clij2.create(new long[]{src.getWidth(), src.getHeight()}, NativeTypeEnum.UnsignedShort);

        ZPositionOfMaximumZProjection.zPositionOfMaximumZProjection(clij2, temp, altitude);
        ZPositionProjection.zPositionProjection(clij2, src, altitude, dst);

        sobel.close();
        temp.close();
        altitude.close();

        return true;
    }

    @Override
    public String getParameterHelpText() {
        return "Image source, ByRef Image destination, Number sigma";
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input)
    {
        return getCLIJ2().create(new long[]{input.getWidth(), input.getHeight()}, input.getNativeType());
    }

    @Override
    public String getDescription() {
        return "Extended depth of focus projection maximizing intensity in the local sobel image.\n\n" +
                "The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "3D -> 2D";
    }

    @Override
    public Object[] getDefaultValues() {
        return new Object[]{null, null, 10};
    }

    @Override
    public String getCategories() {
        return "Projection";
    }
}
