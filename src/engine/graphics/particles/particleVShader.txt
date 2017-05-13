#version 140

in vec2 position;

in mat4 modelViewMatrix;
in vec4 uv_Offsets;
in float blendFactor;


out vec2 pass_uv1;
out vec2 pass_uv2;
out float blend;

uniform mat4 projectionMatrix;
uniform float numOfRows;

void main(void){
    vec2 uv = position + vec2(0.5,0.5);
    uv.y = 1.0 - uv.y;
    uv /= numOfRows;
    pass_uv1 = uv + uv_Offsets.xy;
    pass_uv2 = uv + uv_Offsets.zw;
    blend = blendFactor; //todo
	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);
}