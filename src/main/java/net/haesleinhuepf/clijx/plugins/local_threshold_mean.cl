__const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;

__kernel void local_threshold_mean (  	IMAGE_src_TYPE  src,
                           				IMAGE_srcMean_TYPE  srcMean,
                           				IMAGE_dst_TYPE  dst,
                           				const float c_value
                     			 )
{

  const int x = get_global_id(0);
  const int y = get_global_id(1);
  const int z = get_global_id(2);

  float value = (float)READ_IMAGE(src, sampler, POS_src_INSTANCE(x, y, z, 0)).x;
  float mean = (float)READ_IMAGE(srcMean, sampler, POS_src_INSTANCE(x, y, z, 0)).x;
   
  float res = 1.0f;
  if(value <= (mean - c_value))
    res = 0.0;
    
  	  	
  IMAGE_dst_PIXEL_TYPE out = CONVERT_dst_PIXEL_TYPE( res );
  WRITE_IMAGE(dst, POS_src_INSTANCE(x, y, z, 0), out);
}
