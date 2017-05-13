#version 400

in vec3 uv;

out vec4 out_Color;

uniform samplerCube cubeMap;
uniform vec3 fogColor;
const float useFog = 0;
const float lwrLimit = 0.0;
const float uprLimit = 30.0;

void main(void) {
    vec4 tColor = texture(cubeMap, uv);
    if(useFog > 0.5) {
        float factor = (uv.y - lwrLimit) / (uprLimit - lwrLimit);
        factor = clamp(factor, 0.0, 1.0);
        out_Color = mix(vec4(fogColor, 1.0), tColor, factor);
    } else {
        out_Color = tColor;
    }
}