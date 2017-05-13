#version 140

in vec2 pass_uv1;
in vec2 pass_uv2;
in float blend;

out vec4 out_color;

uniform sampler2D particleTexture;


void main(void) {
    vec4 tex1 = texture(particleTexture,pass_uv1); //todo if only one ...
	vec4 tex2 = texture(particleTexture,pass_uv2);
	out_color = mix(tex1, tex2, blend);
	//out_color = vec4(1.0);
}