package net.haesleinhuepf.clijx.utilities;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.weka.CLIJxWeka2;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLKernel;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import ij.measure.ResultsTable;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import java.util.HashMap;
import ij.ImagePlus;
import java.util.List;
import java.util.ArrayList;
import net.haesleinhuepf.clij.kernels.Kernels;
import net.haesleinhuepf.clijx.plugins.CrossCorrelation;
import net.haesleinhuepf.clijx.plugins.Extrema;
import net.haesleinhuepf.clijx.plugins.LocalExtremaBox;
import net.haesleinhuepf.clijx.plugins.LocalID;
import net.haesleinhuepf.clijx.plugins.Presign;
import net.haesleinhuepf.clijx.plugins.StackToTiles;
import net.haesleinhuepf.clijx.plugins.SubtractBackground2D;
import net.haesleinhuepf.clijx.plugins.SubtractBackground3D;
import net.haesleinhuepf.clijx.piv.FastParticleImageVelocimetry;
import net.haesleinhuepf.clijx.piv.ParticleImageVelocimetry;
import net.haesleinhuepf.clijx.piv.ParticleImageVelocimetryTimelapse;
import net.haesleinhuepf.clijx.registration.DeformableRegistration2D;
import net.haesleinhuepf.clijx.registration.TranslationRegistration;
import net.haesleinhuepf.clijx.registration.TranslationTimelapseRegistration;
import net.haesleinhuepf.clijx.io.ReadImageFromDisc;
import net.haesleinhuepf.clijx.io.ReadRawImageFromDisc;
import net.haesleinhuepf.clijx.io.PreloadFromDisc;
import net.haesleinhuepf.clijx.plugins.GaussJordan;
import net.haesleinhuepf.clijx.plugins.StopWatch;
import net.haesleinhuepf.clijx.plugins.DrawTwoValueLine;
import net.haesleinhuepf.clijx.plugins.ConnectedComponentsLabelingInplace;
import net.haesleinhuepf.clijx.plugins.AutomaticThresholdInplace;
import net.haesleinhuepf.clijx.plugins.DifferenceOfGaussianInplace3D;
import net.haesleinhuepf.clijx.plugins.AbsoluteInplace;
import net.haesleinhuepf.clijx.plugins.ShowRGB;
import net.haesleinhuepf.clijx.plugins.ShowGrey;
import net.haesleinhuepf.clijx.gui.OrganiseWindows;
import net.haesleinhuepf.clijx.plugins.TopHatOctagon;
import net.haesleinhuepf.clijx.plugins.ShowGlasbeyOnGrey;
import net.haesleinhuepf.clijx.plugins.BlurSliceBySlice;
import net.haesleinhuepf.clijx.plugins.splitstack.AbstractSplitStack;
import net.haesleinhuepf.clijx.plugins.TopHatOctagonSliceBySlice;
import net.haesleinhuepf.clijx.io.WriteVTKLineListToDisc;
import net.haesleinhuepf.clijx.io.WriteXYZPointListToDisc;
import net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusion;
import net.haesleinhuepf.clijx.plugins.Skeletonize;
import net.haesleinhuepf.clijx.plugins.PushTile;
import net.haesleinhuepf.clijx.plugins.PullTile;
import net.haesleinhuepf.clijx.weka.autocontext.ApplyAutoContextWekaModel;
import net.haesleinhuepf.clijx.weka.autocontext.TrainAutoContextWekaModel;
import net.haesleinhuepf.clijx.weka.ApplyWekaModel;
import net.haesleinhuepf.clijx.weka.ApplyWekaToTable;
import net.haesleinhuepf.clijx.weka.GenerateFeatureStack;
import net.haesleinhuepf.clijx.weka.TrainWekaModel;
import net.haesleinhuepf.clijx.weka.TrainWekaFromTable;
import net.haesleinhuepf.clijx.weka.TrainWekaModelWithOptions;
import net.haesleinhuepf.clijx.plugins.StartContinuousWebcamAcquisition;
import net.haesleinhuepf.clijx.plugins.StopContinuousWebcamAcquisition;
import net.haesleinhuepf.clijx.plugins.CaptureWebcamImage;
import net.haesleinhuepf.clijx.plugins.ConvertRGBStackToGraySlice;
import net.haesleinhuepf.clijx.plugins.NonLocalMeans;
import net.haesleinhuepf.clijx.plugins.Bilateral;
import net.haesleinhuepf.clijx.plugins.FindMaxima;
import net.haesleinhuepf.clijx.plugins.MergeTouchingLabels;
import net.haesleinhuepf.clijx.plugins.AverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.CylinderTransform;
import net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima;
import net.haesleinhuepf.clijx.plugins.DrawDistanceMeshBetweenTouchingLabels;
import net.haesleinhuepf.clijx.plugins.DrawMeshBetweenTouchingLabels;
import net.haesleinhuepf.clijx.plugins.ExcludeLabelsOutsideSizeRange;
import net.haesleinhuepf.clijx.plugins.ExtendLabelsWithMaximumRadius;
import net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima;
import net.haesleinhuepf.clijx.plugins.MakeIsotropic;
import net.haesleinhuepf.clijx.plugins.TouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.RigidTransform;
import net.haesleinhuepf.clijx.plugins.SphereTransform;
import net.haesleinhuepf.clijx.plugins.SubtractGaussianBackground;
import net.haesleinhuepf.clijx.plugins.ThresholdDoG;
import net.haesleinhuepf.clijx.plugins.DriftCorrectionByCenterOfMassFixation;
import net.haesleinhuepf.clijx.plugins.DriftCorrectionByCentroidFixation;
import net.haesleinhuepf.clijx.plugins.IntensityCorrection;
import net.haesleinhuepf.clijx.plugins.IntensityCorrectionAboveThresholdOtsu;
import net.haesleinhuepf.clijx.plugins.LabelMeanIntensityMap;
import net.haesleinhuepf.clijx.plugins.LabelStandardDeviationIntensityMap;
import net.haesleinhuepf.clijx.plugins.LabelPixelCountMap;
import net.haesleinhuepf.clijx.plugins.ParametricWatershed;
import net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold;
import net.haesleinhuepf.clijx.plugins.SeededWatershed;
import net.haesleinhuepf.clijx.plugins.PushMetaData;
import net.haesleinhuepf.clijx.plugins.PopMetaData;
import net.haesleinhuepf.clijx.plugins.ResetMetaData;
import net.haesleinhuepf.clijx.plugins.AverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.DrawTouchCountMeshBetweenTouchingLabels;
import net.haesleinhuepf.clijx.plugins.LocalMaximumAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalMaximumAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalMaximumTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LocalMeanAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalMeanAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalMeanTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LocalMeanTouchPortionMap;
import net.haesleinhuepf.clijx.plugins.LocalMedianAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalMedianAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalMedianTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LocalMinimumAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalMinimumAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalMinimumTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalStandardDeviationTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LabelMinimumIntensityMap;
import net.haesleinhuepf.clijx.plugins.LabelMaximumIntensityMap;
import net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionRatioMap;
import net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionMap;
import net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox;
import net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond;
import net.haesleinhuepf.clijx.plugins.DivideByGaussianBackground;
import net.haesleinhuepf.clijx.plugins.GenerateGreyValueCooccurrenceMatrixBox;
import net.haesleinhuepf.clijx.plugins.GreyLevelAtttributeFiltering;
import net.haesleinhuepf.clijx.plugins.BinaryFillHolesSliceBySlice;
import net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier;
import net.haesleinhuepf.clijx.weka.WekaLabelClassifier;
import net.haesleinhuepf.clijx.weka.GenerateLabelFeatureImage;
import net.haesleinhuepf.clijx.plugins.LabelSurface;
import net.haesleinhuepf.clijx.plugins.ReduceLabelsToLabelledSpots;
import net.haesleinhuepf.clijx.plugins.LabelMeanExtensionMap;
import net.haesleinhuepf.clijx.plugins.MeanZProjectionBelowThreshold;
import net.haesleinhuepf.clijx.plugins.EuclideanDistanceFromLabelCentroidMap;
import net.haesleinhuepf.clijx.plugins.GammaCorrection;
import net.haesleinhuepf.clijx.plugins.ZPositionOfMaximumZProjection;
import net.haesleinhuepf.clijx.plugins.ZPositionProjection;
import net.haesleinhuepf.clijx.plugins.ZPositionRangeProjection;
import net.haesleinhuepf.clijx.plugins.VarianceSphere;
import net.haesleinhuepf.clijx.plugins.StandardDeviationSphere;
import net.haesleinhuepf.clijx.plugins.VarianceBox;
import net.haesleinhuepf.clijx.plugins.StandardDeviationBox;
import net.haesleinhuepf.clijx.plugins.Tenengrad;
import net.haesleinhuepf.clijx.plugins.TenengradSliceBySlice;
import net.haesleinhuepf.clijx.plugins.SobelSliceBySlice;
import net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusSobelProjection;
import net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusTenengradProjection;
import net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusVarianceProjection;
import net.haesleinhuepf.clijx.plugins.DrawMeshBetweenNClosestLabels;
import net.haesleinhuepf.clijx.plugins.DrawMeshBetweenProximalLabels;
import net.haesleinhuepf.clijx.plugins.Cosinus;
import net.haesleinhuepf.clijx.plugins.Sinus;
import net.haesleinhuepf.clijx.plugins.GenerateDistanceMatrixAlongAxis;
import net.haesleinhuepf.clijx.plugins.MaximumDistanceOfTouchingNeighbors;
import net.haesleinhuepf.clijx.plugins.MaximumNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.MinimumNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.GenerateAngleMatrix;
import net.haesleinhuepf.clijx.plugins.NeighborDistanceRangeRatioMap;
import net.haesleinhuepf.clijx.plugins.VoronoiOtsuLabeling;
import net.haesleinhuepf.clijx.plugins.VisualizeOutlinesOnOriginal;
import net.haesleinhuepf.clijx.plugins.FlagLabelsOnEdges;
import net.haesleinhuepf.clijx.plugins.MaskedVoronoiLabeling;
import net.haesleinhuepf.clijx.plugins.PullToResultsTableColumn;
import net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer;
import net.haesleinhuepf.clijx.plugins.ModeOfTouchingNeighbors;
import net.haesleinhuepf.clijx.plugins.GenerateProximalNeighborsMatrix;
import net.haesleinhuepf.clijx.plugins.ReadIntensitiesFromMap;
import net.haesleinhuepf.clijx.plugins.MaximumOfTouchingNeighborsMap;
import net.haesleinhuepf.clijx.plugins.MinimumOfTouchingNeighborsMap;
import net.haesleinhuepf.clijx.plugins.MeanOfTouchingNeighborsMap;
import net.haesleinhuepf.clijx.plugins.ModeOfTouchingNeighborsMap;
import net.haesleinhuepf.clijx.plugins.StandardDeviationOfTouchingNeighborsMap;
import net.haesleinhuepf.clijx.plugins.PointIndexListToTouchMatrix;
import net.haesleinhuepf.clijx.plugins.GenerateNNearestNeighborsMatrix;
import net.haesleinhuepf.clijx.plugins.MaximumOfNNearestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.MinimumOfNNearestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.MeanOfNNearestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.ModeOfNNearestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.StandardDeviationOfNNearestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.MaximumOfProximalNeighborsMap;
import net.haesleinhuepf.clijx.plugins.MinimumOfProximalNeighborsMap;
import net.haesleinhuepf.clijx.plugins.MeanOfProximalNeighborsMap;
import net.haesleinhuepf.clijx.plugins.ModeOfProximalNeighborsMap;
import net.haesleinhuepf.clijx.plugins.StandardDeviationOfProximalNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LabelOverlapCountMap;
import net.haesleinhuepf.clijx.plugins.LabelProximalNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.ReduceLabelsToLabelEdges;
import net.haesleinhuepf.clijx.plugins.OutOfIntensityRange;
import net.haesleinhuepf.clijx.plugins.ShrinkLabels;
import net.haesleinhuepf.clijx.plugins.Similar;
import net.haesleinhuepf.clijx.plugins.Different;
import net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier;
import net.haesleinhuepf.clijx.plugins.LabelMeanOfLaplacianMap;
import net.haesleinhuepf.clijx.plugins.MedianZProjectionMasked;
import net.haesleinhuepf.clijx.plugins.MedianTouchPortionMap;
import net.haesleinhuepf.clijx.plugins.NeighborCountWithTouchPortionAboveThresholdMap;
import net.haesleinhuepf.clijx.plugins.DivideScalarByImage;
import net.haesleinhuepf.clijx.plugins.ReadValuesFromMap;
import net.haesleinhuepf.clijx.plugins.ReadValuesFromPositions;
import net.haesleinhuepf.clijx.plugins.ZPositionOfMinimumZProjection;
import net.haesleinhuepf.clijx.plugins.LocalThresholdPhansalkar;
import net.haesleinhuepf.clijx.plugins.LocalThresholdBernsen;
import net.haesleinhuepf.clijx.plugins.LocalThresholdContrast;
import net.haesleinhuepf.clijx.plugins.LocalThresholdMean;
import net.haesleinhuepf.clijx.plugins.LocalThresholdMedian;
import net.haesleinhuepf.clijx.plugins.LocalThresholdMidGrey;
import net.haesleinhuepf.clijx.plugins.LocalThresholdNiblack;
import net.haesleinhuepf.clijx.plugins.LocalThresholdSauvola;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
public abstract interface CLIJxOps {
   CLIJ getCLIJ();
   CLIJ2 getCLIJ2();
   CLIJx getCLIJx();
   boolean doTimeTracing();
   void recordMethodStart(String method);
   void recordMethodEnd(String method);
   

    // net.haesleinhuepf.clij.kernels.Kernels
    //----------------------------------------------------
    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * vector_x : Image
     *     Pixels in this image describe the distance in X direction pixels should be shifted during warping.
     * vector_y : Image
     *     Pixels in this image describe the distance in Y direction pixels should be shifted during warping.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    default boolean applyVectorfield(ClearCLBuffer source, ClearCLBuffer vector_x, ClearCLBuffer vector_y, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.applyVectorfield(getCLIJ(), source, vector_x, vector_y, destination);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * vector_x : Image
     *     Pixels in this image describe the distance in X direction pixels should be shifted during warping.
     * vector_y : Image
     *     Pixels in this image describe the distance in Y direction pixels should be shifted during warping.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    default boolean applyVectorfield(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, ClearCLBuffer arg5) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.applyVectorfield(getCLIJ(), arg1, arg2, arg3, arg4, arg5);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * vector_x : Image
     *     Pixels in this image describe the distance in X direction pixels should be shifted during warping.
     * vector_y : Image
     *     Pixels in this image describe the distance in Y direction pixels should be shifted during warping.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    default boolean applyVectorfield(ClearCLImage source, ClearCLImage vector_x, ClearCLImage vector_y, ClearCLImage destination) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.applyVectorfield(getCLIJ(), source, vector_x, vector_y, destination);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * vector_x : Image
     *     Pixels in this image describe the distance in X direction pixels should be shifted during warping.
     * vector_y : Image
     *     Pixels in this image describe the distance in Y direction pixels should be shifted during warping.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    default boolean applyVectorfield(ClearCLImage arg1, ClearCLImage arg2, ClearCLImage arg3, ClearCLImage arg4, ClearCLImage arg5) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.applyVectorfield(getCLIJ(), arg1, arg2, arg3, arg4, arg5);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean convertToImageJBinary(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.convertToImageJBinary(getCLIJ(), arg1, arg2);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean convertToImageJBinary(ClearCLImage arg1, ClearCLImage arg2) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.convertToImageJBinary(getCLIJ(), arg1, arg2);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean detectOptima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.detectOptima(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), arg4);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean detectOptima(ClearCLImage arg1, ClearCLImage arg2, double arg3, boolean arg4) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.detectOptima(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), arg4);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean detectOptimaSliceBySlice(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.detectOptimaSliceBySlice(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), arg4);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean detectOptimaSliceBySlice(ClearCLImage arg1, ClearCLImage arg2, double arg3, boolean arg4) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.detectOptimaSliceBySlice(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), arg4);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * sigma1_x : float
     *     Sigma of the first Gaussian filter in x
     * sigma1_y : float
     *     Sigma of the first Gaussian filter in y
     * sigma2_x : float
     *     Sigma of the second Gaussian filter in x
     * sigma2_y : float
     *     Sigma of the second Gaussian filter in y
     */
    default boolean differenceOfGaussian(ClearCLImage arg1, ClearCLImage arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.differenceOfGaussian(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean differenceOfGaussianSliceBySlice(ClearCLImage arg1, ClearCLImage arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.differenceOfGaussianSliceBySlice(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean maximumXYZProjection(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.maximumXYZProjection(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean maximumXYZProjection(ClearCLImage arg1, ClearCLImage arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.maximumXYZProjection(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean multiplySliceBySliceWithScalars(ClearCLBuffer arg1, ClearCLBuffer arg2, float[] arg3) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.multiplySliceBySliceWithScalars(getCLIJ(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default boolean multiplySliceBySliceWithScalars(ClearCLImage arg1, ClearCLImage arg2, float[] arg3) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        boolean result = Kernels.multiplySliceBySliceWithScalars(getCLIJ(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default double[] sumPixelsSliceBySlice(ClearCLBuffer arg1) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        double[] result = Kernels.sumPixelsSliceBySlice(getCLIJ(), arg1);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }

    /**
     * 
     */
    default double[] sumPixelsSliceBySlice(ClearCLImage arg1) {
        if (doTimeTracing()) {recordMethodStart("Kernels");}
        double[] result = Kernels.sumPixelsSliceBySlice(getCLIJ(), arg1);
        if (doTimeTracing()) {recordMethodEnd("Kernels");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.CrossCorrelation
    //----------------------------------------------------
    /**
     * Performs cross correlation analysis between two images. 
     * 
     * The second image is shifted by deltaPos in the given dimension. The cross correlation coefficient is calculated for each pixel in a range around the given pixel with given radius in the given dimension. Together with the original images it is recommended to hand over mean filtered images using the same radius.  
     */
    default boolean crossCorrelation(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, ClearCLBuffer arg5, double arg6, double arg7, double arg8) {
        if (doTimeTracing()) {recordMethodStart("CrossCorrelation");}
        boolean result = CrossCorrelation.crossCorrelation(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        if (doTimeTracing()) {recordMethodEnd("CrossCorrelation");}
        return result;
    }

    /**
     * Performs cross correlation analysis between two images. 
     * 
     * The second image is shifted by deltaPos in the given dimension. The cross correlation coefficient is calculated for each pixel in a range around the given pixel with given radius in the given dimension. Together with the original images it is recommended to hand over mean filtered images using the same radius.  
     */
    default boolean crossCorrelation(ClearCLImage arg1, ClearCLImage arg2, ClearCLImage arg3, ClearCLImage arg4, ClearCLImage arg5, double arg6, double arg7, double arg8) {
        if (doTimeTracing()) {recordMethodStart("CrossCorrelation");}
        boolean result = CrossCorrelation.crossCorrelation(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        if (doTimeTracing()) {recordMethodEnd("CrossCorrelation");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Extrema
    //----------------------------------------------------
    /**
     * Returns an image with pixel values most distant from 0: 
     * 
     * f(x, y) = x if abs(x) > abs(y), y else.
     */
    default boolean extrema(ClearCLBuffer input1, ClearCLBuffer input2, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("Extrema");}
        boolean result = Extrema.extrema(getCLIJ(), input1, input2, destination);
        if (doTimeTracing()) {recordMethodEnd("Extrema");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalExtremaBox
    //----------------------------------------------------
    /**
     * Applies a local minimum and maximum filter. 
     * 
     * Afterwards, the value is returned which is more far from zero.
     */
    default boolean localExtremaBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("LocalExtremaBox");}
        boolean result = LocalExtremaBox.localExtremaBox(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("LocalExtremaBox");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalID
    //----------------------------------------------------
    /**
     * local id
     */
    default boolean localID(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalID");}
        boolean result = LocalID.localID(getCLIJ(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalID");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Presign
    //----------------------------------------------------
    /**
     * Determines the extrema of pixel values: 
     * 
     * f(x) = x / abs(x).
     */
    default boolean presign(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("Presign");}
        boolean result = Presign.presign(getCLIJ(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("Presign");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StackToTiles
    //----------------------------------------------------
    /**
     * Stack to tiles.
     */
    default boolean stackToTiles(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("StackToTiles");}
        boolean result = StackToTiles.stackToTiles(getCLIJx(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("StackToTiles");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractBackground2D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    default boolean subtractBackground(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("SubtractBackground2D");}
        boolean result = SubtractBackground2D.subtractBackground(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("SubtractBackground2D");}
        return result;
    }

    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    default boolean subtractBackground2D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("SubtractBackground2D");}
        boolean result = SubtractBackground2D.subtractBackground2D(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("SubtractBackground2D");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractBackground3D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    default boolean subtractBackground(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("SubtractBackground3D");}
        boolean result = SubtractBackground3D.subtractBackground(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("SubtractBackground3D");}
        return result;
    }

    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    default boolean subtractBackground3D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("SubtractBackground3D");}
        boolean result = SubtractBackground3D.subtractBackground3D(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("SubtractBackground3D");}
        return result;
    }


    // net.haesleinhuepf.clijx.piv.FastParticleImageVelocimetry
    //----------------------------------------------------
    /**
     * 
     */
    default boolean particleImageVelocimetry2D(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("FastParticleImageVelocimetry");}
        boolean result = FastParticleImageVelocimetry.particleImageVelocimetry2D(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("FastParticleImageVelocimetry");}
        return result;
    }


    // net.haesleinhuepf.clijx.piv.ParticleImageVelocimetry
    //----------------------------------------------------
    /**
     * For every pixel in source image 1, determine the pixel with the most similar intensity in 
     *  the local neighborhood with a given radius in source image 2. Write the distance in 
     * X, Y and Z in the three corresponding destination images.
     */
    default boolean particleImageVelocimetry(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, ClearCLBuffer arg5, double arg6, double arg7, double arg8) {
        if (doTimeTracing()) {recordMethodStart("ParticleImageVelocimetry");}
        boolean result = ParticleImageVelocimetry.particleImageVelocimetry(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        if (doTimeTracing()) {recordMethodEnd("ParticleImageVelocimetry");}
        return result;
    }


    // net.haesleinhuepf.clijx.piv.ParticleImageVelocimetryTimelapse
    //----------------------------------------------------
    /**
     * Run particle image velocimetry on a 2D+t timelapse.
     */
    default boolean particleImageVelocimetryTimelapse(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, double arg5, double arg6, double arg7, boolean arg8) {
        if (doTimeTracing()) {recordMethodStart("ParticleImageVelocimetryTimelapse");}
        boolean result = ParticleImageVelocimetryTimelapse.particleImageVelocimetryTimelapse(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), arg8);
        if (doTimeTracing()) {recordMethodEnd("ParticleImageVelocimetryTimelapse");}
        return result;
    }


    // net.haesleinhuepf.clijx.registration.DeformableRegistration2D
    //----------------------------------------------------
    /**
     * Applies particle image velocimetry to two images and registers them afterwards by warping input image 2 with a smoothed vector field.
     */
    default boolean deformableRegistration2D(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("DeformableRegistration2D");}
        boolean result = DeformableRegistration2D.deformableRegistration2D(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("DeformableRegistration2D");}
        return result;
    }


    // net.haesleinhuepf.clijx.registration.TranslationRegistration
    //----------------------------------------------------
    /**
     * Measures center of mass of thresholded objects in the two input images and translates the second image so that it better fits to the first image.
     */
    default boolean translationRegistration(ClearCLBuffer arg1, ClearCLBuffer arg2, double[] arg3) {
        if (doTimeTracing()) {recordMethodStart("TranslationRegistration");}
        boolean result = TranslationRegistration.translationRegistration(getCLIJ(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("TranslationRegistration");}
        return result;
    }

    /**
     * Measures center of mass of thresholded objects in the two input images and translates the second image so that it better fits to the first image.
     */
    default boolean translationRegistration(ClearCLBuffer input1, ClearCLBuffer input2, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("TranslationRegistration");}
        boolean result = TranslationRegistration.translationRegistration(getCLIJ(), input1, input2, destination);
        if (doTimeTracing()) {recordMethodEnd("TranslationRegistration");}
        return result;
    }


    // net.haesleinhuepf.clijx.registration.TranslationTimelapseRegistration
    //----------------------------------------------------
    /**
     * Applies 2D translation registration to every pair of t, t+1 slices of a 2D+t image stack.
     */
    default boolean translationTimelapseRegistration(ClearCLBuffer input, ClearCLBuffer output) {
        if (doTimeTracing()) {recordMethodStart("TranslationTimelapseRegistration");}
        boolean result = TranslationTimelapseRegistration.translationTimelapseRegistration(getCLIJ(), input, output);
        if (doTimeTracing()) {recordMethodEnd("TranslationTimelapseRegistration");}
        return result;
    }


    // net.haesleinhuepf.clijx.io.ReadImageFromDisc
    //----------------------------------------------------
    /**
     * Read an image from disc.
     */
    default ClearCLBuffer readImageFromDisc(String arg1) {
        if (doTimeTracing()) {recordMethodStart("ReadImageFromDisc");}
        ClearCLBuffer result = ReadImageFromDisc.readImageFromDisc(getCLIJ(), arg1);
        if (doTimeTracing()) {recordMethodEnd("ReadImageFromDisc");}
        return result;
    }


    // net.haesleinhuepf.clijx.io.ReadRawImageFromDisc
    //----------------------------------------------------
    /**
     * Reads a raw file from disc and pushes it immediately to the GPU.
     */
    default boolean readRawImageFromDisc(ClearCLBuffer arg1, String arg2) {
        if (doTimeTracing()) {recordMethodStart("ReadRawImageFromDisc");}
        boolean result = ReadRawImageFromDisc.readRawImageFromDisc(getCLIJ(), arg1, arg2);
        if (doTimeTracing()) {recordMethodEnd("ReadRawImageFromDisc");}
        return result;
    }

    /**
     * Reads a raw file from disc and pushes it immediately to the GPU.
     */
    default ClearCLBuffer readRawImageFromDisc(String arg1, double arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("ReadRawImageFromDisc");}
        ClearCLBuffer result = ReadRawImageFromDisc.readRawImageFromDisc(getCLIJ(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("ReadRawImageFromDisc");}
        return result;
    }


    // net.haesleinhuepf.clijx.io.PreloadFromDisc
    //----------------------------------------------------
    /**
     * This plugin takes two image filenames and loads them into RAM. The first image is returned immediately, the second image is loaded in the background and  will be returned when the plugin is called again.
     * 
     *  It is assumed that all images have the same size. If this is not the case, call release(image) before  getting the second image.
     */
    default ClearCLBuffer preloadFromDisc(ClearCLBuffer destination, String filename, String nextFilename, String loaderId) {
        if (doTimeTracing()) {recordMethodStart("PreloadFromDisc");}
        ClearCLBuffer result = PreloadFromDisc.preloadFromDisc(getCLIJ(), destination, filename, nextFilename, loaderId);
        if (doTimeTracing()) {recordMethodEnd("PreloadFromDisc");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GaussJordan
    //----------------------------------------------------
    /**
     * Gauss Jordan elimination algorithm for solving linear equation systems. 
     * 
     * Ent the equation coefficients as an n*n sized image A and an n*1 sized image B:
     * <pre>a(1,1)*x + a(2,1)*y + a(3,1)+z = b(1)
     * a(2,1)*x + a(2,2)*y + a(3,2)+z = b(2)
     * a(3,1)*x + a(3,2)*y + a(3,3)+z = b(3)
     * </pre>
     * The results will then be given in an n*1 image with values [x, y, z].
     * 
     * Adapted from: 
     * https://github.com/qbunia/rodinia/blob/master/opencl/gaussian/gaussianElim_kernels.cl
     * L.G. Szafaryn, K. Skadron and J. Saucerman. "Experiences Accelerating MATLAB Systems
     * //Biology Applications." in Workshop on Biomedicine in Computing (BiC) at the International
     * //Symposium on Computer Architecture (ISCA), June 2009.
     */
    default boolean gaussJordan(ClearCLBuffer A_matrix, ClearCLBuffer B_result_vector, ClearCLBuffer solution_destination) {
        if (doTimeTracing()) {recordMethodStart("GaussJordan");}
        boolean result = GaussJordan.gaussJordan(getCLIJ(), A_matrix, B_result_vector, solution_destination);
        if (doTimeTracing()) {recordMethodEnd("GaussJordan");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StopWatch
    //----------------------------------------------------
    /**
     * Measures time and outputs delay to last call.
     */
    default boolean stopWatch(String text) {
        if (doTimeTracing()) {recordMethodStart("StopWatch");}
        boolean result = StopWatch.stopWatch(getCLIJ(), text);
        if (doTimeTracing()) {recordMethodEnd("StopWatch");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawTwoValueLine
    //----------------------------------------------------
    /**
     * Draws a line between two points with a given thickness. 
     * 
     * Pixels close to point 1 are set to value1. Pixels closer to point 2 are set to value2 All pixels other than on the line are untouched. Consider using clij.set(buffer, 0); in advance.
     */
    default boolean drawTwoValueLine(ClearCLBuffer arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        if (doTimeTracing()) {recordMethodStart("DrawTwoValueLine");}
        boolean result = DrawTwoValueLine.drawTwoValueLine(getCLIJx(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue(), new Double (arg9).floatValue(), new Double (arg10).floatValue());
        if (doTimeTracing()) {recordMethodEnd("DrawTwoValueLine");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ConnectedComponentsLabelingInplace
    //----------------------------------------------------
    /**
     * Performs connected components analysis to a binary image and generates a label map.
     * 
     * Deprecated: Use connectedComponentsLabelingBox() instead.
     */
    @Deprecated
    default boolean connectedComponentsLabelingInplace(ClearCLBuffer binary_source_labeling_destination) {
        System.out.println("connectedComponentsLabelingInplace is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("ConnectedComponentsLabelingInplace");}
        boolean result = ConnectedComponentsLabelingInplace.connectedComponentsLabelingInplace(getCLIJx(), binary_source_labeling_destination);
        if (doTimeTracing()) {recordMethodEnd("ConnectedComponentsLabelingInplace");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.AutomaticThresholdInplace
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method. Enter one 
     * of these methods in the method text field:
     * [Default, Huang, Intermodes, IsoData, IJ_IsoData, Li, MaxEntropy, Mean, MinError, Minimum, Moments, Otsu, Percentile, RenyiEntropy, Shanbhag, Triangle, Yen]
     * 
     * Deprecated: Use threshold* instead.
     */
    @Deprecated
    default boolean automaticThresholdInplace(ClearCLBuffer input_and_destination, String method) {
        System.out.println("automaticThresholdInplace is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("AutomaticThresholdInplace");}
        boolean result = AutomaticThresholdInplace.automaticThresholdInplace(getCLIJx(), input_and_destination, method);
        if (doTimeTracing()) {recordMethodEnd("AutomaticThresholdInplace");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DifferenceOfGaussianInplace3D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     * 
     * Deprecated: Use differenceOfGaussian3D instead.
     */
    @Deprecated
    default boolean differenceOfGaussianInplace3D(ClearCLBuffer arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        System.out.println("differenceOfGaussianInplace3D is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("DifferenceOfGaussianInplace3D");}
        boolean result = DifferenceOfGaussianInplace3D.differenceOfGaussianInplace3D(getCLIJ(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        if (doTimeTracing()) {recordMethodEnd("DifferenceOfGaussianInplace3D");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.AbsoluteInplace
    //----------------------------------------------------
    /**
     * Computes the absolute value of every individual pixel x in a given image.
     * 
     * <pre>f(x) = |x| </pre>
     * 
     * Deprecated: Use absolute() instead.
     */
    @Deprecated
    default boolean absoluteInplace(ClearCLBuffer source_destination) {
        System.out.println("absoluteInplace is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("AbsoluteInplace");}
        boolean result = AbsoluteInplace.absoluteInplace(getCLIJx(), source_destination);
        if (doTimeTracing()) {recordMethodEnd("AbsoluteInplace");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ShowRGB
    //----------------------------------------------------
    /**
     * Visualises three 2D images as one RGB image
     */
    default boolean showRGB(ClearCLBuffer red, ClearCLBuffer green, ClearCLBuffer blue, String title) {
        if (doTimeTracing()) {recordMethodStart("ShowRGB");}
        boolean result = ShowRGB.showRGB(getCLIJ(), red, green, blue, title);
        if (doTimeTracing()) {recordMethodEnd("ShowRGB");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ShowGrey
    //----------------------------------------------------
    /**
     * Visualises a single 2D image.
     */
    default ImagePlus showGrey(ClearCLBuffer input, String title) {
        if (doTimeTracing()) {recordMethodStart("ShowGrey");}
        ImagePlus result = ShowGrey.showGrey(getCLIJ(), input, title);
        if (doTimeTracing()) {recordMethodEnd("ShowGrey");}
        return result;
    }


    // net.haesleinhuepf.clijx.gui.OrganiseWindows
    //----------------------------------------------------
    /**
     * Organises windows on screen.
     */
    default boolean organiseWindows(double arg1, double arg2, double arg3, double arg4, double arg5, double arg6) {
        if (doTimeTracing()) {recordMethodStart("OrganiseWindows");}
        boolean result = OrganiseWindows.organiseWindows(getCLIJ(), new Double (arg1).intValue(), new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue());
        if (doTimeTracing()) {recordMethodEnd("OrganiseWindows");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.TopHatOctagon
    //----------------------------------------------------
    /**
     * Applies a minimum filter with kernel size 3x3 n times to an image iteratively. 
     * 
     *  Odd iterations are done with box neighborhood, even iterations with a diamond. Thus, with n > 2, the filter shape is an octagon. The given number of iterations - 2 makes the filter result very similar to minimum sphere.
     */
    default boolean topHatOctagon(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("TopHatOctagon");}
        boolean result = TopHatOctagon.topHatOctagon(getCLIJx(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("TopHatOctagon");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ShowGlasbeyOnGrey
    //----------------------------------------------------
    /**
     * Visualises two 2D images as one RGB image. 
     * 
     * The first channel is shown in grey, the second with glasbey LUT.
     */
    default boolean showGlasbeyOnGrey(ClearCLBuffer red, ClearCLBuffer labelling, String title) {
        if (doTimeTracing()) {recordMethodStart("ShowGlasbeyOnGrey");}
        boolean result = ShowGlasbeyOnGrey.showGlasbeyOnGrey(getCLIJ(), red, labelling, title);
        if (doTimeTracing()) {recordMethodEnd("ShowGlasbeyOnGrey");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.BlurSliceBySlice
    //----------------------------------------------------
    /**
     * Computes the Gaussian blurred image of an image given two sigma values in X and Y. Thus, the filterkernel can have non-isotropic shape.
     * 
     * The Gaussian blur is applied slice by slice in 2D.
     * 
     * DEPRECATED: This method is deprecated. Use gaussianBlur3D instead.
     */
    @Deprecated
    default boolean blurSliceBySlice(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        System.out.println("blurSliceBySlice is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("BlurSliceBySlice");}
        boolean result = BlurSliceBySlice.blurSliceBySlice(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("BlurSliceBySlice");}
        return result;
    }

    /**
     * Computes the Gaussian blurred image of an image given two sigma values in X and Y. Thus, the filterkernel can have non-isotropic shape.
     * 
     * The Gaussian blur is applied slice by slice in 2D.
     * 
     * DEPRECATED: This method is deprecated. Use gaussianBlur3D instead.
     */
    @Deprecated
    default boolean blurSliceBySlice(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5, double arg6) {
        System.out.println("blurSliceBySlice is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("BlurSliceBySlice");}
        boolean result = BlurSliceBySlice.blurSliceBySlice(getCLIJx(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        if (doTimeTracing()) {recordMethodEnd("BlurSliceBySlice");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.splitstack.AbstractSplitStack
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.TopHatOctagonSliceBySlice
    //----------------------------------------------------
    /**
     * Applies a minimum filter with kernel size 3x3 n times to an image iteratively. 
     * 
     * Odd iterations are done with box neighborhood, even iterations with a diamond. Thus, with n > 2, the filter shape is an octagon. The given number of iterations - 2 makes the filter result very similar to minimum sphere.
     */
    default boolean topHatOctagonSliceBySlice(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("TopHatOctagonSliceBySlice");}
        boolean result = TopHatOctagonSliceBySlice.topHatOctagonSliceBySlice(getCLIJx(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("TopHatOctagonSliceBySlice");}
        return result;
    }


    // net.haesleinhuepf.clijx.io.WriteVTKLineListToDisc
    //----------------------------------------------------
    /**
     * Takes a point list image representing n points (n*2 for 2D points, n*3 for 3D points) and a corresponding touch matrix , sized (n+1)*(n+1), and exports them in VTK format.
     */
    default boolean writeVTKLineListToDisc(ClearCLBuffer pointlist, ClearCLBuffer touch_matrix, String filename) {
        if (doTimeTracing()) {recordMethodStart("WriteVTKLineListToDisc");}
        boolean result = WriteVTKLineListToDisc.writeVTKLineListToDisc(getCLIJx(), pointlist, touch_matrix, filename);
        if (doTimeTracing()) {recordMethodEnd("WriteVTKLineListToDisc");}
        return result;
    }


    // net.haesleinhuepf.clijx.io.WriteXYZPointListToDisc
    //----------------------------------------------------
    /**
     * Takes a point list image representing n points (n*2 for 2D points, n*3 for 3D points) and exports them in XYZ format.
     */
    default boolean writeXYZPointListToDisc(ClearCLBuffer pointlist, String filename) {
        if (doTimeTracing()) {recordMethodStart("WriteXYZPointListToDisc");}
        boolean result = WriteXYZPointListToDisc.writeXYZPointListToDisc(getCLIJx(), pointlist, filename);
        if (doTimeTracing()) {recordMethodEnd("WriteXYZPointListToDisc");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusion
    //----------------------------------------------------
    /**
     * Fuses #n# image stacks using Tenengrads algorithm.
     */
    default boolean tenengradFusion(ClearCLBuffer arg1, float[] arg2, float arg3, ClearCLBuffer[] arg4) {
        if (doTimeTracing()) {recordMethodStart("TenengradFusion");}
        boolean result = TenengradFusion.tenengradFusion(getCLIJx(), arg1, arg2, arg3, arg4);
        if (doTimeTracing()) {recordMethodEnd("TenengradFusion");}
        return result;
    }

    /**
     * Fuses #n# image stacks using Tenengrads algorithm.
     */
    default boolean tenengradFusion(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        if (doTimeTracing()) {recordMethodStart("TenengradFusion");}
        boolean result = TenengradFusion.tenengradFusion(getCLIJx(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        if (doTimeTracing()) {recordMethodEnd("TenengradFusion");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Skeletonize
    //----------------------------------------------------
    /**
     * Erodes a binary image until just its skeleton is left. 
     * 
     * The result is similar to Skeletonize3D in Fiji.
     * 
     * Deprecated: Use SimpleITK binaryThinning() instead.
     */
    @Deprecated
    default boolean skeletonize(ClearCLBuffer source, ClearCLBuffer destination) {
        System.out.println("skeletonize is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("Skeletonize");}
        boolean result = Skeletonize.skeletonize(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("Skeletonize");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.PushTile
    //----------------------------------------------------
    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    default ClearCLBuffer pushTile(ImagePlus arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        if (doTimeTracing()) {recordMethodStart("PushTile");}
        ClearCLBuffer result = PushTile.pushTile(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue());
        if (doTimeTracing()) {recordMethodEnd("PushTile");}
        return result;
    }

    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    default ClearCLBuffer pushTile(ClearCLBuffer arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        if (doTimeTracing()) {recordMethodStart("PushTile");}
        ClearCLBuffer result = PushTile.pushTile(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue());
        if (doTimeTracing()) {recordMethodEnd("PushTile");}
        return result;
    }

    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    default void pushTile(ImagePlus arg1, String arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        if (doTimeTracing()) {recordMethodStart("PushTile");}
        PushTile.pushTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
        if (doTimeTracing()) {recordMethodEnd("PushTile");}
    }


    // net.haesleinhuepf.clijx.plugins.PullTile
    //----------------------------------------------------
    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    default void pullTile(ImagePlus arg1, String arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        if (doTimeTracing()) {recordMethodStart("PullTile");}
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
        if (doTimeTracing()) {recordMethodEnd("PullTile");}
    }

    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    default void pullTile(ImagePlus arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        if (doTimeTracing()) {recordMethodStart("PullTile");}
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
        if (doTimeTracing()) {recordMethodEnd("PullTile");}
    }

    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    default void pullTile(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        if (doTimeTracing()) {recordMethodStart("PullTile");}
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
        if (doTimeTracing()) {recordMethodEnd("PullTile");}
    }


    // net.haesleinhuepf.clijx.weka.autocontext.ApplyAutoContextWekaModel
    //----------------------------------------------------
    /**
     * 
     */
    default boolean applyAutoContextWekaModelWithOptions(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, String arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("ApplyAutoContextWekaModel");}
        boolean result = ApplyAutoContextWekaModel.applyAutoContextWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("ApplyAutoContextWekaModel");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.autocontext.TrainAutoContextWekaModel
    //----------------------------------------------------
    /**
     * 
     */
    default boolean trainAutoContextWekaModelWithOptions(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, String arg4, double arg5, double arg6, double arg7, double arg8) {
        if (doTimeTracing()) {recordMethodStart("TrainAutoContextWekaModel");}
        boolean result = TrainAutoContextWekaModel.trainAutoContextWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        if (doTimeTracing()) {recordMethodEnd("TrainAutoContextWekaModel");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.ApplyWekaModel
    //----------------------------------------------------
    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and applies a pre-trained a Weka model. Take care that the feature stack has been generated in the sameway as for training the model!
     */
    default boolean applyWekaModel(ClearCLBuffer arg1, ClearCLBuffer arg2, CLIJxWeka2 arg3) {
        if (doTimeTracing()) {recordMethodStart("ApplyWekaModel");}
        boolean result = ApplyWekaModel.applyWekaModel(getCLIJ2(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("ApplyWekaModel");}
        return result;
    }

    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and applies a pre-trained a Weka model. Take care that the feature stack has been generated in the sameway as for training the model!
     */
    default CLIJxWeka2 applyWekaModel(ClearCLBuffer featureStack3D, ClearCLBuffer prediction2D_destination, String loadModelFilename) {
        if (doTimeTracing()) {recordMethodStart("ApplyWekaModel");}
        CLIJxWeka2 result = ApplyWekaModel.applyWekaModel(getCLIJ2(), featureStack3D, prediction2D_destination, loadModelFilename);
        if (doTimeTracing()) {recordMethodEnd("ApplyWekaModel");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.ApplyWekaToTable
    //----------------------------------------------------
    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a Results Table, sorts its columns by name alphabetically and uses it as extracted features (rows correspond to feature vectors) and applies a pre-trained a Weka model. Take care that the table has been generated in the sameway as for training the model!
     */
    default CLIJxWeka2 applyWekaToTable(ResultsTable arg1, String arg2, String arg3) {
        if (doTimeTracing()) {recordMethodStart("ApplyWekaToTable");}
        CLIJxWeka2 result = ApplyWekaToTable.applyWekaToTable(getCLIJ2(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("ApplyWekaToTable");}
        return result;
    }

    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a Results Table, sorts its columns by name alphabetically and uses it as extracted features (rows correspond to feature vectors) and applies a pre-trained a Weka model. Take care that the table has been generated in the sameway as for training the model!
     */
    default CLIJxWeka2 applyWekaToTable(ResultsTable arg1, String arg2, CLIJxWeka2 arg3) {
        if (doTimeTracing()) {recordMethodStart("ApplyWekaToTable");}
        CLIJxWeka2 result = ApplyWekaToTable.applyWekaToTable(getCLIJ2(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("ApplyWekaToTable");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.GenerateFeatureStack
    //----------------------------------------------------
    /**
     * Generates a feature stack for Trainable Weka Segmentation. 
     * 
     * Use this terminology to specifiy which stacks should be generated:
     * * "original" original slice
     * * "GaussianBlur=s" Gaussian blurred image with sigma s
     * * "LaplacianOfGaussian=s" Laplacian of Gaussian blurred image with sigma s
     * * "SobelOfGaussian=s" Sobel filter applied to Gaussian blurred image with sigma s
     * * "minimum=r" local minimum with radius r
     * * "maximum=r" local maximum with radius r
     * * "mean=r" local mean with radius r
     * * "entropy=r" local entropy with radius r
     * * "gradientX" local gradient in X direction
     * * "gradientY" local gradient in Y direction
     * 
     * Use sigma=0 to apply a filter to the original image. Feature definitions are not case sensitive.
     * 
     * Example: "original gaussianBlur=1 gaussianBlur=5 laplacianOfGaussian=1 laplacianOfGaussian=7 entropy=3"
     */
    default boolean generateFeatureStack(ClearCLBuffer input, ClearCLBuffer feature_stack_destination, String feature_definitions) {
        if (doTimeTracing()) {recordMethodStart("GenerateFeatureStack");}
        boolean result = GenerateFeatureStack.generateFeatureStack(getCLIJ2(), input, feature_stack_destination, feature_definitions);
        if (doTimeTracing()) {recordMethodEnd("GenerateFeatureStack");}
        return result;
    }

    /**
     * Generates a feature stack for Trainable Weka Segmentation. 
     * 
     * Use this terminology to specifiy which stacks should be generated:
     * * "original" original slice
     * * "GaussianBlur=s" Gaussian blurred image with sigma s
     * * "LaplacianOfGaussian=s" Laplacian of Gaussian blurred image with sigma s
     * * "SobelOfGaussian=s" Sobel filter applied to Gaussian blurred image with sigma s
     * * "minimum=r" local minimum with radius r
     * * "maximum=r" local maximum with radius r
     * * "mean=r" local mean with radius r
     * * "entropy=r" local entropy with radius r
     * * "gradientX" local gradient in X direction
     * * "gradientY" local gradient in Y direction
     * 
     * Use sigma=0 to apply a filter to the original image. Feature definitions are not case sensitive.
     * 
     * Example: "original gaussianBlur=1 gaussianBlur=5 laplacianOfGaussian=1 laplacianOfGaussian=7 entropy=3"
     */
    default ClearCLBuffer generateFeatureStack(ClearCLBuffer arg1, String arg2) {
        if (doTimeTracing()) {recordMethodStart("GenerateFeatureStack");}
        ClearCLBuffer result = GenerateFeatureStack.generateFeatureStack(getCLIJ2(), arg1, arg2);
        if (doTimeTracing()) {recordMethodEnd("GenerateFeatureStack");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.TrainWekaModel
    //----------------------------------------------------
    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and trains a Weka model. This model will be saved to disc.
     * The given groundTruth image is supposed to be a label map where pixels with value 1 represent class 1, pixels with value 2 represent class 2 and so on. Pixels with value 0 will be ignored for training.
     */
    default CLIJxWeka2 trainWekaModel(ClearCLBuffer featureStack3D, ClearCLBuffer groundTruth2D, String saveModelFilename) {
        if (doTimeTracing()) {recordMethodStart("TrainWekaModel");}
        CLIJxWeka2 result = TrainWekaModel.trainWekaModel(getCLIJ2(), featureStack3D, groundTruth2D, saveModelFilename);
        if (doTimeTracing()) {recordMethodEnd("TrainWekaModel");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.TrainWekaFromTable
    //----------------------------------------------------
    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes the given Results Table, sorts its columns alphabetically as extracted features (rows correspond to feature vectors) and a given column name containing the ground truth to train a Weka model. This model will be saved to disc.
     * The given groundTruth column is supposed to be numeric with values 1 represent class 1,  value 2 represent class 2 and so on. Value 0 will be ignored for training.
     * 
     * Default values for options are:
     * * trees = 200
     * * features = 2
     * * maxDepth = 0
     */
    default CLIJxWeka2 trainWekaFromTable(ResultsTable arg1, String arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("TrainWekaFromTable");}
        CLIJxWeka2 result = TrainWekaFromTable.trainWekaFromTable(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("TrainWekaFromTable");}
        return result;
    }

    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes the given Results Table, sorts its columns alphabetically as extracted features (rows correspond to feature vectors) and a given column name containing the ground truth to train a Weka model. This model will be saved to disc.
     * The given groundTruth column is supposed to be numeric with values 1 represent class 1,  value 2 represent class 2 and so on. Value 0 will be ignored for training.
     * 
     * Default values for options are:
     * * trees = 200
     * * features = 2
     * * maxDepth = 0
     */
    default CLIJxWeka2 trainWekaFromTable(ResultsTable arg1, String arg2, String arg3, double arg4, double arg5, double arg6) {
        if (doTimeTracing()) {recordMethodStart("TrainWekaFromTable");}
        CLIJxWeka2 result = TrainWekaFromTable.trainWekaFromTable(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue());
        if (doTimeTracing()) {recordMethodEnd("TrainWekaFromTable");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.TrainWekaModelWithOptions
    //----------------------------------------------------
    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and trains a Weka model. This model will be saved to disc.
     * The given groundTruth image is supposed to be a label map where pixels with value 1 represent class 1, pixels with value 2 represent class 2 and so on. Pixels with value 0 will be ignored for training.
     * 
     * Default values for options are:
     * * trees = 200
     * * features = 2
     * * maxDepth = 0
     */
    default CLIJxWeka2 trainWekaModelWithOptions(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, double arg4, double arg5, double arg6) {
        if (doTimeTracing()) {recordMethodStart("TrainWekaModelWithOptions");}
        CLIJxWeka2 result = TrainWekaModelWithOptions.trainWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue());
        if (doTimeTracing()) {recordMethodEnd("TrainWekaModelWithOptions");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StartContinuousWebcamAcquisition
    //----------------------------------------------------
    /**
     * Starts acquistion of images from a webcam.
     */
    default boolean startContinuousWebcamAcquisition(double arg1, double arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("StartContinuousWebcamAcquisition");}
        boolean result = StartContinuousWebcamAcquisition.startContinuousWebcamAcquisition(getCLIJx(), new Double (arg1).intValue(), new Double (arg2).intValue(), new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("StartContinuousWebcamAcquisition");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StopContinuousWebcamAcquisition
    //----------------------------------------------------
    /**
     * Stops continous acquistion from a webcam.
     */
    default boolean stopContinuousWebcamAcquisition(double arg1) {
        if (doTimeTracing()) {recordMethodStart("StopContinuousWebcamAcquisition");}
        boolean result = StopContinuousWebcamAcquisition.stopContinuousWebcamAcquisition(getCLIJx(), new Double (arg1).intValue());
        if (doTimeTracing()) {recordMethodEnd("StopContinuousWebcamAcquisition");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.CaptureWebcamImage
    //----------------------------------------------------
    /**
     * Acquires an image (in fact an RGB image stack with three slices) of given size using a webcam. 
     * 
     * It uses the webcam-capture library by Bartosz Firyn.https://github.com/sarxos/webcam-capture
     */
    default boolean captureWebcamImage(ClearCLBuffer arg1, double arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("CaptureWebcamImage");}
        boolean result = CaptureWebcamImage.captureWebcamImage(getCLIJx(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("CaptureWebcamImage");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ConvertRGBStackToGraySlice
    //----------------------------------------------------
    /**
     * Converts a three channel image (stack with three slices) to a single channel image (2D image) by multiplying with factors 0.299, 0.587, 0.114.
     */
    default boolean convertRGBStackToGraySlice(ClearCLBuffer stack_source, ClearCLBuffer slice_destination) {
        if (doTimeTracing()) {recordMethodStart("ConvertRGBStackToGraySlice");}
        boolean result = ConvertRGBStackToGraySlice.convertRGBStackToGraySlice(getCLIJx(), stack_source, slice_destination);
        if (doTimeTracing()) {recordMethodEnd("ConvertRGBStackToGraySlice");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.NonLocalMeans
    //----------------------------------------------------
    /**
     * Applies a non-local means filter using a box neighborhood with a Gaussian weight specified with sigma to the input image.
     */
    default boolean nonLocalMeans(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        if (doTimeTracing()) {recordMethodStart("NonLocalMeans");}
        boolean result = NonLocalMeans.nonLocalMeans(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).floatValue());
        if (doTimeTracing()) {recordMethodEnd("NonLocalMeans");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Bilateral
    //----------------------------------------------------
    /**
     * Applies a bilateral filter using a box neighborhood with sigma weights for space and intensity to the input image.
     * 
     * Deprecated: Use SimpleITK bilateral() instead.
     */
    @Deprecated
    default boolean bilateral(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        System.out.println("bilateral is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("Bilateral");}
        boolean result = Bilateral.bilateral(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        if (doTimeTracing()) {recordMethodEnd("Bilateral");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.FindMaxima
    //----------------------------------------------------
    /**
     * Finds and labels local maxima with neighboring maxima and background above a given tolerance threshold.
     * 
     * 
     */
    default boolean findMaxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("FindMaxima");}
        boolean result = FindMaxima.findMaxima(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("FindMaxima");}
        return result;
    }

    /**
     * 
     */
    default boolean mergeTouchingLabelsSpecial(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4) {
        if (doTimeTracing()) {recordMethodStart("FindMaxima");}
        boolean result = FindMaxima.mergeTouchingLabelsSpecial(getCLIJ2(), arg1, arg2, arg3, arg4);
        if (doTimeTracing()) {recordMethodEnd("FindMaxima");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MergeTouchingLabels
    //----------------------------------------------------
    /**
     * 
     */
    default boolean mergeTouchingLabels(ClearCLBuffer source, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("MergeTouchingLabels");}
        boolean result = MergeTouchingLabels.mergeTouchingLabels(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("MergeTouchingLabels");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.AverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the average distance to their neighboring labels.
     * 
     * To determine the distances, the centroid of the labels is determined internally.
     */
    default boolean averageNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("AverageNeighborDistanceMap");}
        boolean result = AverageNeighborDistanceMap.averageNeighborDistanceMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("AverageNeighborDistanceMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.CylinderTransform
    //----------------------------------------------------
    /**
     * Applies a cylinder transform to an image stack assuming the center line goes in Y direction in the center of the stack.
     * 
     * This transforms an image stack from an XYZ coordinate system to a AYD coordinate system with 
     * A the angle around the center line, 
     * Y the original Y coordinate and 
     * D, the distance from the center.
     * 
     * Thus, going in virtual Z direction (actually D) in the resulting stack, you go from the center to theperiphery.
     */
    default boolean cylinderTransform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        if (doTimeTracing()) {recordMethodStart("CylinderTransform");}
        boolean result = CylinderTransform.cylinderTransform(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        if (doTimeTracing()) {recordMethodEnd("CylinderTransform");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima
    //----------------------------------------------------
    /**
     * Determines maximum regions in a Gaussian blurred version of the original image.
     * 
     * The regions do not not necessarily have to be single pixels. 
     * It is also possible to invert the image before determining the maxima.
     */
    default boolean detectAndLabelMaxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        if (doTimeTracing()) {recordMethodStart("DetectAndLabelMaxima");}
        boolean result = DetectAndLabelMaxima.detectAndLabelMaxima(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        if (doTimeTracing()) {recordMethodEnd("DetectAndLabelMaxima");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawDistanceMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. The intensity of the lines 
     * corresponds to the distance between these labels (in pixels or voxels).
     */
    default boolean drawDistanceMeshBetweenTouchingLabels(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("DrawDistanceMeshBetweenTouchingLabels");}
        boolean result = DrawDistanceMeshBetweenTouchingLabels.drawDistanceMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("DrawDistanceMeshBetweenTouchingLabels");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. 
     */
    default boolean drawMeshBetweenTouchingLabels(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("DrawMeshBetweenTouchingLabels");}
        boolean result = DrawMeshBetweenTouchingLabels.drawMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("DrawMeshBetweenTouchingLabels");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExcludeLabelsOutsideSizeRange
    //----------------------------------------------------
    /**
     * Removes labels from a label map which are not within a certain size range.
     * 
     * Size of the labels is given as the number of pixel or voxels per label.
     */
    default boolean excludeLabelsOutsideSizeRange(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("ExcludeLabelsOutsideSizeRange");}
        boolean result = ExcludeLabelsOutsideSizeRange.excludeLabelsOutsideSizeRange(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("ExcludeLabelsOutsideSizeRange");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExtendLabelsWithMaximumRadius
    //----------------------------------------------------
    /**
     * Extend labels with a given radius.
     * 
     * This is actually a local maximum filter applied to a label map which does not overwrite labels.
     * It is recommended to apply this operation to isotropic images only.
     */
    default boolean extendLabelsWithMaximumRadius(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("ExtendLabelsWithMaximumRadius");}
        boolean result = ExtendLabelsWithMaximumRadius.extendLabelsWithMaximumRadius(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("ExtendLabelsWithMaximumRadius");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima
    //----------------------------------------------------
    /**
     * Determine maxima with a given tolerance to surrounding maxima and background and label them.
     */
    default boolean findAndLabelMaxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        if (doTimeTracing()) {recordMethodStart("FindAndLabelMaxima");}
        boolean result = FindAndLabelMaxima.findAndLabelMaxima(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), arg4);
        if (doTimeTracing()) {recordMethodEnd("FindAndLabelMaxima");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MakeIsotropic
    //----------------------------------------------------
    /**
     * Applies a scaling operation using linear interpolation to generate an image stack with a given isotropic voxel size.
     */
    default boolean makeIsotropic(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        if (doTimeTracing()) {recordMethodStart("MakeIsotropic");}
        boolean result = MakeIsotropic.makeIsotropic(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        if (doTimeTracing()) {recordMethodEnd("MakeIsotropic");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.TouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the number of touching neighboring labels.
     * 
     * 
     */
    default boolean touchingNeighborCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("TouchingNeighborCountMap");}
        boolean result = TouchingNeighborCountMap.touchingNeighborCountMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("TouchingNeighborCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.RigidTransform
    //----------------------------------------------------
    /**
     * Applies a rigid transform using linear interpolation to an image stack.
     */
    default boolean rigidTransform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        if (doTimeTracing()) {recordMethodStart("RigidTransform");}
        boolean result = RigidTransform.rigidTransform(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        if (doTimeTracing()) {recordMethodEnd("RigidTransform");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SphereTransform
    //----------------------------------------------------
    /**
     * Turns an image stack in XYZ cartesian coordinate system to an AID polar coordinate system.
     * 
     * A corresponds to azimut,I to inclination and D to the distance from the center.Thus, going in virtual Z direction (actually D) in the resulting stack, you go from the center to theperiphery.
     */
    default boolean sphereTransform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        if (doTimeTracing()) {recordMethodStart("SphereTransform");}
        boolean result = SphereTransform.sphereTransform(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        if (doTimeTracing()) {recordMethodEnd("SphereTransform");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractGaussianBackground
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use differenceOfGaussian() instead.
     */
    default boolean subtractGaussianBackground(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("SubtractGaussianBackground");}
        boolean result = SubtractGaussianBackground.subtractGaussianBackground(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("SubtractGaussianBackground");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ThresholdDoG
    //----------------------------------------------------
    /**
     * 
     */
    @Deprecated
    default boolean localDoGThreshold(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        System.out.println("localDoGThreshold is deprecated. Check the documentation for a replacement. https://clij.github.io/clij2-doccs/reference");
        if (doTimeTracing()) {recordMethodStart("ThresholdDoG");}
        boolean result = ThresholdDoG.localDoGThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        if (doTimeTracing()) {recordMethodEnd("ThresholdDoG");}
        return result;
    }

    /**
     * Applies a Difference-of-Gaussian filter to an image and thresholds it with given sigma and threshold values.
     */
    default boolean thresholdDoG(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        if (doTimeTracing()) {recordMethodStart("ThresholdDoG");}
        boolean result = ThresholdDoG.thresholdDoG(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        if (doTimeTracing()) {recordMethodEnd("ThresholdDoG");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DriftCorrectionByCenterOfMassFixation
    //----------------------------------------------------
    /**
     * Determines the centerOfMass of the image stack and translates it so that it stays in a defined position.
     */
    default boolean driftCorrectionByCenterOfMassFixation(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("DriftCorrectionByCenterOfMassFixation");}
        boolean result = DriftCorrectionByCenterOfMassFixation.driftCorrectionByCenterOfMassFixation(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("DriftCorrectionByCenterOfMassFixation");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DriftCorrectionByCentroidFixation
    //----------------------------------------------------
    /**
     * Threshold the image stack, determines the centroid of the resulting binary image and 
     * translates the image stack so that its centroid sits in a defined position.
     */
    default boolean driftCorrectionByCentroidFixation(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        if (doTimeTracing()) {recordMethodStart("DriftCorrectionByCentroidFixation");}
        boolean result = DriftCorrectionByCentroidFixation.driftCorrectionByCentroidFixation(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        if (doTimeTracing()) {recordMethodEnd("DriftCorrectionByCentroidFixation");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.IntensityCorrection
    //----------------------------------------------------
    /**
     * Determines the mean intensity of the image stack and multiplies it with a factor so that the mean intensity becomes equal to a given value.
     */
    default boolean intensityCorrection(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("IntensityCorrection");}
        boolean result = IntensityCorrection.intensityCorrection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("IntensityCorrection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.IntensityCorrectionAboveThresholdOtsu
    //----------------------------------------------------
    /**
     * Determines the mean intensity of all pixel the image stack which are above a determined Threshold (Otsu et al. 1979) and multiplies it with a factor so that the mean intensity becomes equal to a given value.
     */
    default boolean intensityCorrectionAboveThresholdOtsu(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("IntensityCorrectionAboveThresholdOtsu");}
        boolean result = IntensityCorrectionAboveThresholdOtsu.intensityCorrectionAboveThresholdOtsu(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("IntensityCorrectionAboveThresholdOtsu");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMeanIntensityMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the mean intensity per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing mean object intensity.
     */
    default boolean labelMeanIntensityMap(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LabelMeanIntensityMap");}
        boolean result = LabelMeanIntensityMap.labelMeanIntensityMap(getCLIJ2(), input, label_map, destination);
        if (doTimeTracing()) {recordMethodEnd("LabelMeanIntensityMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelStandardDeviationIntensityMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the standard deviation of the intensity per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing standard deviation of object intensity.
     */
    default boolean labelStandardDeviationIntensityMap(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LabelStandardDeviationIntensityMap");}
        boolean result = LabelStandardDeviationIntensityMap.labelStandardDeviationIntensityMap(getCLIJ2(), input, label_map, destination);
        if (doTimeTracing()) {recordMethodEnd("LabelStandardDeviationIntensityMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelPixelCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines the number of pixels per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing area or volume.
     */
    default boolean labelPixelCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LabelPixelCountMap");}
        boolean result = LabelPixelCountMap.labelPixelCountMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LabelPixelCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ParametricWatershed
    //----------------------------------------------------
    /**
     * Apply a binary watershed to a binary image and introduce black pixels between objects.
     * 
     * To have control about where objects are cut, the sigma parameters allow to control a Gaussian blur filter applied to the internally used distance map.
     */
    default boolean parametricWatershed(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("ParametricWatershed");}
        boolean result = ParametricWatershed.parametricWatershed(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("ParametricWatershed");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z but only for pixels above a given threshold.
     */
    default boolean meanZProjectionAboveThreshold(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("MeanZProjectionAboveThreshold");}
        boolean result = MeanZProjectionAboveThreshold.meanZProjectionAboveThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("MeanZProjectionAboveThreshold");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SeededWatershed
    //----------------------------------------------------
    /**
     * Takes a label map (seeds) and an input image with gray values to apply the watershed algorithm and split the image above a given threshold in labels.
     */
    default boolean seededWatershed(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("SeededWatershed");}
        boolean result = SeededWatershed.seededWatershed(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("SeededWatershed");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.PushMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.PopMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.ResetMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.AverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids and replaces every label with the average distance to the n closest neighboring labels.
     */
    default boolean averageDistanceOfNClosestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("AverageDistanceOfNClosestNeighborsMap");}
        boolean result = AverageDistanceOfNClosestNeighborsMap.averageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("AverageDistanceOfNClosestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawTouchCountMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. The intensity of the lines 
     * corresponds to the touch count between these labels.
     */
    default boolean drawTouchCountMeshBetweenTouchingLabels(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("DrawTouchCountMeshBetweenTouchingLabels");}
        boolean result = DrawTouchCountMeshBetweenTouchingLabels.drawTouchCountMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("DrawTouchCountMeshBetweenTouchingLabels");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMaximumAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the maximum distance of touching labels.
     */
    default boolean localMaximumAverageDistanceOfNClosestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("LocalMaximumAverageDistanceOfNClosestNeighborsMap");}
        boolean result = LocalMaximumAverageDistanceOfNClosestNeighborsMap.localMaximumAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("LocalMaximumAverageDistanceOfNClosestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMaximumAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the maximum distancebetween touching neighbors. It then replaces every label with the that value.
     */
    default boolean localMaximumAverageNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMaximumAverageNeighborDistanceMap");}
        boolean result = LocalMaximumAverageNeighborDistanceMap.localMaximumAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMaximumAverageNeighborDistanceMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMaximumTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local maximum of this count.
     * 
     * 
     */
    default boolean localMaximumTouchingNeighborCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMaximumTouchingNeighborCountMap");}
        boolean result = LocalMaximumTouchingNeighborCountMap.localMaximumTouchingNeighborCountMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMaximumTouchingNeighborCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMeanAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the mean distance of touching labels.
     */
    default boolean localMeanAverageDistanceOfNClosestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("LocalMeanAverageDistanceOfNClosestNeighborsMap");}
        boolean result = LocalMeanAverageDistanceOfNClosestNeighborsMap.localMeanAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("LocalMeanAverageDistanceOfNClosestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMeanAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the mean distancebetween touching neighbors. It then replaces every label with the that value.
     */
    default boolean localMeanAverageNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMeanAverageNeighborDistanceMap");}
        boolean result = LocalMeanAverageNeighborDistanceMap.localMeanAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMeanAverageNeighborDistanceMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMeanTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local mean of this count.
     * 
     * 
     */
    default boolean localMeanTouchingNeighborCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMeanTouchingNeighborCountMap");}
        boolean result = LocalMeanTouchingNeighborCountMap.localMeanTouchingNeighborCountMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMeanTouchingNeighborCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMeanTouchPortionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and how much, relatively taking the whole outline of 
     * each label into account, and determines for every label with the mean of this value and replaces the 
     * label index with that value.
     * 
     * 
     */
    default boolean localMeanTouchPortionMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMeanTouchPortionMap");}
        boolean result = LocalMeanTouchPortionMap.localMeanTouchPortionMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMeanTouchPortionMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMedianAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the median distance of touching labels.
     */
    default boolean localMedianAverageDistanceOfNClosestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("LocalMedianAverageDistanceOfNClosestNeighborsMap");}
        boolean result = LocalMedianAverageDistanceOfNClosestNeighborsMap.localMedianAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("LocalMedianAverageDistanceOfNClosestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMedianAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the median distancebetween touching neighbors. It then replaces every label with the that value.
     */
    default boolean localMedianAverageNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMedianAverageNeighborDistanceMap");}
        boolean result = LocalMedianAverageNeighborDistanceMap.localMedianAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMedianAverageNeighborDistanceMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMedianTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local median of this count.
     * 
     * 
     */
    default boolean localMedianTouchingNeighborCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMedianTouchingNeighborCountMap");}
        boolean result = LocalMedianTouchingNeighborCountMap.localMedianTouchingNeighborCountMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMedianTouchingNeighborCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMinimumAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the minimum distance of touching labels.
     */
    default boolean localMinimumAverageDistanceOfNClosestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("LocalMinimumAverageDistanceOfNClosestNeighborsMap");}
        boolean result = LocalMinimumAverageDistanceOfNClosestNeighborsMap.localMinimumAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("LocalMinimumAverageDistanceOfNClosestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMinimumAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the minimum distancebetween touching neighbors. It then replaces every label with the that value.
     */
    default boolean localMinimumAverageNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMinimumAverageNeighborDistanceMap");}
        boolean result = LocalMinimumAverageNeighborDistanceMap.localMinimumAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMinimumAverageNeighborDistanceMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMinimumTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local minimum of this count.
     * 
     * 
     */
    default boolean localMinimumTouchingNeighborCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalMinimumTouchingNeighborCountMap");}
        boolean result = LocalMinimumTouchingNeighborCountMap.localMinimumTouchingNeighborCountMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalMinimumTouchingNeighborCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the standard deviation distance of touching labels.
     */
    default boolean localStandardDeviationAverageDistanceOfNClosestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap");}
        boolean result = LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap.localStandardDeviationAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the standard deviation distancebetween touching neighbors. It then replaces every label with the that value.
     */
    default boolean localStandardDeviationAverageNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalStandardDeviationAverageNeighborDistanceMap");}
        boolean result = LocalStandardDeviationAverageNeighborDistanceMap.localStandardDeviationAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalStandardDeviationAverageNeighborDistanceMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local standard deviation of this count.
     * 
     * 
     */
    default boolean localStandardDeviationTouchingNeighborCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LocalStandardDeviationTouchingNeighborCountMap");}
        boolean result = LocalStandardDeviationTouchingNeighborCountMap.localStandardDeviationTouchingNeighborCountMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LocalStandardDeviationTouchingNeighborCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMinimumIntensityMap
    //----------------------------------------------------
    /**
     * 
     */
    default boolean labelMinimumIntensityMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3) {
        if (doTimeTracing()) {recordMethodStart("LabelMinimumIntensityMap");}
        boolean result = LabelMinimumIntensityMap.labelMinimumIntensityMap(getCLIJ2(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("LabelMinimumIntensityMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMaximumIntensityMap
    //----------------------------------------------------
    /**
     * 
     */
    default boolean labelMaximumIntensityMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3) {
        if (doTimeTracing()) {recordMethodStart("LabelMaximumIntensityMap");}
        boolean result = LabelMaximumIntensityMap.labelMaximumIntensityMap(getCLIJ2(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("LabelMaximumIntensityMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionRatioMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the maximum distance of any pixel to the centroid and replaces every label with the that number.
     * 
     * 
     */
    default boolean labelMaximumExtensionRatioMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LabelMaximumExtensionRatioMap");}
        boolean result = LabelMaximumExtensionRatioMap.labelMaximumExtensionRatioMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LabelMaximumExtensionRatioMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the maximum distance of any pixel to the centroid and replaces every label with the that number.
     * 
     * 
     */
    default boolean labelMaximumExtensionMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LabelMaximumExtensionMap");}
        boolean result = LabelMaximumExtensionMap.labelMaximumExtensionMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LabelMaximumExtensionMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox
    //----------------------------------------------------
    /**
     * Takes an image and assumes its grey values are integers. It builds up a grey-level co-occurrence matrix of neighboring (west, south-west, south, south-east, in 3D 9 pixels on the next plane) pixel intensities. 
     * 
     * Major parts of this operation run on the CPU.
     */
    default boolean generateIntegerGreyValueCooccurrenceCountMatrixHalfBox(ClearCLBuffer integer_image, ClearCLBuffer grey_value_cooccurrence_matrix_destination) {
        if (doTimeTracing()) {recordMethodStart("GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox");}
        boolean result = GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox.generateIntegerGreyValueCooccurrenceCountMatrixHalfBox(getCLIJ2(), integer_image, grey_value_cooccurrence_matrix_destination);
        if (doTimeTracing()) {recordMethodEnd("GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond
    //----------------------------------------------------
    /**
     * Takes an image and assumes its grey values are integers. It builds up a grey-level co-occurrence matrix of neighboring (left, bottom, back) pixel intensities. 
     * 
     * Major parts of this operation run on the CPU.
     */
    default boolean generateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond(ClearCLBuffer integer_image, ClearCLBuffer grey_value_cooccurrence_matrix_destination) {
        if (doTimeTracing()) {recordMethodStart("GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond");}
        boolean result = GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond.generateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond(getCLIJ2(), integer_image, grey_value_cooccurrence_matrix_destination);
        if (doTimeTracing()) {recordMethodEnd("GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DivideByGaussianBackground
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and divides the original by the result.
     */
    default boolean divideByGaussianBackground(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("DivideByGaussianBackground");}
        boolean result = DivideByGaussianBackground.divideByGaussianBackground(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("DivideByGaussianBackground");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateGreyValueCooccurrenceMatrixBox
    //----------------------------------------------------
    /**
     * Takes an image and an intensity range to determine a grey value co-occurrence matrix.
     * 
     * For determining which pixel intensities are neighbors, the box neighborhood is taken into account.
     * Pixels with intensity below minimum of the given range are considered having the minimum intensity.
     * Pixels with intensity above the maximimum of the given range are treated analogously.
     * The resulting co-occurrence matrix contains probability values between 0 and 1.
     */
    default boolean generateGreyValueCooccurrenceMatrixBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("GenerateGreyValueCooccurrenceMatrixBox");}
        boolean result = GenerateGreyValueCooccurrenceMatrixBox.generateGreyValueCooccurrenceMatrixBox(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("GenerateGreyValueCooccurrenceMatrixBox");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GreyLevelAtttributeFiltering
    //----------------------------------------------------
    /**
     * Inspired by Grayscale attribute filtering from MorpholibJ library by David Legland & Ignacio Arganda-Carreras.
     * 
     * This plugin will remove components in a grayscale image based on user-specified area (for 2D: pixels) or volume (3D: voxels).
     * For each gray level specified in the number of bins, binary images will be generated, followed by exclusion of objects (labels)
     * below a minimum pixel count.
     * All the binary images for each gray level are combined to form the final image. The output is a grayscale image, where bright objects
     * below pixel count are removed.
     * It is recommended that low values be used for number of bins, especially for large 3D images, or it may take long time.
     */
    default boolean greyLevelAtttributeFiltering(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("GreyLevelAtttributeFiltering");}
        boolean result = GreyLevelAtttributeFiltering.greyLevelAtttributeFiltering(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("GreyLevelAtttributeFiltering");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.BinaryFillHolesSliceBySlice
    //----------------------------------------------------
    /**
     * Fills holes (pixels with value 0 surrounded by pixels with value 1) in a binary image stack slice by slice.
     */
    default boolean binaryFillHolesSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("BinaryFillHolesSliceBySlice");}
        boolean result = BinaryFillHolesSliceBySlice.binaryFillHolesSliceBySlice(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("BinaryFillHolesSliceBySlice");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier
    //----------------------------------------------------
    /**
     * Applies a pre-trained CLIJx-Weka model to a 2D image. 
     * 
     * You can train your own model using menu Plugins > Segmentation > CLIJx Binary Weka Pixel ClassifierMake sure that the handed over feature list is the same used while training the model.
     */
    default boolean binaryWekaPixelClassifier(ClearCLBuffer input, ClearCLBuffer destination, String features, String modelfilename) {
        if (doTimeTracing()) {recordMethodStart("BinaryWekaPixelClassifier");}
        boolean result = BinaryWekaPixelClassifier.binaryWekaPixelClassifier(getCLIJ2(), input, destination, features, modelfilename);
        if (doTimeTracing()) {recordMethodEnd("BinaryWekaPixelClassifier");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.WekaLabelClassifier
    //----------------------------------------------------
    /**
     * Applies a pre-trained CLIJx-Weka model to an image and a corresponding label map to classify labeled objects. 
     * 
     * Make sure that the handed over feature list is the same used while training the model.
     */
    default boolean wekaLabelClassifier(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination, String features, String modelfilename) {
        if (doTimeTracing()) {recordMethodStart("WekaLabelClassifier");}
        boolean result = WekaLabelClassifier.wekaLabelClassifier(getCLIJ2(), input, label_map, destination, features, modelfilename);
        if (doTimeTracing()) {recordMethodEnd("WekaLabelClassifier");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.GenerateLabelFeatureImage
    //----------------------------------------------------
    /**
     * Generates a feature image for Trainable Weka Segmentation. 
     * 
     * Use this terminology to specify which features should be generated:
     * * BOUNDING_BOX_DEPTH
     * * BOUNDING_BOX_WIDTH
     * * BOUNDING_BOX_HEIGHT
     * * CENTROID_X
     * * CENTROID_Y
     * * CENTROID_Z
     * * MASS_CENTER_X
     * * MASS_CENTER_Y
     * * MASS_CENTER_Z
     * * MAX_DISTANCE_TO_CENTROID
     * * MAX_DISTANCE_TO_MASS_CENTER
     * * MEAN_DISTANCE_TO_CENTROID
     * * MEAN_DISTANCE_TO_MASS_CENTER
     * * MAX_MEAN_DISTANCE_TO_CENTROID_RATIO
     * * MAX_MEAN_DISTANCE_TO_MASS_CENTER_RATIO
     * * MAXIMUM_INTENSITY
     * * MEAN_INTENSITY
     * * MINIMUM_INTENSITY
     * * SUM_INTENSITY
     * * STANDARD_DEVIATION_INTENSITY
     * * PIXEL_COUNT
     * * count_touching_neighbors
     * * average_touch_pixel_count
     * * average_distance_of_touching_neighbors
     * * MEAN_OF_LAPLACIAN
     * 
     * Example: "MEAN_INTENSITY count_touching_neighbors"
     */
    default boolean generateLabelFeatureImage(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer label_feature_image_destination, String feature_definitions) {
        if (doTimeTracing()) {recordMethodStart("GenerateLabelFeatureImage");}
        boolean result = GenerateLabelFeatureImage.generateLabelFeatureImage(getCLIJ2(), input, label_map, label_feature_image_destination, feature_definitions);
        if (doTimeTracing()) {recordMethodEnd("GenerateLabelFeatureImage");}
        return result;
    }

    /**
     * Generates a feature image for Trainable Weka Segmentation. 
     * 
     * Use this terminology to specify which features should be generated:
     * * BOUNDING_BOX_DEPTH
     * * BOUNDING_BOX_WIDTH
     * * BOUNDING_BOX_HEIGHT
     * * CENTROID_X
     * * CENTROID_Y
     * * CENTROID_Z
     * * MASS_CENTER_X
     * * MASS_CENTER_Y
     * * MASS_CENTER_Z
     * * MAX_DISTANCE_TO_CENTROID
     * * MAX_DISTANCE_TO_MASS_CENTER
     * * MEAN_DISTANCE_TO_CENTROID
     * * MEAN_DISTANCE_TO_MASS_CENTER
     * * MAX_MEAN_DISTANCE_TO_CENTROID_RATIO
     * * MAX_MEAN_DISTANCE_TO_MASS_CENTER_RATIO
     * * MAXIMUM_INTENSITY
     * * MEAN_INTENSITY
     * * MINIMUM_INTENSITY
     * * SUM_INTENSITY
     * * STANDARD_DEVIATION_INTENSITY
     * * PIXEL_COUNT
     * * count_touching_neighbors
     * * average_touch_pixel_count
     * * average_distance_of_touching_neighbors
     * * MEAN_OF_LAPLACIAN
     * 
     * Example: "MEAN_INTENSITY count_touching_neighbors"
     */
    default ClearCLBuffer generateLabelFeatureImage(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3) {
        if (doTimeTracing()) {recordMethodStart("GenerateLabelFeatureImage");}
        ClearCLBuffer result = GenerateLabelFeatureImage.generateLabelFeatureImage(getCLIJ2(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("GenerateLabelFeatureImage");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelSurface
    //----------------------------------------------------
    /**
     * Takes a label map and excludes all labels which are not on the surface.
     * 
     * For each label, a ray from a given center towards the label. If the ray crosses another label, the labelin question is not at the surface and thus, removed.
     */
    default boolean labelSurface(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("LabelSurface");}
        boolean result = LabelSurface.labelSurface(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("LabelSurface");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ReduceLabelsToLabelledSpots
    //----------------------------------------------------
    /**
     * Takes a label map and reduces all labels to their center spots. Label IDs stay and background will be zero.
     */
    default boolean reduceLabelsToLabelledSpots(ClearCLBuffer input_labels, ClearCLBuffer destination_labels) {
        if (doTimeTracing()) {recordMethodStart("ReduceLabelsToLabelledSpots");}
        boolean result = ReduceLabelsToLabelledSpots.reduceLabelsToLabelledSpots(getCLIJ2(), input_labels, destination_labels);
        if (doTimeTracing()) {recordMethodEnd("ReduceLabelsToLabelledSpots");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMeanExtensionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the mean distance of any pixel to the centroid and replaces every label with the that number.
     * 
     * 
     */
    default boolean labelMeanExtensionMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LabelMeanExtensionMap");}
        boolean result = LabelMeanExtensionMap.labelMeanExtensionMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("LabelMeanExtensionMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MeanZProjectionBelowThreshold
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z but only for pixels below a given threshold.
     */
    default boolean meanZProjectionBelowThreshold(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("MeanZProjectionBelowThreshold");}
        boolean result = MeanZProjectionBelowThreshold.meanZProjectionBelowThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("MeanZProjectionBelowThreshold");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.EuclideanDistanceFromLabelCentroidMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines the centroids of all labels and writes the distance of all labelled pixels to their centroid in the result image.
     * Background pixels stay zero.
     */
    default boolean euclideanDistanceFromLabelCentroidMap(ClearCLBuffer labelmap_input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("EuclideanDistanceFromLabelCentroidMap");}
        boolean result = EuclideanDistanceFromLabelCentroidMap.euclideanDistanceFromLabelCentroidMap(getCLIJ2(), labelmap_input, destination);
        if (doTimeTracing()) {recordMethodEnd("EuclideanDistanceFromLabelCentroidMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GammaCorrection
    //----------------------------------------------------
    /**
     * Applies a gamma correction to an image.
     * 
     * Therefore, all pixels x of the Image X are normalized and the power to gamma g is computed, before normlization is reversed (^ is the power operator):f(x) = (x / max(X)) ^ gamma * max(X)
     */
    default boolean gammaCorrection(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("GammaCorrection");}
        boolean result = GammaCorrection.gammaCorrection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("GammaCorrection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ZPositionOfMaximumZProjection
    //----------------------------------------------------
    /**
     * Determines a Z-position of the maximum intensity along Z and writes it into the resulting image.
     * 
     * If there are multiple z-slices with the same value, the smallest Z will be chosen.
     */
    default boolean zPositionOfMaximumZProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("ZPositionOfMaximumZProjection");}
        boolean result = ZPositionOfMaximumZProjection.zPositionOfMaximumZProjection(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("ZPositionOfMaximumZProjection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ZPositionProjection
    //----------------------------------------------------
    /**
     * Project a defined Z-slice of a 3D stack into a 2D image.
     * 
     * The slice is determined using a separate 2D image.
     */
    default boolean zPositionProjection(ClearCLImageInterface source_stack, ClearCLImageInterface z_position, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("ZPositionProjection");}
        boolean result = ZPositionProjection.zPositionProjection(getCLIJ2(), source_stack, z_position, destination);
        if (doTimeTracing()) {recordMethodEnd("ZPositionProjection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ZPositionRangeProjection
    //----------------------------------------------------
    /**
     * Project multiple Z-slices of a 3D stack into a new 3D stack.
     * 
     * The slices are defined using a separate 2D image containing z-positions and two numbers defining the range.
     */
    default boolean zPositionRangeProjection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, int arg4, int arg5) {
        if (doTimeTracing()) {recordMethodStart("ZPositionRangeProjection");}
        boolean result = ZPositionRangeProjection.zPositionRangeProjection(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        if (doTimeTracing()) {recordMethodEnd("ZPositionRangeProjection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.VarianceSphere
    //----------------------------------------------------
    /**
     * Computes the local variance of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    default boolean varianceSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("VarianceSphere");}
        boolean result = VarianceSphere.varianceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("VarianceSphere");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StandardDeviationSphere
    //----------------------------------------------------
    /**
     * Computes the local standard deviation of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    default boolean standardDeviationSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("StandardDeviationSphere");}
        boolean result = StandardDeviationSphere.standardDeviationSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("StandardDeviationSphere");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.VarianceBox
    //----------------------------------------------------
    /**
     * Computes the local variance of a pixels box neighborhood. 
     * 
     * The box size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    default boolean varianceBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("VarianceBox");}
        boolean result = VarianceBox.varianceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("VarianceBox");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StandardDeviationBox
    //----------------------------------------------------
    /**
     * Computes the local standard deviation of a pixels box neighborhood. 
     * 
     * The box size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    default boolean standardDeviationBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("StandardDeviationBox");}
        boolean result = StandardDeviationBox.standardDeviationBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        if (doTimeTracing()) {recordMethodEnd("StandardDeviationBox");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Tenengrad
    //----------------------------------------------------
    /**
     * Convolve the image with the Tenengrad kernel slice by slice.
     */
    default boolean tenengrad(ClearCLImageInterface source, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("Tenengrad");}
        boolean result = Tenengrad.tenengrad(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("Tenengrad");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.TenengradSliceBySlice
    //----------------------------------------------------
    /**
     * Convolve the image with the Tenengrad kernel slice by slice.
     */
    default boolean tenengradSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("TenengradSliceBySlice");}
        boolean result = TenengradSliceBySlice.tenengradSliceBySlice(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("TenengradSliceBySlice");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SobelSliceBySlice
    //----------------------------------------------------
    /**
     * Convolve the image with the Sobel kernel slice by slice.
     */
    default boolean sobelSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("SobelSliceBySlice");}
        boolean result = SobelSliceBySlice.sobelSliceBySlice(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("SobelSliceBySlice");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusSobelProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing local pixel intensity variance.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    default boolean extendedDepthOfFocusVarianceProjection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("ExtendedDepthOfFocusSobelProjection");}
        boolean result = ExtendedDepthOfFocusSobelProjection.extendedDepthOfFocusVarianceProjection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("ExtendedDepthOfFocusSobelProjection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusTenengradProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing intensity in the local sobel image.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    default boolean extendedDepthOfFocusTenengradProjection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("ExtendedDepthOfFocusTenengradProjection");}
        boolean result = ExtendedDepthOfFocusTenengradProjection.extendedDepthOfFocusTenengradProjection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("ExtendedDepthOfFocusTenengradProjection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusVarianceProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing local pixel intensity variance.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    default boolean extendedDepthOfFocusVarianceProjection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("ExtendedDepthOfFocusVarianceProjection");}
        boolean result = ExtendedDepthOfFocusVarianceProjection.extendedDepthOfFocusVarianceProjection(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("ExtendedDepthOfFocusVarianceProjection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawMeshBetweenNClosestLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between n closest labels for each label resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. 
     */
    default boolean drawMeshBetweenNClosestLabels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("DrawMeshBetweenNClosestLabels");}
        boolean result = DrawMeshBetweenNClosestLabels.drawMeshBetweenNClosestLabels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("DrawMeshBetweenNClosestLabels");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawMeshBetweenProximalLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between labels that are closer than a given distance resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels.
     */
    default boolean drawMeshBetweenProximalLabels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("DrawMeshBetweenProximalLabels");}
        boolean result = DrawMeshBetweenProximalLabels.drawMeshBetweenProximalLabels(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("DrawMeshBetweenProximalLabels");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Cosinus
    //----------------------------------------------------
    /**
     * Computes the cosinus of all pixels value x.
     * 
     * <pre>f(x) = cos(x)</pre>
     */
    default boolean cosinus(ClearCLImageInterface source, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("Cosinus");}
        boolean result = Cosinus.cosinus(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("Cosinus");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Sinus
    //----------------------------------------------------
    /**
     * Computes the sinus of all pixels value x.
     * 
     * <pre>f(x) = sin(x)</pre>
     */
    default boolean sinus(ClearCLImageInterface source, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("Sinus");}
        boolean result = Sinus.sinus(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("Sinus");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateDistanceMatrixAlongAxis
    //----------------------------------------------------
    /**
     * Computes the distance in X, Y or Z (specified with parameter axis) between all point coordinates given in two point lists.
     * 
     * Takes two images containing pointlists (dimensionality n * d, n: number of points and d: dimensionality) and builds up a matrix containing the distances between these points. 
     * 
     * Convention: Given two point lists with dimensionality n * d and m * d, the distance matrix will be of size(n + 1) * (m + 1). The first row and column contain zeros. They represent the distance of the objects to a theoretical background object. In that way, distance matrices are of the same size as touch matrices (see generateTouchMatrix). Thus, one can threshold a distance matrix to generate a touch matrix out of it for drawing meshes.
     */
    default boolean generateDistanceMatrixAlongAxis(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("GenerateDistanceMatrixAlongAxis");}
        boolean result = GenerateDistanceMatrixAlongAxis.generateDistanceMatrixAlongAxis(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("GenerateDistanceMatrixAlongAxis");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MaximumDistanceOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a distance matrix to determine the maximum distance of touching neighbors for every object.
     */
    default boolean maximumDistanceOfTouchingNeighbors(ClearCLBuffer distance_matrix, ClearCLBuffer touch_matrix, ClearCLBuffer distancelist_destination) {
        if (doTimeTracing()) {recordMethodStart("MaximumDistanceOfTouchingNeighbors");}
        boolean result = MaximumDistanceOfTouchingNeighbors.maximumDistanceOfTouchingNeighbors(getCLIJ2(), distance_matrix, touch_matrix, distancelist_destination);
        if (doTimeTracing()) {recordMethodEnd("MaximumDistanceOfTouchingNeighbors");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MaximumNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the maximum distance to their neighboring labels.
     * 
     * To determine the distances, the centroid of the labels is determined internally.
     */
    default boolean maximumNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("MaximumNeighborDistanceMap");}
        boolean result = MaximumNeighborDistanceMap.maximumNeighborDistanceMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("MaximumNeighborDistanceMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MinimumNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the minimum distance to their neighboring labels.
     * 
     * To determine the distances, the centroid of the labels is determined internally.
     */
    default boolean minimumNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("MinimumNeighborDistanceMap");}
        boolean result = MinimumNeighborDistanceMap.minimumNeighborDistanceMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("MinimumNeighborDistanceMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateAngleMatrix
    //----------------------------------------------------
    /**
     * Computes the angle in radians between all point coordinates given in two point lists.
     * 
     *  Takes two images containing pointlists (dimensionality n * d, n: number of 
     * points and d: dimensionality) and builds up a matrix containing the 
     * angles between these points.
     * 
     * Convention: Values range from -90 to 90 degrees (-0.5 to 0.5 pi radians)
     * * -90 degreess (-0.5 pi radians): Top
     * * 0 defrees (0 radians): Right
     * * 90 degrees (0.5 pi radians): Bottom
     * 
     * Convention: Given two point lists with dimensionality n * d and m * d, the distance 
     * matrix will be of size(n + 1) * (m + 1). The first row and column 
     * contain zeros. They represent the distance of the objects to a 
     * theoretical background object. In that way, distance matrices are of 
     * the same size as touch matrices (see generateTouchMatrix). Thus, one 
     * can threshold a distance matrix to generate a touch matrix out of it 
     * for drawing meshes. 
     * 
     * Implemented for 2D only at the moment.
     * 
     * Parameters
     * ----------
     * coordinate_list1 : Image
     * coordinate_list2 : Image
     * angle_matrix_destination : Image
     * 
     * Returns
     * -------
     * angle_matrix_destination
     */
    default boolean generateAngleMatrix(ClearCLBuffer coordinate_list1, ClearCLBuffer coordinate_list2, ClearCLBuffer angle_matrix_destination) {
        if (doTimeTracing()) {recordMethodStart("GenerateAngleMatrix");}
        boolean result = GenerateAngleMatrix.generateAngleMatrix(getCLIJ2(), coordinate_list1, coordinate_list2, angle_matrix_destination);
        if (doTimeTracing()) {recordMethodEnd("GenerateAngleMatrix");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.NeighborDistanceRangeRatioMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the minimum distance to their neighboring labels.
     * 
     * To determine the distances, the centroid of the labels is determined internally.
     */
    default boolean neighborDistanceRangeRatioMap(ClearCLBuffer input, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("NeighborDistanceRangeRatioMap");}
        boolean result = NeighborDistanceRangeRatioMap.neighborDistanceRangeRatioMap(getCLIJ2(), input, destination);
        if (doTimeTracing()) {recordMethodEnd("NeighborDistanceRangeRatioMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.VoronoiOtsuLabeling
    //----------------------------------------------------
    /**
     * Applies two Gaussian blurs, spot detection, Otsu-thresholding and Voronoi-labeling.
     * The thresholded binary image is flooded using the Voronoi approach starting from the found local maxima.
     * Noise-removal sigma for spot detection and thresholding can be configured separately.
     */
    default boolean voronoiOtsuLabeling(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("VoronoiOtsuLabeling");}
        boolean result = VoronoiOtsuLabeling.voronoiOtsuLabeling(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("VoronoiOtsuLabeling");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.VisualizeOutlinesOnOriginal
    //----------------------------------------------------
    /**
     * Combines an intensity image and a label (or binary) image so that you can see segmentation outlines on the intensity image.
     */
    default boolean visualizeOutlinesOnOriginal(ClearCLBuffer intensity, ClearCLBuffer labels, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("VisualizeOutlinesOnOriginal");}
        boolean result = VisualizeOutlinesOnOriginal.visualizeOutlinesOnOriginal(getCLIJ2(), intensity, labels, destination);
        if (doTimeTracing()) {recordMethodEnd("VisualizeOutlinesOnOriginal");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.FlagLabelsOnEdges
    //----------------------------------------------------
    /**
     * Determines which labels in a label map touch the edges of the image (in X, Y and Z if the image is 3D). 
     * 
     * It results in a vector image with values 1 (touches edges) and 0 (does not touch edge).
     * The entry in the vector (index 0) corresponds to background, following entries correspond to labels.
     */
    default boolean flagLabelsOnEdges(ClearCLBuffer label_map_input, ClearCLBuffer flag_vector_destination) {
        if (doTimeTracing()) {recordMethodStart("FlagLabelsOnEdges");}
        boolean result = FlagLabelsOnEdges.flagLabelsOnEdges(getCLIJ2(), label_map_input, flag_vector_destination);
        if (doTimeTracing()) {recordMethodEnd("FlagLabelsOnEdges");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MaskedVoronoiLabeling
    //----------------------------------------------------
    /**
     * Takes a binary image, labels connected components and dilates the regions using a octagon shape until they touch and only inside another binary mask image.
     * 
     * The resulting label map is written to the output.
     * 
     * Hint: Process isotropic images only.
     */
    default boolean maskedVoronoiLabeling(ClearCLBuffer input, ClearCLBuffer mask, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("MaskedVoronoiLabeling");}
        boolean result = MaskedVoronoiLabeling.maskedVoronoiLabeling(getCLIJ2(), input, mask, destination);
        if (doTimeTracing()) {recordMethodEnd("MaskedVoronoiLabeling");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.PullToResultsTableColumn
    //----------------------------------------------------
    /**
     * Copies the content of a vector image to a column in the results table.
     * You can configure if new rows should be appended or if existing values should be overwritten.
     */
    default boolean pullToResultsTableColumn(ClearCLBuffer arg1, ResultsTable arg2, String arg3, boolean arg4) {
        if (doTimeTracing()) {recordMethodStart("PullToResultsTableColumn");}
        boolean result = PullToResultsTableColumn.pullToResultsTableColumn(getCLIJ2(), arg1, arg2, arg3, arg4);
        if (doTimeTracing()) {recordMethodEnd("PullToResultsTableColumn");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer
    //----------------------------------------------------
    /**
     * Applies K-Means clustering to an image and a corresponding label map. 
     * 
     * See also: https://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/ml/clustering/KMeansPlusPlusClusterer.html
     * Make sure that the handed over feature list is the same used while training the model.
     * The neighbor_radius specifies a correction step which allows to use a region where the mode of 
     * classification results (the most popular class) will be determined after clustering.
     */
    default boolean kMeansLabelClusterer(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, String arg4, String arg5, double arg6, double arg7, boolean arg8) {
        if (doTimeTracing()) {recordMethodStart("KMeansLabelClusterer");}
        boolean result = KMeansLabelClusterer.kMeansLabelClusterer(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), arg8);
        if (doTimeTracing()) {recordMethodEnd("KMeansLabelClusterer");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ModeOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a vector of values to determine the most popular integer value among touching neighbors for every object.
     * TODO: This only works for values between 0 and 255 for now.
     */
    default boolean modeOfTouchingNeighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer mode_values_destination) {
        if (doTimeTracing()) {recordMethodStart("ModeOfTouchingNeighbors");}
        boolean result = ModeOfTouchingNeighbors.modeOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, mode_values_destination);
        if (doTimeTracing()) {recordMethodEnd("ModeOfTouchingNeighbors");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateProximalNeighborsMatrix
    //----------------------------------------------------
    /**
     * Produces a touch-matrix where the neighbors within a given distance range are marked as touching neighbors.
     * 
     * Takes a distance matrix (e.g. derived from a pointlist of centroids) and marks for every column the neighbors whose
     * distance lie within a given distance range (>= min and <= max). 
     * The resulting matrix can be use as if it was a touch-matrix (a.k.a. adjacency graph matrix). 
     * 
     * Parameters
     * ----------
     * distance_marix : Image
     * touch_matrix_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: 10 
     * 
     * Returns
     * -------
     * touch_matrix_destination
     */
    default boolean generateProximalNeighborsMatrix(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("GenerateProximalNeighborsMatrix");}
        boolean result = GenerateProximalNeighborsMatrix.generateProximalNeighborsMatrix(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("GenerateProximalNeighborsMatrix");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ReadIntensitiesFromMap
    //----------------------------------------------------
    /**
     * Takes a label image and an parametric image and reads parametric values from the labels positions.
     * 
     * The read intensity valus are stored in a new vector.
     * 
     * Note: This will only work if all labels have number of voxels == 1 or if all pixels in each label have the same value.
     * 
     * DEPRECATED: Use ReadValuesFromMap instead
     */
    default boolean readIntensitiesFromMap(ClearCLImageInterface labels, ClearCLImageInterface map_image, ClearCLImageInterface values_destination) {
        if (doTimeTracing()) {recordMethodStart("ReadIntensitiesFromMap");}
        boolean result = ReadIntensitiesFromMap.readIntensitiesFromMap(getCLIJ2(), labels, map_image, values_destination);
        if (doTimeTracing()) {recordMethodEnd("ReadIntensitiesFromMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MaximumOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the maximum value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    default boolean maximumOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        if (doTimeTracing()) {recordMethodStart("MaximumOfTouchingNeighborsMap");}
        boolean result = MaximumOfTouchingNeighborsMap.maximumOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        if (doTimeTracing()) {recordMethodEnd("MaximumOfTouchingNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MinimumOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the minimum value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    default boolean minimumOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        if (doTimeTracing()) {recordMethodStart("MinimumOfTouchingNeighborsMap");}
        boolean result = MinimumOfTouchingNeighborsMap.minimumOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        if (doTimeTracing()) {recordMethodEnd("MinimumOfTouchingNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MeanOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the mean average value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    default boolean meanOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        if (doTimeTracing()) {recordMethodStart("MeanOfTouchingNeighborsMap");}
        boolean result = MeanOfTouchingNeighborsMap.meanOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        if (doTimeTracing()) {recordMethodEnd("MeanOfTouchingNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ModeOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the most popular value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    default boolean modeOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        if (doTimeTracing()) {recordMethodStart("ModeOfTouchingNeighborsMap");}
        boolean result = ModeOfTouchingNeighborsMap.modeOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        if (doTimeTracing()) {recordMethodEnd("ModeOfTouchingNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StandardDeviationOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the standard deviation value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    default boolean standardDeviationOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        if (doTimeTracing()) {recordMethodStart("StandardDeviationOfTouchingNeighborsMap");}
        boolean result = StandardDeviationOfTouchingNeighborsMap.standardDeviationOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        if (doTimeTracing()) {recordMethodEnd("StandardDeviationOfTouchingNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.PointIndexListToTouchMatrix
    //----------------------------------------------------
    /**
     * Takes a list of point indices to generate a touch matrix (a.k.a. adjacency graph matrix) out of it. 
     * 
     * The list hasa dimensionality of m*n for the points 1... m (0 a.k.a. background is not in this list). In the n rows, there are
     * indices to points which should be connected.
     * 
     * Parameters
     * ----------
     * indexlist : Image
     * matrix_destination : Image
     */
    default boolean pointIndexListToTouchMatrix(ClearCLBuffer indexlist, ClearCLBuffer matrix_destination) {
        if (doTimeTracing()) {recordMethodStart("PointIndexListToTouchMatrix");}
        boolean result = PointIndexListToTouchMatrix.pointIndexListToTouchMatrix(getCLIJ2(), indexlist, matrix_destination);
        if (doTimeTracing()) {recordMethodEnd("PointIndexListToTouchMatrix");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateNNearestNeighborsMatrix
    //----------------------------------------------------
    /**
     * Produces a touch-matrix where the n nearest neighbors are marked as touching neighbors. 
     * 
     * Takes a distance matrix (e.g. derived from a pointlist of centroids) and marks for every column the n smallest
     * distances as neighbors. The resulting matrix can be use as if it was a touch-matrix (a.k.a. adjacency graph matrix). 
     * 
     * Inspired by a similar implementation in imglib2 [1]
     * 
     * Note: The implementation is limited to square matrices.
     * 
     * Parameters
     * ----------
     * distance_marix : Image
     * touch_matrix_destination : Image
     * n : int
     *    number of neighbors
     *    
     * References
     * ----------
     * [1] https://github.com/imglib/imglib2/blob/master/src/main/java/net/imglib2/interpolation/neighborsearch/InverseDistanceWeightingInterpolator.java
     * 
     */
    default boolean generateNNearestNeighborsMatrix(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("GenerateNNearestNeighborsMatrix");}
        boolean result = GenerateNNearestNeighborsMatrix.generateNNearestNeighborsMatrix(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        if (doTimeTracing()) {recordMethodEnd("GenerateNNearestNeighborsMatrix");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MaximumOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the maximum value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    default boolean maximumOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("MaximumOfNNearestNeighborsMap");}
        boolean result = MaximumOfNNearestNeighborsMap.maximumOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("MaximumOfNNearestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MinimumOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the minimum value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    default boolean minimumOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("MinimumOfNNearestNeighborsMap");}
        boolean result = MinimumOfNNearestNeighborsMap.minimumOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("MinimumOfNNearestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MeanOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the minimum value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    default boolean meanOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("MeanOfNNearestNeighborsMap");}
        boolean result = MeanOfNNearestNeighborsMap.meanOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("MeanOfNNearestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ModeOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the mode value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    default boolean modeOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("ModeOfNNearestNeighborsMap");}
        boolean result = ModeOfNNearestNeighborsMap.modeOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("ModeOfNNearestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StandardDeviationOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the standard deviation value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    default boolean standardDeviationOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("StandardDeviationOfNNearestNeighborsMap");}
        boolean result = StandardDeviationOfNNearestNeighborsMap.standardDeviationOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        if (doTimeTracing()) {recordMethodEnd("StandardDeviationOfNNearestNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MaximumOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the maximum value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    default boolean maximumOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("MaximumOfProximalNeighborsMap");}
        boolean result = MaximumOfProximalNeighborsMap.maximumOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("MaximumOfProximalNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MinimumOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the minimum value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    default boolean minimumOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("MinimumOfProximalNeighborsMap");}
        boolean result = MinimumOfProximalNeighborsMap.minimumOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("MinimumOfProximalNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MeanOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the mean average value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    default boolean meanOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("MeanOfProximalNeighborsMap");}
        boolean result = MeanOfProximalNeighborsMap.meanOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("MeanOfProximalNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ModeOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the most popular value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    default boolean modeOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("ModeOfProximalNeighborsMap");}
        boolean result = ModeOfProximalNeighborsMap.modeOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("ModeOfProximalNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StandardDeviationOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the standard deviation value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    default boolean standardDeviationOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("StandardDeviationOfProximalNeighborsMap");}
        boolean result = StandardDeviationOfProximalNeighborsMap.standardDeviationOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("StandardDeviationOfProximalNeighborsMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelOverlapCountMap
    //----------------------------------------------------
    /**
     * Takes two label maps, and counts for every label in label map 1 how many labels overlap with it in label map 2.
     * 
     * The resulting map is generated from the label map 1 by replacing the labels with the respective count.
     */
    default boolean labelOverlapCountMap(ClearCLBuffer label_map1, ClearCLBuffer label_map2, ClearCLBuffer overlap_count_map_destination) {
        if (doTimeTracing()) {recordMethodStart("LabelOverlapCountMap");}
        boolean result = LabelOverlapCountMap.labelOverlapCountMap(getCLIJ2(), label_map1, label_map2, overlap_count_map_destination);
        if (doTimeTracing()) {recordMethodEnd("LabelOverlapCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelProximalNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes two label maps, and counts for every label in label map 1 how many labels are in a given distance range to it in label map 2.
     * 
     * Distances are computed from the centroids of the labels. The resulting map is generated from the label map 1 by replacing the labels with the respective count.
     */
    default boolean labelProximalNeighborCountMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        if (doTimeTracing()) {recordMethodStart("LabelProximalNeighborCountMap");}
        boolean result = LabelProximalNeighborCountMap.labelProximalNeighborCountMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        if (doTimeTracing()) {recordMethodEnd("LabelProximalNeighborCountMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ReduceLabelsToLabelEdges
    //----------------------------------------------------
    /**
     * Takes a label map and reduces all labels to their edges. Label IDs stay the same and background will be zero.
     */
    default boolean reduceLabelsToLabelEdges(ClearCLBuffer input_labels, ClearCLBuffer destination_labels) {
        if (doTimeTracing()) {recordMethodStart("ReduceLabelsToLabelEdges");}
        boolean result = ReduceLabelsToLabelEdges.reduceLabelsToLabelEdges(getCLIJ2(), input_labels, destination_labels);
        if (doTimeTracing()) {recordMethodEnd("ReduceLabelsToLabelEdges");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.OutOfIntensityRange
    //----------------------------------------------------
    /**
     * Sets all pixels to 1 if their intensity lies out of a given range, and 0 otherwise.
     * 
     * Given minimum and maximum are considered part of the range.
     */
    default boolean outOfIntensityRange(ClearCLBuffer arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("OutOfIntensityRange");}
        boolean result = OutOfIntensityRange.outOfIntensityRange(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("OutOfIntensityRange");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ShrinkLabels
    //----------------------------------------------------
    /**
     * Extend labels with a given radius.
     * 
     * This is actually a local minimum filter applied to a label map after introducing background-gaps between labels.
     * In case relabel_islands is set, split objects will get new labels each. In this case, more labels might be in the result.
     * It is recommended to apply this operation to isotropic images only.
     * Warning: If labels were too small, they may be missing in the resulting label image.
     */
    default boolean shrinkLabels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        if (doTimeTracing()) {recordMethodStart("ShrinkLabels");}
        boolean result = ShrinkLabels.shrinkLabels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), arg4);
        if (doTimeTracing()) {recordMethodEnd("ShrinkLabels");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Similar
    //----------------------------------------------------
    /**
     * Determines the absolute difference between two images and sets all pixels to 1 where it is below or equal a given tolerance, and 0 otherwise.
     */
    default boolean similar(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("Similar");}
        boolean result = Similar.similar(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("Similar");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Different
    //----------------------------------------------------
    /**
     * Determines the absolute difference between two images and sets all pixels to 1 where it is above a given tolerance, and 0 otherwise.
     */
    default boolean different(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        if (doTimeTracing()) {recordMethodStart("Different");}
        boolean result = Different.different(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        if (doTimeTracing()) {recordMethodEnd("Different");}
        return result;
    }


    // net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier
    //----------------------------------------------------
    /**
     * Applies a pre-trained CLIJx-Weka model to an image and a corresponding label map to classify labeled objects.
     * 
     * Given radii allow to configure if values of proximal neighbors, other labels with centroids closer 
     * than given radius, should be taken into account, e.g. for determining the regional maximum.
     * 
     * Make sure that the handed over feature list and radii are the same used while training the model.
     */
    default boolean wekaRegionalLabelClassifier(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, String arg4, String arg5, double arg6, double arg7, double arg8, double arg9) {
        if (doTimeTracing()) {recordMethodStart("WekaRegionalLabelClassifier");}
        boolean result = WekaRegionalLabelClassifier.wekaRegionalLabelClassifier(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue());
        if (doTimeTracing()) {recordMethodEnd("WekaRegionalLabelClassifier");}
        return result;
    }

    /**
     * 
     */
    default ClearCLBuffer generateRegionalLabelFeatureImage(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, int arg4, int arg5, int arg6, int arg7) {
        if (doTimeTracing()) {recordMethodStart("WekaRegionalLabelClassifier");}
        ClearCLBuffer result = WekaRegionalLabelClassifier.generateRegionalLabelFeatureImage(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, arg6, arg7);
        if (doTimeTracing()) {recordMethodEnd("WekaRegionalLabelClassifier");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMeanOfLaplacianMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the mean intensity in the laplacian of the image per label and replaces every label with the that number.
     * 
     * This results in a parametric image visualizing local contrast.
     */
    default boolean labelMeanOfLaplacianMap(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination) {
        if (doTimeTracing()) {recordMethodStart("LabelMeanOfLaplacianMap");}
        boolean result = LabelMeanOfLaplacianMap.labelMeanOfLaplacianMap(getCLIJ2(), input, label_map, destination);
        if (doTimeTracing()) {recordMethodEnd("LabelMeanOfLaplacianMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MedianZProjectionMasked
    //----------------------------------------------------
    /**
     * Determines the median intensity projection of an image stack along Z where pixels in a corresponding mask image are unequal to zero.
     */
    default boolean medianZProjectionMasked(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        if (doTimeTracing()) {recordMethodStart("MedianZProjectionMasked");}
        boolean result = MedianZProjectionMasked.medianZProjectionMasked(getCLIJ2(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("MedianZProjectionMasked");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MedianTouchPortionMap
    //----------------------------------------------------
    /**
     * Starts from a label map, determines median touch portion to neighbors (between 0 and 1) and draws a map.
     * 
     * 
     */
    default boolean medianTouchPortionMap(ClearCLBuffer labels, ClearCLBuffer map_destination) {
        if (doTimeTracing()) {recordMethodStart("MedianTouchPortionMap");}
        boolean result = MedianTouchPortionMap.medianTouchPortionMap(getCLIJ2(), labels, map_destination);
        if (doTimeTracing()) {recordMethodEnd("MedianTouchPortionMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.NeighborCountWithTouchPortionAboveThresholdMap
    //----------------------------------------------------
    /**
     * Starts from a label map, determines touch portion to neighbors, counts those above a given value (between 0 and 1) and draws a map.
     * 
     * 
     */
    default boolean neighborCountWithTouchPortionAboveThresholdMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("NeighborCountWithTouchPortionAboveThresholdMap");}
        boolean result = NeighborCountWithTouchPortionAboveThresholdMap.neighborCountWithTouchPortionAboveThresholdMap(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("NeighborCountWithTouchPortionAboveThresholdMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DivideScalarByImage
    //----------------------------------------------------
    /**
     * Divides a scalar s by image X pixel wise. 
     * 
     * <pre>f(s, x) = s / x</pre>
     */
    default boolean divideScalarByImage(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        if (doTimeTracing()) {recordMethodStart("DivideScalarByImage");}
        boolean result = DivideScalarByImage.divideScalarByImage(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        if (doTimeTracing()) {recordMethodEnd("DivideScalarByImage");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ReadValuesFromMap
    //----------------------------------------------------
    /**
     * Takes a label image and an parametric image and reads parametric values from the labels positions.
     * 
     * The read intensity valus are stored in a new vector.
     * 
     * Note: This will only work if all labels have number of voxels == 1 or if all pixels in each label have the same value.
     * 
     * Parameters
     * ----------
     * labels
     * map_image
     * values_destination
     */
    default boolean readValuesFromMap(ClearCLImageInterface labels, ClearCLImageInterface map_image, ClearCLImageInterface values_destination) {
        if (doTimeTracing()) {recordMethodStart("ReadValuesFromMap");}
        boolean result = ReadValuesFromMap.readValuesFromMap(getCLIJ2(), labels, map_image, values_destination);
        if (doTimeTracing()) {recordMethodEnd("ReadValuesFromMap");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ReadValuesFromPositions
    //----------------------------------------------------
    /**
     * Takes a pointlist and a parametric image and reads parametric values from the positions.
     * 
     * The read intensity values are stored in a new vector.
     * 
     * Parameters
     * ----------
     * pointlist
     * map_image
     * values_destination
     */
    default boolean readValuesFromPositions(ClearCLImageInterface pointlist, ClearCLImageInterface map_image, ClearCLImageInterface values_destination) {
        if (doTimeTracing()) {recordMethodStart("ReadValuesFromPositions");}
        boolean result = ReadValuesFromPositions.readValuesFromPositions(getCLIJ2(), pointlist, map_image, values_destination);
        if (doTimeTracing()) {recordMethodEnd("ReadValuesFromPositions");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ZPositionOfMinimumZProjection
    //----------------------------------------------------
    /**
     * Determines a Z-position of the minimum intensity along Z and writes it into the resulting image.
     * 
     * If there are multiple z-slices with the same value, the smallest Z will be chosen.
     */
    default boolean zPositionOfMinimumZProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        if (doTimeTracing()) {recordMethodStart("ZPositionOfMinimumZProjection");}
        boolean result = ZPositionOfMinimumZProjection.zPositionOfMinimumZProjection(getCLIJ2(), source, destination);
        if (doTimeTracing()) {recordMethodEnd("ZPositionOfMinimumZProjection");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdPhansalkar
    //----------------------------------------------------
    /**
     * Computes the local threshold (Fast version) based on 
     *  Auto Local Threshold (Phansalkar method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/c955dc18cff58ac61df82f3f001799f7ffaec5cb/src/main/java/fiji/threshold/Auto_Local_Threshold.java#L636 
     *  Formulary: 
     * <pre>t = mean * (1 + p * exp(-q * mean) + k * ((stdev / r) - 1))</pre>
     */
    default boolean localThresholdPhansalkar(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4, float arg5) {
        if (doTimeTracing()) {recordMethodStart("LocalThresholdPhansalkar");}
        boolean result = LocalThresholdPhansalkar.localThresholdPhansalkar(getCLIJx(), arg1, arg2, arg3, arg4, arg5);
        if (doTimeTracing()) {recordMethodEnd("LocalThresholdPhansalkar");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdBernsen
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Bernsen method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if (tcontrast > max - min){ if ((max + min)/2.0 >= 128) res = 0} else if (val > (max + min)/2.0) res =0</pre>
     */
    default boolean localThresholdBernsen(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4) {
        if (doTimeTracing()) {recordMethodStart("LocalThresholdBernsen");}
        boolean result = LocalThresholdBernsen.localThresholdBernsen(getCLIJx(), arg1, arg2, arg3, arg4);
        if (doTimeTracing()) {recordMethodEnd("LocalThresholdBernsen");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdContrast
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Contrast method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if (abs(value - min) >= abs(max - value) && (value != 0)) value = 0 </pre>
     */
    default boolean localThresholdContrast(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3) {
        if (doTimeTracing()) {recordMethodStart("LocalThresholdContrast");}
        boolean result = LocalThresholdContrast.localThresholdContrast(getCLIJx(), arg1, arg2, arg3);
        if (doTimeTracing()) {recordMethodEnd("LocalThresholdContrast");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdMean
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Mean method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if(value > (mean - c_value)) value = 0 </pre>
     */
    default boolean localThresholdMean(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4) {
        if (doTimeTracing()) {recordMethodStart("LocalThresholdMean");}
        boolean result = LocalThresholdMean.localThresholdMean(getCLIJx(), arg1, arg2, arg3, arg4);
        if (doTimeTracing()) {recordMethodEnd("LocalThresholdMean");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdMedian
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Median method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if(value > (median - c_value)) value = 0 </pre>
     */
    default boolean localThresholdMedian(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4) {
        if (doTimeTracing()) {recordMethodStart("LocalThresholdMedian");}
        boolean result = LocalThresholdMedian.localThresholdMedian(getCLIJx(), arg1, arg2, arg3, arg4);
        if (doTimeTracing()) {recordMethodEnd("LocalThresholdMedian");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdMidGrey
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (MidGrey method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if (value > ( (max + min)/2.0 - c_value) ) value = 0 </pre>
     */
    default boolean localThresholdMidGrey(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4) {
        if (doTimeTracing()) {recordMethodStart("LocalThresholdMidGrey");}
        boolean result = LocalThresholdMidGrey.localThresholdMidGrey(getCLIJx(), arg1, arg2, arg3, arg4);
        if (doTimeTracing()) {recordMethodEnd("LocalThresholdMidGrey");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdNiblack
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Niblack method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>t = mean + k_value * sqrt(var - c_value) </pre>
     */
    default boolean localThresholdNiblack(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4, float arg5) {
        if (doTimeTracing()) {recordMethodStart("LocalThresholdNiblack");}
        boolean result = LocalThresholdNiblack.localThresholdNiblack(getCLIJx(), arg1, arg2, arg3, arg4, arg5);
        if (doTimeTracing()) {recordMethodEnd("LocalThresholdNiblack");}
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdSauvola
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Sauvola method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>t = mean * (1.0 + k_value * (stddev / r_value - 1.0)) </pre>
     */
    default boolean localThresholdSauvola(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4, float arg5) {
        if (doTimeTracing()) {recordMethodStart("LocalThresholdSauvola");}
        boolean result = LocalThresholdSauvola.localThresholdSauvola(getCLIJx(), arg1, arg2, arg3, arg4, arg5);
        if (doTimeTracing()) {recordMethodEnd("LocalThresholdSauvola");}
        return result;
    }

}
// 224 methods generated.
