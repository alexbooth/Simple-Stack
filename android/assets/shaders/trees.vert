#ifdef GL_ES
    precision mediump float;
#endif

attribute vec4 a_position;
uniform mat4 u_projTrans;

void main() {
    gl_Position = u_projTrans * a_position;
}