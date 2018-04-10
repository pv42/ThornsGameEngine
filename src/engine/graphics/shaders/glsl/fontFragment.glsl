#version 330

in vec2 pass_uv;

out vec4 out_color;

uniform vec3 color;
uniform float edge;
uniform vec3 borderColor;
uniform sampler2D fontAtlas;
uniform float borderWidth = 0.6;

const float width = 0.5;

//const float borderWidth = 0.6;
const float borderEdge = 0.1;
const vec2 borderOffset = vec2(-0.000,-0.000);
//const vec3 borderColor = vec3(0.3,0.4,0.3);

vec4 df() {
    float distance = 1.0 - texture(fontAtlas,pass_uv).a;
    float alpha = 1.0 - smoothstep(width,width + edge,distance);

    float borderDistance = 1.0 - texture(fontAtlas,pass_uv + borderOffset).a;
    float borderAlpha = 1.0 - smoothstep(borderWidth,borderWidth + borderEdge,borderDistance);

    float overallAlpha = alpha + (1-alpha) * borderAlpha;
    vec3 overallColor = mix(borderColor, color, alpha/overallAlpha);
    return vec4(overallColor,overallAlpha);
}
void main(void){
    //out_color = vec4(0,1,0,.5);
    //out_color = texture(fontAtlas,pass_uv);
    out_color = df();
}