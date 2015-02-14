#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 vTexCoord;
uniform vec3 color;

void main() {
   // float aa = 1.0 - vTexCoord.y;
   // gl_FragColor = vec4(color, smoothstep(0.0,0.6, aa));
   gl_FragColor = vec4(color, 1.0);
}