#version 300 es

in vec4 position;

out vec2 uv;

uniform vec4 glyphUV;
uniform vec4 targetPos;

void main() {
    if(position.x < 0.0) {
        if(position.y < 0.0) {
            gl_Position = vec4(targetPos.xy,0,1);
            uv = glyphUV.xw;
        } else {
            gl_Position = vec4(targetPos.xw,0,1);
            uv = glyphUV.xy;
        }
    } else {
        if(position.y < 0.0) {
            gl_Position = vec4(targetPos.zy,0,1);
            uv = glyphUV.zw;
        } else {
            gl_Position = vec4(targetPos.zw,0,1);
            uv = glyphUV.zy;
        }
    }
}