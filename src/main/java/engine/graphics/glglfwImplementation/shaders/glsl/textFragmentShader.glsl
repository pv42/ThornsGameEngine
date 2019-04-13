#version 300 es

precision mediump float;

in vec2 uv;

out vec4 out_Color;

uniform vec4 textColor;
uniform sampler2D textTexture;

void main() {
    vec4 texValue = texture(textTexture, uv);
    out_Color = texValue.w * textColor;
}