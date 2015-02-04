#ifdef GL_ES
    precision mediump float;
#endif

attribute vec4 a_position;
attribute vec2 a_texCoord0;
//attribute vec4 a_color;

uniform mat4 u_projTrans;
uniform vec4 color;
varying vec2 vTexCoord;
//varying vec4 color;

void main() {
   // color = a_color;
    vTexCoord = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}