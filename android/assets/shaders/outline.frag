#ifdef GL_ES
    precision mediump float;
#endif
#extension GL_OES_standard_derivatives : enable

varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;

const  vec3 OutlineColor = vec3(0.0, 0.0, 0.0);
const  float SmoothCenter = 0.3;
const  float OutlineCenter = 0.6;

void main(void)
{
     vec4 color = texture2D(u_texture, v_texCoord);
     float distance = color.a;
     float smoothWidth = fwidth(distance);
 	 float mu = smoothstep(OutlineCenter - smoothWidth, OutlineCenter + smoothWidth, distance);
     float alpha = smoothstep(SmoothCenter - smoothWidth, SmoothCenter + smoothWidth, distance);
     vec3 rgb = mix(OutlineColor, v_color.rgb, mu);

    gl_FragColor = vec4(rgb, alpha);
}