__constant sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;

__constant float hx[] = {-1,-2,-1,-2,-4,-2,-1,-2,-1,0,0,0,0,0,0,0,0,0,1,2,1,2,4,2,1,2,1};
__constant float hy[] = {-1,-2,-1,0,0,0,1,2,1,-2,-4,-2,0,0,0,2,4,2,-1,-2,-1,0,0,0,1,2,1};
__constant float hz[] = {-1,0,1,-2,0,2,-1,0,1,-2,0,2,-4,0,4,-2,0,2,-1,0,1,-2,0,2,-1,0,1};


__kernel void tenengrad_fusion_with_provided_weights_3_images(
  IMAGE_dst_TYPE dst, const int factor,
  IMAGE_src0_TYPE src0, IMAGE_src1_TYPE src1, IMAGE_src2_TYPE src2,
  IMAGE_weight0_TYPE weight0, IMAGE_weight1_TYPE weight1, IMAGE_weight2_TYPE weight2
)
{
  const int i = get_global_id(0), j = get_global_id(1), k = get_global_id(2);
  const POS_src0_TYPE coord = POS_src0_INSTANCE(i,j,k,0);

  const POS_weight0_TYPE coord_weight = POS_weight0_INSTANCE((i+0.5f)/factor,(j+0.5f)/factor,k+0.5f,0);
  const sampler_t sampler_weight = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_LINEAR;

  float w0 = READ_weight0_IMAGE(weight0,sampler_weight,coord_weight).x;
  float w1 = READ_weight1_IMAGE(weight1,sampler_weight,coord_weight).x;
  float w2 = READ_weight2_IMAGE(weight2,sampler_weight,coord_weight).x;

  const float wsum = w0 + w1 + w2 + 1e-30f; // add small epsilon to avoid wsum = 0
  w0 /= wsum;
  w1 /= wsum;
  w2 /= wsum;

  const float  v0 = (float)READ_src0_IMAGE(src0,sampler,coord).x;
  const float  v1 = (float)READ_src1_IMAGE(src1,sampler,coord).x;
  const float  v2 = (float)READ_src2_IMAGE(src2,sampler,coord).x;
  const float res = w0 * v0 + w1 * v1 + w2 * v2;

  WRITE_dst_IMAGE(dst,coord, CONVERT_dst_PIXEL_TYPE(res));
}

