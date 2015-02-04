#ifdef GL_ES
    precision mediump float;
#endif

uniform float time;
varying vec2 pos;

vec3 sky_top_color = vec3( 90.0 / 255.0, 200.0 / 255.0, 190.0 / 255.0);
const vec3 sky_bot_color = vec3(235.0 / 255.0, 235.0 / 255.0, 215.0 / 255.0);
const vec3 hill_1_color  = vec3(170.0 / 255.0, 210.0 / 255.0, 205.0 / 255.0);
const vec3 hill_2_color  = vec3(150.0 / 255.0, 190.0 / 255.0, 185.0 / 255.0);
const vec3 hill_3_color  = vec3(110.0 / 255.0, 142.0 / 255.0, 135.0 / 255.0);

uniform float hill_3_amplitude;
uniform float hill_3_phase;
uniform float hill_2_amplitude;
uniform float hill_2_phase;
uniform float hill_1_amplitude;
uniform float hill_1_phase;

void main() {
	float hill_1 = sin(pos.x * 15.0 + hill_1_phase) * hill_1_amplitude + 0.775;
	float hill_2 = sin(pos.x * 15.0 + hill_2_phase) * hill_2_amplitude + 0.725;
	float hill_3 = sin(pos.x * 15.0 + hill_3_phase) * hill_3_amplitude + 0.68;

	vec3 color = mix(sky_bot_color, sky_top_color, smoothstep(hill_1, 1.0, pos.y));
	color = mix(color, hill_1_color, smoothstep(hill_1 + 0.0027, hill_1, pos.y));
	color = mix(color, hill_2_color, smoothstep(hill_2 + 0.0027, hill_2, pos.y));
	gl_FragColor = vec4(mix(color, hill_3_color, smoothstep(hill_3 + 0.0027, hill_3, pos.y)), 1.0);
}